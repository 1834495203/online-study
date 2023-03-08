package com.study.media.api;

import com.study.base.model.PageParams;
import com.study.base.model.PageResult;
import com.study.media.model.dto.QueryMediaParamsDto;
import com.study.media.model.dto.UploadFileParamsDto;
import com.study.media.model.dto.UploadFileResultDto;
import com.study.media.model.po.MediaFiles;
import com.study.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * 媒资文件管理接口
 * @author GLaDOS
 */
 @Api(value = "媒资文件管理接口",tags = "媒资文件管理接口")
 @RestController
 @Slf4j
public class MediaFilesController {

    @Autowired
    private MediaFileService mediaFileService;

    @ApiOperation("媒资列表查询接口")
    @PostMapping("/files")
    public PageResult<MediaFiles> list(PageParams pageParams,
                                        @RequestBody QueryMediaParamsDto queryMediaParamsDto){
        Long companyId = 1232141425L;
        return mediaFileService.queryMediaFiles(companyId,pageParams,queryMediaParamsDto);
    }

    @ApiOperation("上传图片")
    @RequestMapping(value = "/upload/courseFile",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            method = RequestMethod.POST)
    public UploadFileResultDto uploadImage(@RequestPart("file") MultipartFile file) throws IOException {

        UploadFileParamsDto fileParamsDto = new UploadFileParamsDto();
        fileParamsDto.setFilename(file.getOriginalFilename()); // 文件名称
        fileParamsDto.setFileSize(file.getSize()); // 文件大小
        fileParamsDto.setFileType("001001"); // 文件类型

        //创建一个临时文件
        File tempFile = File.createTempFile("minio", ".temp");
        file.transferTo(tempFile);
        //文件路径
        String absolutePath = tempFile.getAbsolutePath();
        log.debug("文件路径为: {}", absolutePath);

        // 调用service上传图像
        Long companyId = 22L;
        UploadFileResultDto resultDto = mediaFileService.uploadFile(companyId, fileParamsDto, absolutePath);
        if (tempFile.delete()) log.debug("已删除临时文件: {}", tempFile.getName());
        return resultDto;
    }
}
