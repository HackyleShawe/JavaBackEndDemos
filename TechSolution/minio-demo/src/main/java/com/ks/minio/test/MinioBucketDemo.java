package com.ks.minio.test;

import com.ks.minio.bean.MinioBucket;
import io.minio.BucketExistsArgs;
import io.minio.GetBucketPolicyArgs;
import io.minio.SetBucketLifecycleArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import io.minio.messages.LifecycleConfiguration;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * 操作桶的一些代码示例
 */
public class MinioBucketDemo {
    /**
     * 生命周期
     * 1.周期性上传的日志文件，可能只需要保留一个星期或一个月。到期后要删除它们。
     * 2.某些文档在一段时间内经常访问，但是超过一定时间后便可能不再访问了。这些文档需要在一定时间后转化为低频访问存储，归档存储或者删除。
     */
    public static boolean configBucketWithLifeCycle(MinioBucket minioBucket) throws IOException,
            InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException,
            ServerException, InternalException, XmlParserException, ErrorResponseException {
        if(!bucketExist(minioBucket)) {
            return false;
        }
        if(minioBucket.getLifecycleRuleList() == null || minioBucket.getLifecycleRuleList().size() < 1) {
            return false;
        }

        // 配置生命周期规则
        //List<LifecycleRule> rules = new LinkedList<>();
        //rules.add(new LifecycleRule(Status.ENABLED, // 开启状态
        //        null,
        //        new Expiration((ZonedDateTime) null, 365, null), // 保存365天
        //        new RuleFilter("logs/"), // 目录配置
        //        "rule2",
        //        null,
        //        null,
        //        null));
        LifecycleConfiguration lifecycleConfiguration = new LifecycleConfiguration(minioBucket.getLifecycleRuleList());
        minioBucket.getMinioClient().setBucketLifecycle(
                SetBucketLifecycleArgs.builder().bucket(minioBucket.getBucketName()).config(lifecycleConfiguration).build());

        return true;
    }


    /**
     * 获取桶策略
     */
    public static String getBucketPolicy(MinioBucket minioBucket) throws Exception {
        GetBucketPolicyArgs policyArgs = GetBucketPolicyArgs.builder()
                .bucket(minioBucket.getBucketName())
                .build();
        return minioBucket.getMinioClient().getBucketPolicy(policyArgs);
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
}
