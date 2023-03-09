package com.study.media.api;

import com.study.base.model.RestResponse;
import com.study.media.model.dto.UploadFileParamsDto;
import com.study.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Api(value = "视频文件上传", tags = "视频文件上传")
@RestController
@Slf4j
public class BigFileController {

    @Autowired
    private MediaFileService mediaFileService;

    @ApiOperation(value = "文件上传前检查文件")
    @PostMapping("/upload/checkfile")
    public RestResponse<Boolean> checkFile(@RequestParam("fileMd5") String fileMd5) {
        return mediaFileService.checkFile(fileMd5);
    }


    @ApiOperation(value = "分块文件上传前的检测")
    @PostMapping("/upload/checkchunk")
    public RestResponse<Boolean> checkChunk(@RequestParam("fileMd5") String fileMd5,
                                            @RequestParam("chunk") int chunk) {
        return mediaFileService.checkChunk(fileMd5, chunk);
    }

    @ApiOperation(value = "上传分块文件")
    @PostMapping("/upload/uploadchunk")
    public RestResponse<Boolean> uploadChunk(@RequestParam("file") MultipartFile file,
                                          @RequestParam("fileMd5") String fileMd5,
                                          @RequestParam("chunk") int chunk) throws Exception {
        //创建一个临时文件
        File tempFile = File.createTempFile("minio", ".temp");
        file.transferTo(tempFile);

        RestResponse<Boolean> res = mediaFileService.uploadChunk(fileMd5, chunk, tempFile.getAbsolutePath());
        if (tempFile.delete()) log.debug("删除成功");
        return res;
    }

    @ApiOperation(value = "合并文件")
    @PostMapping("/upload/mergechunks")
    public RestResponse<Boolean> mergeChunks(@RequestParam("fileMd5") String fileMd5,
                                    @RequestParam("fileName") String fileName,
                                    @RequestParam("chunkTotal") int chunkTotal) {
        Long companyId = 123L;
        UploadFileParamsDto fileParamsDto = new UploadFileParamsDto();
        fileParamsDto.setFilename(fileName);
        fileParamsDto.setTags("视频文件");
        fileParamsDto.setFileType("001002");

        return mediaFileService.mergeChunks(companyId, fileMd5, chunkTotal,fileParamsDto);
    }

}
