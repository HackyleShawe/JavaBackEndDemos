package com.ks.minio.bean;

import io.minio.MinioClient;
import io.minio.messages.LifecycleRule;

import java.util.List;

/**
 * MinIO桶对象：只能含括桶的相关属性
 * 注意：MinioClient、bucketName通过构造器构成后不得修改，因此这两个属性不能有setter
 */
public class MinioBucket {
    /** MinIO 客户端； */
    private MinioClient minioClient;
    /** 存储桶 */
    private String bucketName;
    /** 区域 */
    private String region;

    /** 生命周期 */
    List<LifecycleRule> lifecycleRuleList;


    public MinioBucket(MinioClient minioClient) {
        if(minioClient == null) {
            throw new IllegalArgumentException("minioClient为空");
        }
        this.minioClient = minioClient;
    }
    /**
     * 一般情况：构造本个对象强制要求有MinioClient和bucketName
     */
    public MinioBucket(MinioClient minioClient, String bucketName) {
        if(minioClient == null || bucketName == null || "".equals(bucketName.trim())) {
            throw new IllegalArgumentException("入参有空值：minioClient="+minioClient+"，bucketName="+bucketName);
        }
        this.minioClient = minioClient;
        this.bucketName = bucketName;
    }

    public MinioClient getMinioClient() {
        return minioClient;
    }

    public String getBucketName() {
        return bucketName;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public List<LifecycleRule> getLifecycleRuleList() {
        return lifecycleRuleList;
    }

    public void setLifecycleRuleList(List<LifecycleRule> lifecycleRuleList) {
        this.lifecycleRuleList = lifecycleRuleList;
    }


}
