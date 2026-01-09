package com.ks.minio.service;

import com.ks.minio.bean.MinioBucket;
import com.ks.minio.bean.MinioFile;
import com.ks.minio.config.MinioConfig;
import com.ks.minio.util.MinioClientUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MinioService {
    @Autowired
    private MinioConfig minioConfig;

    public List<String> fileUpload(MultipartFile[] multipartFiles) throws Exception {
        MinioBucket minioBucket = new MinioBucket(minioConfig.minioClient(), minioConfig.getBucketName());
        MinioClientUtils.createBucket(minioBucket);

        List<String> fileObjects = new ArrayList<>();

        for (MultipartFile file : multipartFiles) {
            InputStream inputStream = file.getInputStream(); //得到文件流
            String fileName = file.getOriginalFilename(); //文件名
            String contentType = file.getContentType();  //类型

            MinioFile minioFile = new MinioFile();
            minioFile.setBucketName(minioBucket.getBucketName());
            minioFile.setPrefixPath(generatePrefixByDate());
            minioFile.setFilePath(generatePrefixByDate() + generateFileNameByStamp(fileName));
            minioFile.setFileSize(file.getSize());
            minioFile.setContentType(contentType);
            minioFile.setInputStream(inputStream);

            MinioClientUtils.createFileObject(minioBucket, minioFile);
            String url = minioConfig.getFileObjDomain() + minioConfig.getBucketName() + minioFile.getFilePath();
            fileObjects.add(url);
        }

        return fileObjects;
    }


    /**
     * 存储于MinIO中的文件命名：时间戳
     * @param originalFileName 原始名字
     * @return 时间戳转换后的新名字
     */
    private String generateFileNameByStamp(String originalFileName) {
        if(originalFileName == null || "".equals(originalFileName.trim())) {
            throw new IllegalArgumentException("文件名为空");
        }
        return UUID.randomUUID().toString().replaceAll("-", "") + originalFileName.substring(originalFileName.lastIndexOf("."));
    }

    /**
     * 根据当时日期生成文件前缀的路径：/年/月/
     */
    private String generatePrefixByDate() {
        LocalDate localDate = LocalDate.now();
        int month = localDate.getMonthValue();
        return String.format("/%s/%s/", localDate.getYear(), month < 10 ? "0"+month: String.valueOf(month));
    }


    public boolean fileDelete(String fileURL) throws Exception{
        MinioBucket minioBucket = new MinioBucket(minioConfig.minioClient(), minioConfig.getBucketName());

        String fileObjDomain = minioConfig.getFileObjDomain();
        String bucketName = minioBucket.getBucketName();

        //截取出文件对象的Key
        //例如：http://127.0.0.1:9000/hello/2022/12/aaa.png，截取后：/2022/12/aaa.png
        String fileObj = fileURL.substring(fileObjDomain.length() + 1 + bucketName.length());

        return MinioClientUtils.deleteFileObject(minioBucket, fileObj);
    }

}
