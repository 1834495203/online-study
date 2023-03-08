package com.study.media.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.study.base.exception.OSException;
import com.study.base.model.PageParams;
import com.study.base.model.PageResult;
import com.study.media.mapper.MediaFilesMapper;
import com.study.media.model.dto.QueryMediaParamsDto;
import com.study.media.model.dto.UploadFileParamsDto;
import com.study.media.model.dto.UploadFileResultDto;
import com.study.media.model.po.MediaFiles;
import com.study.media.service.MediaFileService;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

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
                                          String localFilePath) {

        //获取文件名
        String filename = fileParamsDto.getFilename();
        //获取后缀
        String ex = filename.substring(filename.lastIndexOf("."));
        //得到mimeType
        String mimeType = getMimeType(ex);
        //获取文件的md5
        String fileMd5 = getFileMd5(new File(localFilePath));
        //将文件上传至minio
        String path;

        //通过依赖注入使其能被动态代理, 可以事务回滚
        MediaFiles file2Db = mediaFileServiceProxy.uploadMediaFile2Db(fileMd5,
                fileParamsDto, companyId, this.mediaFiles,
                (path=getFilepathByDate()+fileMd5+ex));
        if (file2Db == null) OSException.cast("文件上传至数据库失败");

        boolean b = uploadFiles(mediaFiles, path, localFilePath, mimeType);
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
    private boolean uploadFiles(String bucket, String pathname, String name,
                                String contentType) {
        log.debug("pathname: {}", pathname);
        try {
            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(bucket)
                            .filename(name)
                            .object(pathname)
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
