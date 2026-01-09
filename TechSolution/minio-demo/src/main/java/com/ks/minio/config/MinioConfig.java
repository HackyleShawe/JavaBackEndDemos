package com.ks.minio.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 从YML配置文件中读取配置数据
 * 方案1：@ConfigurationProperties(prefix = "minio")，指定属性及其getter/setter
 * 方案2：直接在属性上加注解：@Value("minio.endpoint")
 */
@Configuration
public class MinioConfig {
    /** 外网MinIO URL */
    @Value("${minio.endpoint}")
    private String endpoint;

    /** 内网MinIO URL */
    @Value("${minio.endpoint-in}")
    private String endpointIn;

    /** MinIO用户名 */
    @Value("${minio.access-key}")
    private String accessKey;

    /** MinIO密码 */
    @Value("${minio.secret-key}")
    private String secretKey;

    /** 本个项目所用的桶名 */
    @Value("${minio.bucket-name}")
    private String bucketName;

    /** 文件对象对外提供的域 */
    @Value("${minio.file-obj-domain}")
    private String fileObjDomain;

    /**
     * 把MinioClient交给Spring管理
     */
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder().endpoint(endpoint).
                credentials(accessKey, secretKey).build();
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getEndpointIn() {
        return endpointIn;
    }

    public void setEndpointIn(String endpointIn) {
        this.endpointIn = endpointIn;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getFileObjDomain() {
        return fileObjDomain;
    }

    public void setFileObjDomain(String fileObjDomain) {
        this.fileObjDomain = fileObjDomain;
    }
}
