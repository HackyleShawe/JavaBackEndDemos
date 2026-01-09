package com.ks.minio.test;

import com.alibaba.fastjson.JSON;
import com.ks.minio.bean.MinioBucket;
import com.ks.minio.util.MinioClientUtils;
import io.minio.MinioClient;
import io.minio.messages.Bucket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class MinioBucketTest {
    private MinioClient minioClient;

    @BeforeEach
    public void initMinioClient() {
        minioClient = MinioClient.builder().endpoint("http://127.0.0.1:9000").
                credentials("kyle-minio", "hackyle-minio").build();
    }

    /**
     * 测试Bucket
     */
    @Test
    public void testBucketCreate() {
        try {
            MinioBucket minioBucket = new MinioBucket(minioClient, BucketNameEnum.HELLO.getBucketName());

            MinioClientUtils.createBucket(minioBucket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDeleteBucket() {
        try {
            MinioBucket minioBucket = new MinioBucket(minioClient, BucketNameEnum.HELLO.getBucketName());

            MinioClientUtils.deleteBucket(minioBucket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testObtainAllBuckets() {
        try {
            MinioBucket minioBucket = new MinioBucket(minioClient);

            List<Bucket> buckets = MinioClientUtils.obtainAllBuckets(minioBucket);
            System.out.println(JSON.toJSONString(buckets));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
