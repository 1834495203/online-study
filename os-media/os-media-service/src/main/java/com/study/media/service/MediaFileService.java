package com.study.media.service;

import com.study.base.model.PageParams;
import com.study.base.model.PageResult;
import com.study.media.model.dto.QueryMediaParamsDto;
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
}
