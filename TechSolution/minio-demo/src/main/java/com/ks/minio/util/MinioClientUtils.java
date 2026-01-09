package com.ks.minio.util;

import com.ks.minio.bean.MinioBucket;
import com.ks.minio.bean.MinioFile;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Bucket;
import io.minio.messages.Item;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * 对象存储MinIO客户端：管理桶、管理文件对象
 */
public class MinioClientUtils {

    /* *****************************  Operate Bucket Start  ***************************** */

    /**
     * 创建桶：先判断是否已经存在了，再创建桶
     * @param minioBucket MinIO客户端和桶
     */
    public static void createBucket(MinioBucket minioBucket) throws IOException,
            InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException,
            ServerException, InternalException, XmlParserException, ErrorResponseException {
        //桶已存在，直接返回true
        if(bucketExist(minioBucket)) {
            return;
        }

        MakeBucketArgs.Builder makeBuilder = MakeBucketArgs.builder().bucket(minioBucket.getBucketName());
        //region
        if(minioBucket.getRegion() != null && !"".equals(minioBucket.getRegion())) {
            makeBuilder.region(minioBucket.getRegion());
        }

        minioBucket.getMinioClient().makeBucket(makeBuilder.build());
    }


    /**
     * 删除桶：存储桶存在对象不为空时，删除会报错
     * 不用设计返回值为Boolean，因为当失败时会有异常抛出
     */
    public static void deleteBucket(MinioBucket minioBucket)  throws IOException,
            InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException,
            ServerException, InternalException, XmlParserException, ErrorResponseException {
        //桶存在，才可以删除
        if(bucketExist(minioBucket)) {
            minioBucket.getMinioClient().removeBucket(RemoveBucketArgs.builder().bucket(minioBucket.getBucketName()).build());
        }
    }


    /**
     * 获取所有的桶
     */
    public static List<Bucket> obtainAllBuckets(MinioBucket minioBucket) throws IOException,
            InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException,
            ServerException, InternalException, XmlParserException, ErrorResponseException {

        return minioBucket.getMinioClient().listBuckets();
    }


    /**
     * 检查桶是否存在
     * @return true-存在；false-不存在
     */
    public static boolean bucketExist(MinioBucket minioBucket) throws IOException, InvalidKeyException,
            InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException,
            InternalException, XmlParserException, ErrorResponseException {
        return minioBucket.getMinioClient().bucketExists(BucketExistsArgs.builder().bucket(minioBucket.getBucketName()).build());
    }
    /* *****************************  Operate Bucket End  ***************************** */


    /* *****************************  Operate File Object Start  ***************************** */
    /**
     * 文件上传
     * 文件对象的路径需要自定义
     * @param minioBucket MinIO桶
     * @param minioFile 文件对象
     * @return 文件对象名
     */
    public static String createFileObject(MinioBucket minioBucket, MinioFile minioFile)  throws IOException,
            InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException,
            ServerException, InternalException, XmlParserException, ErrorResponseException {
        PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .bucket(minioBucket.getBucketName())
                .object(minioFile.getFilePath())
                .contentType(minioFile.getContentType())
                .stream(minioFile.getInputStream(), minioFile.getFileSize(), -1)
                .build();
        ObjectWriteResponse objectWriteResponse = minioBucket.getMinioClient().putObject(putObjectArgs);
        return objectWriteResponse.object();
    }

    /**
     * 文件删除
     * @param minioBucket 桶
     * @param fullFileName 文件对象路径（不含桶名）
     */
    public static boolean deleteFileObject(MinioBucket minioBucket, String fullFileName)  throws IOException,
            InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException,
            ServerException, InternalException, XmlParserException, ErrorResponseException {
        if(fileObjectExist(minioBucket, fullFileName)) {
            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                    .bucket(minioBucket.getBucketName())
                    .object(fullFileName)
                    .build();
            minioBucket.getMinioClient().removeObject(removeObjectArgs);
            return true;
        }
        return false;
    }

