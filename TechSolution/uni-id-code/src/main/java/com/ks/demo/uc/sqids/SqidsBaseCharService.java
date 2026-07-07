package com.ks.demo.uc.sqids;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.sqids.Sqids;

import javax.annotation.PostConstruct;
import java.util.List;

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
public class SqidsBaseCharService {
    @Value("${sqids.min-length}")
    private int minLength;
    @Value("${sqids.base-char}")
    private String baseChar;
    private Sqids sqids = null;


    @PostConstruct
    public void init() {
        sqids = Sqids.builder().minLength(minLength).alphabet(baseChar).build();
    }


    public String encode(int code) {
        return sqids.encode(List.of((long) code));
    }

    public String encode(long code) {
        return sqids.encode(List.of(code));
    }

    public long decode(String decoded) {
        if(StringUtils.isBlank(decoded)) {
            throw new RuntimeException("Invalid decoded string");
        }
        List<Long> decodes = sqids.decode(decoded);
        if(decodes.isEmpty()) {
            throw new RuntimeException("sqids decoded fail");
        }
        return decodes.get(0);
    }

    public String encodeArr(List<Long> codes) {
        return sqids.encode(codes);
    }

    public List<Long> decodeArr(String decoded) {
        if(StringUtils.isBlank(decoded)) {
            throw new RuntimeException("Invalid decoded string");
        }
        List<Long> decodes = sqids.decode(decoded);
        if(decodes.isEmpty()) {
            throw new RuntimeException("sqids decoded fail");
        }
        return decodes;
    }
}
