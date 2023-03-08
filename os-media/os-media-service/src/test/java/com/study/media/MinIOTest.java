package com.study.media;

import cn.hutool.core.date.CalendarUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.j256.simplemagic.ContentInfoUtil;
import com.study.base.model.PageParams;
import com.study.media.model.dto.QueryMediaParamsDto;
import com.study.media.service.MediaFileService;
import io.minio.*;
import io.minio.errors.*;
import io.swagger.annotations.ApiOperation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 测试MinIO
 */
@SpringBootTest
public class MinIOTest {

    @Autowired
    private MediaFileService mediaFileService;

    private final MinioClient minioClient =
            MinioClient.builder()
                    .endpoint("http://localhost:9000")
                    .credentials("minioadmin", "minioadmin")
                    .build();

    //上传文件
    @Test
    public void testUpload() throws
            IOException,
            ServerException,
            InsufficientDataException,
            ErrorResponseException,
            NoSuchAlgorithmException,
            InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {
        minioClient.uploadObject(
                UploadObjectArgs.builder()
                        .bucket("testbuckets")
                        .filename("C:\\Users\\asus\\Desktop\\废柴狐阿桔\\music\\By Your Side-Beachwood Sparks.mp3")
                        .object("by your side.mp3")
                        .contentType(ContentInfoUtil.findExtensionMatch(".mp3").getMimeType())
                        .build()
        );
    }

    //删除文件
    @Test
    public void testDelete() throws ServerException,
            InsufficientDataException,
            ErrorResponseException,
            IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket("testbuckets")
                        .object("by your side.mp3")
                        .build()
        );
    }

    @Test
    public void testDownload() throws
            ServerException,
            InsufficientDataException,
            ErrorResponseException,
            IOException,
            NoSuchAlgorithmException,
            InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {
        FilterInputStream buckets = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket("testbuckets")
                        .object("by your side.mp3")
                        .build()
        );

        FileOutputStream fos = new FileOutputStream("D:\\start-project\\online-study\\os-media\\os-media-service\\src\\test\\java\\com\\study\\media\\music.mp3");
        IoUtil.copy(buckets, fos);

        String s = DigestUtil.md5Hex(buckets);
        String s1 = DigestUtil.md5Hex(new File("D:\\start-project\\online-study\\os-media\\os-media-service\\src\\test\\java\\com\\study\\media\\music.mp3"));
        if (s.equals(s1)) System.out.println("下载成功");
    }

    @Test
    public void testDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/");
        System.out.println(sdf.format(DateUtil.date()));
    }

    @Test
    public void testQueryMediaFiles(){
        PageParams pageParams = new PageParams(1, 5);
        QueryMediaParamsDto queryMediaParamsDto = new QueryMediaParamsDto();
        queryMediaParamsDto.setFilename("6ad24a762f67c18f61966c1b8c55abe6");
        queryMediaParamsDto.setFileType("001002");
        queryMediaParamsDto.setAuditStatus("002003");
        System.out.println(mediaFileService.queryMediaFiles(1232141425L, pageParams, queryMediaParamsDto));
    }
}