    /**
     * 获取文件、加载文件
     * @param fileObjectName 文件对象名（不含桶名）
     */
    public static MinioFile obtainFileObject(MinioBucket minioBucket, String fileObjectName)  throws IOException,
            InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException,
            ServerException, InternalException, XmlParserException, ErrorResponseException {
        if(!fileObjectExist(minioBucket, fileObjectName)) {
            throw new RuntimeException("文件不存在");
        }

        //获取文件流
        GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                .bucket(minioBucket.getBucketName())
                .object(fileObjectName)
                .build();
        GetObjectResponse objectResponse = minioBucket.getMinioClient().getObject(getObjectArgs);

        MinioFile minioFile = new MinioFile();
        minioFile.setBucketName(minioBucket.getBucketName());
        minioFile.setFilePath(fileObjectName);

        //将GetObjectResponse一次性读取所有字节于一个字节数组中，将字节数组转换成输入流
        byte[] allBytes = objectResponse.readAllBytes();
        InputStream inputStream = new ByteArrayInputStream(allBytes);
        minioFile.setInputStream(inputStream);
        minioFile.setFileSize(Long.parseLong(String.valueOf(allBytes.length)));

        //获取文件属性
        StatObjectArgs statObjectArgs = StatObjectArgs.builder().
                bucket(minioBucket.getBucketName())
                .object(fileObjectName).build();
        StatObjectResponse statObjectResponse = minioBucket.getMinioClient().statObject(statObjectArgs);
        minioFile.setContentType(statObjectResponse.contentType());

        return minioFile;
    }


    /**
     * 获取文件（断点）
     * @param fileObjectName 文件对象名（不含桶名）
     * @param offset 起始字节位置
     * @param length 长度
     */
    public static MinioFile obtainFileObjectBySteps(MinioBucket minioBucket, String fileObjectName, long offset, long length)  throws IOException,
            InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException,
            ServerException, InternalException, XmlParserException, ErrorResponseException {
        if(!fileObjectExist(minioBucket, fileObjectName)) {
            throw new RuntimeException("文件不存在");
        }

        //获取文件流
        GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                .bucket(minioBucket.getBucketName())
                .object(fileObjectName)
                .offset(offset).length(length)
                .build();
        GetObjectResponse objectResponse = minioBucket.getMinioClient().getObject(getObjectArgs);

        MinioFile minioFile = new MinioFile();
        minioFile.setBucketName(minioBucket.getBucketName());
        minioFile.setFilePath(fileObjectName);

        //将GetObjectResponse一次性读取所有字节于一个字节数组中，将字节数组转换成输入流
        byte[] allBytes = objectResponse.readAllBytes();
        InputStream inputStream = new ByteArrayInputStream(allBytes);
        minioFile.setInputStream(inputStream);
        minioFile.setFileSize(Long.parseLong(String.valueOf(allBytes.length)));

        //获取文件属性
        StatObjectArgs statObjectArgs = StatObjectArgs.builder().
                bucket(minioBucket.getBucketName())
                .object(fileObjectName).build();
        StatObjectResponse statObjectResponse = minioBucket.getMinioClient().statObject(statObjectArgs);
        minioFile.setContentType(statObjectResponse.contentType());

        return minioFile;
    }


    /**
     * 获取prefixPath下的文件
     * @param prefixPath 文件在桶中的路径，不含桶名
     * @param recurse 是否递归整个文件夹，否则只获取当前path的文件
     */
    public static List<String> obtainFileObjectByPrefixPath(MinioBucket minioBucket, String prefixPath, boolean recurse) throws IOException,
            InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException,
            ServerException, InternalException, XmlParserException, ErrorResponseException {
        List<String> fileList = new ArrayList<>();
        ListObjectsArgs listObjectsArgs = ListObjectsArgs.builder().bucket(
                minioBucket.getBucketName()) //桶名
                .prefix(prefixPath) //文件前缀
                .recursive(recurse) //是否递归所有子路径
                .build();
        Iterable<Result<Item>> objectsIterator = minioBucket.getMinioClient().listObjects(listObjectsArgs);
        if (objectsIterator != null) {
            for (Result<Item> object : objectsIterator) {
                Item item = object.get();
                fileList.add(item.objectName());
            }
        }
        return fileList;
    }

