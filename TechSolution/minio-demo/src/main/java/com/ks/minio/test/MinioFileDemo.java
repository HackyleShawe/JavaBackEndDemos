package com.ks.minio.test;

import com.ks.minio.bean.MinioBucket;
import io.minio.ListObjectsArgs;
import io.minio.PutObjectArgs;
import io.minio.Result;
import io.minio.StatObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import io.minio.messages.Item;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * 操作文件对象的一些代码示例
 */
public class MinioFileDemo {

    /**
     * 创建路径，但不创建文件：可用性不高，因为在创建文件时，它会自动建立对应的路径
     * @param minioBucket 桶
     * @param path 路径
     * @return 路径名称，格式：/桶名/path/bb/bb/
     */
    public static String createFileObjectPrefixPath(MinioBucket minioBucket, String path)  throws IOException,
            InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException,
            ServerException, InternalException, XmlParserException, ErrorResponseException {
        //如果还没有创建桶，则创建
        //if(!bucketExist(minioBucket)) {
        //    createBucket(minioBucket);
        //}

        PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .bucket(minioBucket.getBucketName()) //桶名
                .object(path) //路径
                .stream(new ByteArrayInputStream(new byte[]{}), 0, -1)
                .build();
        minioBucket.getMinioClient().putObject(putObjectArgs);

        path = path.startsWith("/") ? path : "/"+path;
        path = path.endsWith("/") ? path : path+"/";
        return "/" + minioBucket.getBucketName() + path;
    }


    /**
     * 文件夹是否存在
     * @param minioBucket 桶配置的配置信息
     * @param folderName 要判断的文件夹名
     * @param recursive 是否递归查找所有子文件夹
     * @return 是否存在
     */
    public static boolean folderExist(MinioBucket minioBucket, String folderName, boolean recursive) {
        boolean exist = false;
        try {
            ListObjectsArgs listObjectsArgs = ListObjectsArgs.builder().bucket(minioBucket.getBucketName()).prefix(folderName).recursive(recursive).build();
            Iterable<Result<Item>> results = minioBucket.getMinioClient().listObjects(listObjectsArgs);
            for (Result<Item> result : results) {
                Item item = result.get();
                if (item.isDir() && folderName.equals(item.objectName())) {
                    exist = true;
                }
            }
        } catch (Exception e) {
            exist = false;
        }
        return exist;
    }


    /**
     * 文件是否存在，如果抛出异常则说明文件不存在
     * @param fullFileObjectName 文件名不含桶名：如果不存在文件路径，则为"aaa.txt"，如果存在文件路径，则为"/path/aaa.txt"
     * @return true-存在；false-不存在
     */
    public static boolean fileObjectExist(MinioBucket minioBucket, String fullFileObjectName) {
        boolean exist = true;
        try {
            StatObjectArgs statObjectArgs = StatObjectArgs.builder().
                    bucket(minioBucket.getBucketName()) //桶
                    .object(fullFileObjectName) //文件对象
                    .build();
            minioBucket.getMinioClient().statObject(statObjectArgs);
        } catch (Exception e) {
            exist = false;
        }
        return exist;
    }

}
