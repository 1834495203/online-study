package com.study.media.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.study.base.exception.OSException;
import com.study.base.model.PageParams;
import com.study.base.model.PageResult;
import com.study.base.model.RestResponse;
import com.study.base.utils.StringUtil;
import com.study.media.mapper.MediaFilesMapper;
import com.study.media.model.dto.QueryMediaParamsDto;
import com.study.media.model.dto.UploadFileParamsDto;
import com.study.media.model.dto.UploadFileResultDto;
import com.study.media.model.po.MediaFiles;
import com.study.media.service.MediaFileService;
import io.minio.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author GLaDOS
 * @version 1.0
 */
 @Service
 @Slf4j
public class MediaFileServiceImpl implements MediaFileService {

    @Autowired
    private MediaFilesMapper mediaFilesMapper;

    @Autowired
    private MediaFileService mediaFileServiceProxy;

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket.files}")
    private String mediaFiles;

    @Value("${minio.bucket.videofiles}")
    private String video;

    @Override
    public PageResult<MediaFiles> queryMediaFiles(Long companyId,
                                                  PageParams pageParams,
                                                  QueryMediaParamsDto queryMediaParamsDto) {
        //构建查询条件对象
        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();

        //分页对象
        Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        // 查询数据内容获得结果
        Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
        // 获取数据列表
        List<MediaFiles> list = pageResult.getRecords();
        // 获取数据总数
        long total = pageResult.getTotal();
        // 构建结果集
        return new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());
    }

    @Override
    public UploadFileResultDto uploadFile(Long companyId,
                                          UploadFileParamsDto fileParamsDto,
                                          String localFilePath, String objectName) {

        //获取文件名
        String filename = fileParamsDto.getFilename();
        //获取后缀
        String ex = filename.substring(filename.lastIndexOf("."));
        //得到mimeType
        String mimeType = getMimeType(ex);
        //获取文件的md5
        String fileMd5 = getFileMd5(new File(localFilePath));
        //将文件上传至minio
        if (StringUtil.isEmpty(objectName)){
            objectName = getFilepathByDate()+fileMd5+ex;
        }

        //通过依赖注入使其能被动态代理, 可以事务回滚
        MediaFiles file2Db = mediaFileServiceProxy.uploadMediaFile2Db(fileMd5,
                fileParamsDto, companyId, this.mediaFiles,
                objectName);
        if (file2Db == null) OSException.cast("文件上传至数据库失败");

        boolean b = uploadFile2Min(mediaFiles, objectName, localFilePath, mimeType);
        if (!b) OSException.cast("文件上传失败");

        //返回准备的对象
        UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
        BeanUtil.copyProperties(file2Db, uploadFileResultDto);
        return uploadFileResultDto;
    }

    @Override
    @Transactional
    public MediaFiles uploadMediaFile2Db(String fileMd5, UploadFileParamsDto fileParamsDto,
                                   Long companyId, String bucket, String filePath){
        //将文件数据保存到数据库
        MediaFiles media;
        if ((media = mediaFilesMapper.selectById(fileMd5)) == null) {
            media = new MediaFiles();
            BeanUtil.copyProperties(fileParamsDto, media);
            media.setCompanyId(companyId); // 机构id
            media.setBucket(bucket); // 桶
            media.setId(fileMd5); // 文件id
            media.setFilePath(filePath); // 文件路径
            media.setFileId(fileMd5); // 文件名
            media.setUrl("/"+bucket+"/"+filePath); // url
            media.setCreateDate(LocalDateTime.now()); // 当前时间
            media.setStatus("1"); // 文件状态
            media.setAuditStatus("00203"); //审核状态
            //插入数据库
            int insert = mediaFilesMapper.insert(media);
            if (insert <= 0){
                log.error("文件{}信息保存失败", fileParamsDto.getFilename());
            }
        }
        return media;
    }

    @Override
    public RestResponse<Boolean> checkFile(String fileMd5) {

        //先查询数据库
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles != null){
            String bucket = mediaFiles.getBucket();
            String filePath = mediaFiles.getFilePath();

            //如果数据库存在再查询minio
            GetObjectArgs objectArgs = GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(filePath)
                    .build();
            try {
                GetObjectResponse object = minioClient.getObject(objectArgs);
                //如果文件存在
                if (object != null) return RestResponse.success(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //文件不存在
            return RestResponse.success(false);
        }
        return RestResponse.success(false);
    }

    @Override
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex) {

        //分块路径为md5前两位为两个目录, chunk存储分块文件
        String fileFolderPath = getChunkFileFolderPath(fileMd5);

        //如果数据库存在再查询minio
        GetObjectArgs objectArgs = GetObjectArgs.builder()
                .bucket(video)
                .object(fileFolderPath+chunkIndex)
                .build();

        try {
            GetObjectResponse object = minioClient.getObject(objectArgs);
            //文件存在
            if (object != null) return RestResponse.success(true);
        } catch (Exception e) {
            e.printStackTrace();
            return RestResponse.success(false);
        }

        return RestResponse.success(false);
    }

    @Override
    public RestResponse<Boolean> uploadChunk(String fileMd5, int chunk, String localFilePath) {
        //将分块文件上传到minio
        String filePath = getChunkFileFolderPath(fileMd5) + chunk;
        String mimeType = getMimeType(null);
        boolean b = uploadFile2Min(video, filePath, localFilePath, mimeType);
        if (!b) return RestResponse.validFail(false, "上传失败");
        return RestResponse.success(true);
    }

    @Override
    public RestResponse<Boolean> mergeChunks(Long companyId, String fileMd5, int chunkTotal,
                                             UploadFileParamsDto uploadFileParamsDto) {
        //找到分块文件调用minio的sdk进行文件合并

        //分块文件的目录
        String folderPath = getChunkFileFolderPath(fileMd5);

        //找到所有的分块文件
        List<ComposeSource> sources = Stream.iterate(0, i -> ++i)
                .limit(chunkTotal)
                .map(i -> ComposeSource.builder()
                        .bucket(video)
                        .object(folderPath + i)
                        .build())
                .collect(Collectors.toList());

        String ext = uploadFileParamsDto.getFilename().substring(uploadFileParamsDto.getFilename().lastIndexOf("."));
        //合并后文件的objectName信息
        String objectName = getFilePathByMd5(fileMd5, ext);

        try {
            minioClient.composeObject(
                    ComposeObjectArgs.builder()
                            .bucket(video)
                            .sources(sources)
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            e.printStackTrace();
            log.error("文件{}出错, 错误信息: {}", objectName, e.getMessage());
            return RestResponse.validFail(false, "合并文件异常");
        }

        //校验合并后的文件与源文件是否一致
        //先下载合并后的文件
        File file = downloadFileFromMinIO(video, objectName);
        if (file != null)
            try(FileInputStream fis = new FileInputStream(file)) {
                //计算合并后文件的md5
                String mergedMd5 = DigestUtil.md5Hex(fis);
                //比较两者的md5值
                if (!fileMd5.equals(mergedMd5)){
                    log.error("校验文件md5值不一致, 原始文件:{}, 合并文件:{}", fileMd5, mergedMd5);
                }
                uploadFileParamsDto.setFileSize(file.length());
                if (file.delete()) log.debug("文件: {} 删除成功", file.getName());
            } catch (Exception e) {
                e.printStackTrace();
                return RestResponse.validFail(false, "文件校验失败");
            }
        else return RestResponse.validFail(false, "文件为null");

        MediaFiles mediaFiles = mediaFileServiceProxy.uploadMediaFile2Db(fileMd5,
                uploadFileParamsDto, companyId, video, objectName);
        if (mediaFiles == null) return RestResponse.validFail(false, "文件入库失败");

        //清理分块文件
        clearChunkFiles(folderPath, chunkTotal, video);

        return RestResponse.success(true);
    }

    /**
     * 清理分块文件
     * @param chunkFileFolderPath 分块文件路径
     * @param chunkTotal 分块路径总数
     */
    private void clearChunkFiles(String chunkFileFolderPath, int chunkTotal, String bucket){
        Iterable<DeleteObject> objects = Stream.iterate(0, i -> ++i)
                .limit(chunkTotal)
                .map(i -> new DeleteObject(chunkFileFolderPath+i))
                .collect(Collectors.toList());

        RemoveObjectsArgs objectsArgs = RemoveObjectsArgs.builder()
                .bucket(bucket)
                .objects(objects)
                .build();
        Iterable<Result<DeleteError>> results = minioClient.removeObjects(objectsArgs);
        //删除逻辑
        results.forEach(items->{
            try {
                items.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 从minio下载文件
     * @param bucket 桶
     * @param objectName 对象名称
     * @return 下载后的文件
     */
    private File downloadFileFromMinIO(String bucket, String objectName){

        File minioFile;
        FileOutputStream fos = null;

        try {
            FilterInputStream buckets = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .build());
            minioFile = File.createTempFile("minio", ".merge");
            fos = new FileOutputStream(minioFile);
            IoUtil.copy(buckets, fos);
            return minioFile;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (fos != null)
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return null;
    }

    /**
     * 得到合并后文件的地址
     * @param fileMd5 文件id的md5
     * @param fileExt 文件拓展名
     * @return 地址值
     */
    private String getFilePathByMd5(String fileMd5, String fileExt){
        return fileMd5.charAt(0) + "/" + fileMd5.charAt(1) + "/" + fileMd5 + "/" + fileMd5 + fileExt;
    }

    //得到分块文件的目录
    private String getChunkFileFolderPath(String fileMd5) {
        return fileMd5.charAt(0) + "/" + fileMd5.charAt(1) + "/" + fileMd5 + "/" + "chunk" + "/";
    }

    /**
     * 根据拓展名获取mimeType
     * @param ex 文件后缀
     * @return 文件类型
     */
    private String getMimeType(String ex){
        if (ex == null) ex = "";
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(ex);
        if (extensionMatch != null)
            return extensionMatch.getMimeType();
        //为空则返回通用类型 字节流
        return MediaType.APPLICATION_OCTET_STREAM_VALUE;
    }

    /**
     * 文件上传至minio
     * @param bucket 桶
     * @param pathname 文件路径
     * @param name 文件名
     * @param contentType 文件类型
     */
    private boolean uploadFile2Min(String bucket, String pathname, String name,
                                   String contentType) {
        log.debug("pathname: {}", pathname);
        try {
            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(bucket)
                            .filename(name) // 文件在当前系统的路径
                            .object(pathname) // 文件在minio中的路径
                            .contentType(contentType)
                            .build()
            );
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("文件上传失败------>"+e.getMessage());
        }
        return false;
    }

    /**
     * 时间转换为文件路径
     * @return 文件路径
     */
    private String getFilepathByDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/");
        return sdf.format(DateUtil.date());
    }

    /**
     * 获取文件的md5
     * @param file 文件
     * @return md5
     */
    private String getFileMd5(File file){
        try {
            FileInputStream fis = new FileInputStream(file);
            return DigestUtil.md5Hex(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            log.error("文件名转换为md5失败-------->"+e.getMessage());
            return null;
        }
    }
}
