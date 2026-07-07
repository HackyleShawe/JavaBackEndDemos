package com.ks.demo.uc.hashids;

import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;

/**
 * 加盐混淆
 *
 * salt的作用
 * 防止别人用同样库解你 ID
 * salt 一旦上线 绝对不能改
 */
@Service
public class HashidsSaltService {
    @Value("${hashids.salt}")
    private String salt;
    @Value("${hashids.min-length}")
    private int minLength;

    //new在@Value注入之前
    //解决方案：后构造器，在构造器的入参使用@Value，使用@ConfigurationProperties单独注入
    //private Hashids hashidsSalt = new Hashids(salt);

    private Hashids hashidsSalt = null;
    private Hashids hashidsMinLen = null;

    @PostConstruct
    public void init() {
        hashidsSalt = new Hashids(salt);
        hashidsMinLen = new Hashids(salt, minLength);
    }

    public String encode(int code) {
        return hashidsSalt.encode(code);
    }
    public String encode(long code) {
        return hashidsSalt.encode(code);
    }
    public long decode(String decoded) {
        long[] decodes = hashidsSalt.decode(decoded);
        if (decodes.length == 0) {
            throw new IllegalArgumentException("非法ID");
        }
        return decodes[0];
    }

    public String encodeArr(int[] codes) {
        long[] codeArr = Arrays.stream(codes).asLongStream().toArray();
        return hashidsSalt.encode(codeArr);
    }
    public String encodeArr(long[] codes) {
        return hashidsSalt.encode(codes);
    }
    public long[] decodeArr(String decoded) {
        long[] decodes = hashidsSalt.decode(decoded);
        if (decodes.length == 0) {
            throw new IllegalArgumentException("非法ID");
        }
        return decodes;
    }

    public String encodeMinLen(int code) {
        return hashidsMinLen.encode(code);
    }
    public String encodeMinLen(long code) {
        return hashidsMinLen.encode(code);
    }
    public long decodeMinLen(String decoded) {
        long[] decodes = hashidsMinLen.decode(decoded);
        if (decodes.length == 0) {
            throw new IllegalArgumentException("非法ID");
        }
        return decodes[0];
    }

    public String encodeMinLenArr(int[] codes) {
        long[] codeArr = Arrays.stream(codes).asLongStream().toArray();
        return hashidsMinLen.encode(codeArr);
    }
    public String encodeMinLenArr(long[] codes) {
        return hashidsMinLen.encode(codes);
    }
    public long[] decodeMinLenArr(String decoded) {
        long[] decodes = hashidsMinLen.decode(decoded);
        if (decodes.length == 0) {
            throw new IllegalArgumentException("非法ID");
        }
        return decodes;
    }
}
