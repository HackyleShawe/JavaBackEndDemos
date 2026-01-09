package com.ks.minio.test;

public enum BucketNameEnum {
    HELLO("hello", "hello"),
    TEST("test", "test"),

    BLOG_HACKYLE_COM("blog", "博客域(blog.hackyle.com)的文件存储桶"),
    CODE_HACKYLE_COM("code", "代码片段域(code.hackyle.com)的文件存储桶");


    private final String bucketName;
    private final String desc;

    BucketNameEnum(String bucketName, String desc) {
        this.bucketName = bucketName;
        this.desc = desc;
    }

    public String getBucketName() {
        return bucketName;
    }

    public String getDesc() {
        return desc;
    }
}
