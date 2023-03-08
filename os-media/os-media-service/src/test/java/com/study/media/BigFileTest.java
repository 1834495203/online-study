package com.study.media;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

@SpringBootTest
public class BigFileTest {

    @Test
    public void testChunk() throws IOException {
        //源文件
        File file = new File("D:\\test\\苏小柠 (3).mp4");
        //分块文件存储路径
        String chunkPath = "D:\\test\\chunk\\";
        //分块文件大小
        int size = 1024 * 1024;
        int num = (int) Math.ceil(file.length() * 1.0 / size);
        //使用流从源文件读取数据, 向文件中写入数据
        RandomAccessFile r = new RandomAccessFile(file, "r");

        byte[] bytes = new byte[1024];
        for (int i = 0; i < num; i++) {
            File file1 = new File(chunkPath + i);
            //分块文件写入
            RandomAccessFile rw = new RandomAccessFile(file1, "rw");
            int len = -1;

            while ((len = rw.read(bytes)) != -1){
                rw.write(bytes, 0, len);
                if (file1.length() >= size) break;
            }
            rw.close();
        }
        r.close();
    }
}
