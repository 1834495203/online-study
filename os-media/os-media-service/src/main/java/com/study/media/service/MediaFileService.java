package com.study.media.service;

import com.study.base.model.PageParams;
import com.study.base.model.PageResult;
import com.study.media.model.dto.QueryMediaParamsDto;
import com.study.media.model.dto.UploadFileParamsDto;
import com.study.media.model.dto.UploadFileResultDto;
import com.study.media.model.po.MediaFiles;

/**
 * 媒资文件管理业务类
 * @author GLaDOS
 * @version 1.0
 */
public interface MediaFileService {

    /**
    * 媒资文件查询方法
    * @param pageParams 分页参数
    * @param queryMediaParamsDto 查询条件
    * @return com.study.base.model.PageResult<com.study.media.model.po.MediaFiles>
    * @author GLaDOS
    */
    PageResult<MediaFiles> queryMediaFiles(Long companyId,
                                           PageParams pageParams,
                                           QueryMediaParamsDto queryMediaParamsDto);

    /**
     * 上传文件的接口
     * @param companyId 机构id
     * @param fileParamsDto 文件基本参数
     * @param localFilePath 文件的下载路径
     * @return 前端需要的对象
     */
    UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto fileParamsDto, String localFilePath);

    /**
     * 文件入库
     * @param fileMd5 文件md5名
     * @param fileParamsDto 文件dto
     * @param companyId 机构id
     * @param bucket 桶
     * @param filePath 文件路径
     */
    MediaFiles uploadMediaFile2Db(String fileMd5, UploadFileParamsDto fileParamsDto,
                                         Long companyId, String bucket, String filePath);
}
