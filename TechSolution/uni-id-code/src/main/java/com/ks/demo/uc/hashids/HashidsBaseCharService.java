package com.ks.demo.uc.hashids;

import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;

/**
 * 自定义参与编码的字符
 *
 * 字符集规则：
 * 至少 16 个字符
 * 不允许重复字符
 *
 * 使用场景
 * 避免 0/O、l/1 混淆
 * 只允许大写字母
 *
 */
@Service
public class HashidsBaseCharService {
    @Value("${hashids.salt}")
    private String salt;
    @Value("${hashids.min-length}")
    private int minLength;
    @Value("${hashids.base-char}")
    private String baseChar;

    private Hashids hashids = null;

    @PostConstruct
    public void init() {
        hashids = new Hashids(salt, minLength, baseChar);
    }

    public String encode(int code) {
        return hashids.encode(code);
    }
    public String encode(long code) {
        return hashids.encode(code);
    }
    public long decode(String decoded) {
        long[] decodes = hashids.decode(decoded);
        if (decodes.length == 0) {
            throw new IllegalArgumentException("非法ID");
        }
        return decodes[0];
    }

    public String encodeArr(int[] codes) {
        long[] codeArr = Arrays.stream(codes).asLongStream().toArray();
        return hashids.encode(codeArr);
    }
    public String encodeArr(long[] codes) {
        return hashids.encode(codes);
    }
    public long[] decodeArr(String decoded) {
        long[] decodes = hashids.decode(decoded);
        if (decodes.length == 0) {
            throw new IllegalArgumentException("非法ID");
        }
        return decodes;
    }

}
