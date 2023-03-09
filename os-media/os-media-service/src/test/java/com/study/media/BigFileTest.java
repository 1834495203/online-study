package com.study.media;

import cn.hutool.crypto.digest.DigestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

@SpringBootTest
public class BigFileTest {

    @Test
    public void testChunk() throws IOException {
        //源文件
        File file = new File("D:\\test\\Past Lives.mp4");
        //分块文件存储路径
        String chunkPath = "D:\\test\\chunk\\";
        //分块文件大小
        int size = 1024 * 1024 * 5;
        int num = (int) Math.ceil(file.length() * 1.0 / size);
        //使用流从源文件读取数据, 向文件中写入数据
        RandomAccessFile r = new RandomAccessFile(file, "r");

        byte[] bytes = new byte[1024];
        for (int i = 0; i < num; i++) {
            File file1 = new File(chunkPath + i);
            //分块文件写入
            RandomAccessFile rw = new RandomAccessFile(file1, "rw");
            int len;

            while ((len = r.read(bytes)) != -1){
                rw.write(bytes, 0, len);
                if (file1.length() >= size) break;
            }
            rw.close();
        }
        r.close();
    }

    @Test
    public void testMerge() throws IOException {
        //分块文件目录
        File chunkFolder = new File("D:\\test\\chunk");
        //原文件
        File sourceFile = new File("D:\\test\\Past Lives.mp4");
        //合并后的文件
        File mergeFile = new File("D:\\test\\test.mp4");

        //取出所有分块文件
        File[] files = chunkFolder.listFiles();
        //转成list
        assert files != null;
        List<File> fileList = Arrays.asList(files);

        //排序
        fileList.sort(Comparator.comparingInt(o -> Integer.parseInt(o.getName())));

        //合并文件写的流
        RandomAccessFile rw = new RandomAccessFile(mergeFile, "rw");

        byte[] bytes = new byte[1024];
        for (File file : fileList) {
            RandomAccessFile r = new RandomAccessFile(file, "r");
            int len;
            while ((len=r.read(bytes)) != -1){
                rw.write(bytes);
            }
            r.close();
        }
        rw.close();

        FileInputStream mf = new FileInputStream(mergeFile);
        FileInputStream sf = new FileInputStream(sourceFile);
        if (DigestUtil.md5Hex(mf).equals(DigestUtil.md5Hex(sf)))
            System.out.println("成功");
    }

    @Test
    public void testMd5(){
        File file = new File("D:\\test\\Past Lives.mp4");
        System.out.println(DigestUtil.md5Hex(file));
    }
}