    /**
     * 获取文件属性
     * @param fileObjectName 文件对象名，不含桶名
     */
    public static MinioFile fileObjectAttributes(MinioBucket minioBucket, String fileObjectName) throws IOException,
            InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException,
            ServerException, InternalException, XmlParserException, ErrorResponseException {
        if(fileObjectExist(minioBucket, fileObjectName)) {
            StatObjectArgs statObjectArgs = StatObjectArgs.builder().
                    bucket(minioBucket.getBucketName())
                    .object(fileObjectName).build();
            StatObjectResponse statObjectResponse = minioBucket.getMinioClient().statObject(statObjectArgs);

            MinioFile minioFile = new MinioFile();
            minioFile.setBucketName(minioBucket.getBucketName());
            minioFile.setFilePath(statObjectResponse.object());
            minioFile.setContentType(statObjectResponse.contentType());
            minioFile.setFileSize(statObjectResponse.size());
            return minioFile;
        } else {
            throw new RuntimeException("文件不存在");
        }
    }

    /**
     * 文件复制
     * @param sourceBucket 源桶
     * @param sourceFileName 源文件全名（不含桶名）
     * @param targetBucket 目标桶
     * @param targetFileName 目标文件全名
     */
    public static ObjectWriteResponse fileObjectCopy(MinioBucket sourceBucket, String sourceFileName,
                                                     MinioBucket targetBucket, String targetFileName) throws IOException,
            InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException,
            ServerException, InternalException, XmlParserException, ErrorResponseException {
        if(!fileObjectExist(sourceBucket, sourceFileName)) {
            throw new RuntimeException("被复制的源文件不存在");
        }

        //源
        CopySource copySource = CopySource.builder()
                .bucket(sourceBucket.getBucketName())
                .object(sourceFileName)
                .build();
        //目标
        CopyObjectArgs copyArgs = CopyObjectArgs.builder()
                .source(copySource)
                .bucket(targetBucket.getBucketName())
                .object(targetFileName)
                .build();

        return sourceBucket.getMinioClient().copyObject(copyArgs);
    }

    /**
     * 文件移动
     * @param sourceBucket 源桶
     * @param sourceFullName 文件全名（不含桶名），例如：/path11/path22/aa.txt
     * @param targetBucket 目标桶
     * @param targetFullName 目标文件全名
     */
    public static boolean fileObjectMove(MinioBucket sourceBucket, String sourceFullName,
                                         MinioBucket targetBucket, String targetFullName) throws IOException,
            InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException,
            ServerException, InternalException, XmlParserException, ErrorResponseException {
        if(fileObjectExist(sourceBucket, sourceFullName)) {
            //源
            CopySource copySource = CopySource.builder().bucket(sourceBucket.getBucketName()).object(sourceFullName).build();
            //目标
            CopyObjectArgs copyArgs = CopyObjectArgs.builder()
                    .source(copySource)
                    .bucket(targetBucket.getBucketName())
                    .object(targetFullName)
                    .build();
            //复制到目的地
            sourceBucket.getMinioClient().copyObject(copyArgs);
            //删除原位置的文件
            deleteFileObject(sourceBucket, sourceFullName);
            return true;
        } else {
            throw new RuntimeException("被拷贝的文件不存在");
        }
    }

    /**
     * 文件是否存在，如果抛出异常则说明文件不存在
     * @param minioBucket 桶
     * @param fileObjectName 文件名不含桶名：如果不存在文件路径，则为"aaa.txt"，如果存在文件路径，则为"/path/aaa.txt"
     * @return true-存在；false-不存在
     */
    public static boolean fileObjectExist(MinioBucket minioBucket, String fileObjectName) {
        boolean exist = true;
        try {
            StatObjectArgs statObjectArgs = StatObjectArgs.builder().
                    bucket(minioBucket.getBucketName()) //桶
                    .object(fileObjectName) //文件对象
                    .build();
            minioBucket.getMinioClient().statObject(statObjectArgs);
        } catch (Exception e) {
            exist = false;
        }
        return exist;
    }

    /* *****************************  Operate File Object End  ***************************** */
}
