package com.ks.minio.test;

import com.alibaba.fastjson2.JSON;
import com.ks.minio.bean.MinioBucket;
import com.ks.minio.bean.MinioFile;
import com.ks.minio.util.MinioClientUtils;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;

public class MinioFileObjectTest {
    private MinioClient minioClient;

    @BeforeEach
    public void initMinioClient() {
        minioClient = MinioClient.builder().endpoint("http://127.0.0.1:9000").
                credentials("kyle-minio", "hackyle-minio").build();
    }

    /**
     * 测试文件
     */
    @Test
    public void testFileUpload() throws Exception {
        //IDEA中还是以工程的根目录为相对文件的根目录
        File file = new File("datum/aa.txt");

        System.out.println("文件的绝对路径：" + file.getAbsolutePath());

        FileInputStream inputStream = new FileInputStream(file);

        MinioFile minioFile = new MinioFile();
        minioFile.setFilePath("/2022/07/aa.txt");

        minioFile.setContentType("text/plain");
        minioFile.setFileSize(file.length());
        minioFile.setInputStream(inputStream);

        MinioBucket minioBucket = new MinioBucket(minioClient, BucketNameEnum.HELLO.getBucketName());

        String path = MinioClientUtils.createFileObject(minioBucket, minioFile);
        System.out.println(path);
    }


    @Test
    public void testFileDelete() throws Exception {
        MinioBucket minioBucket = new MinioBucket(minioClient, BucketNameEnum.HELLO.getBucketName());

        boolean delete = MinioClientUtils.deleteFileObject(minioBucket, "/2022/07/aa.txt");
        System.out.println(delete);
    }

    @Test
    public void testObtainFileObject() throws Exception {
        MinioBucket minioBucket = new MinioBucket(minioClient, BucketNameEnum.HELLO.getBucketName());

        String fileObjectName = "/2022/akbar-erabiyan-fox-bg.jpg";
        MinioFile minioFile = MinioClientUtils.obtainFileObject(minioBucket, fileObjectName);

        File file = new File("datum/aaa.jpg");
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
        //byte[] buffer = new byte[2048];
        //while((objectResponse.read(buffer) > 0)) {
        //    outputStream.write(buffer, 0, buffer.length);
        //}
        byte[] allBytes = minioFile.getInputStream().readAllBytes();
        outputStream.write(allBytes);
        outputStream.flush();
        outputStream.close();
    }

    @Test
    public void testObtainFileObjectBySteps() throws Exception {
        File file = new File("datum/bbb.jpg");
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));

        String fileObjectName = "/2022/akbar-erabiyan-fox-bg.jpg";
        MinioBucket minioBucket = new MinioBucket(minioClient, BucketNameEnum.HELLO.getBucketName());

        MinioFile minioFile = MinioClientUtils.fileObjectAttributes(minioBucket, fileObjectName);
        System.out.println(minioFile.getFileSize()); //230848

        minioFile = MinioClientUtils.obtainFileObjectBySteps(minioBucket, fileObjectName, 0, 70000);
        byte[] allBytes = minioFile.getInputStream().readAllBytes();
        outputStream.write(allBytes);
        System.out.println(JSON.toJSONString(minioFile));

        minioFile = MinioClientUtils.obtainFileObjectBySteps(minioBucket, fileObjectName, 70000, 230848);
        allBytes = minioFile.getInputStream().readAllBytes();
        outputStream.write(allBytes);
        System.out.println(JSON.toJSONString(minioFile));


        outputStream.flush();
        outputStream.close();
    }


    @Test
    public void testFilesLoadByPath() throws Exception {
        MinioBucket minioBucket = new MinioBucket(minioClient, BucketNameEnum.HELLO.getBucketName());
        List<String> fileList = MinioClientUtils.obtainFileObjectByPrefixPath(minioBucket, "/2022/01", true);
        for (String s : fileList) {
            System.out.println(s);
        }
    }

    @Test
    public void testFileCopy() throws Exception {
        MinioBucket minioBucket = new MinioBucket(minioClient, BucketNameEnum.HELLO.getBucketName());

        MinioBucket targetMinioBucket = new MinioBucket(minioBucket.getMinioClient(), BucketNameEnum.HELLO.getBucketName());

        ObjectWriteResponse objectWriteResponse = MinioClientUtils.fileObjectCopy(minioBucket, "/2022/akbar-erabiyan-fox-bg.jpg",
                targetMinioBucket, "/2022/07/aaaa.jpg");

        System.out.println(objectWriteResponse.etag());
    }

    @Test
    public void testFileAttributes() {
        MinioBucket minioBucket = new MinioBucket(minioClient, BucketNameEnum.HELLO.getBucketName());

        try {
            MinioFile minioFile = MinioClientUtils.fileObjectAttributes(minioBucket, "/2022/akbar-erabiyan-fox-bg.jpg");

            System.out.println(JSON.toJSONString(minioFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFileExist() {
        MinioBucket minioBucket = new MinioBucket(minioClient, BucketNameEnum.HELLO.getBucketName());

        try {
            //boolean hello = MinioClientUtils.fileExist(minioBucket, "/path/to/bbbb.txt");
            boolean hello = MinioClientUtils.fileObjectExist(minioBucket, "text01.txt");

            System.out.println(hello);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
