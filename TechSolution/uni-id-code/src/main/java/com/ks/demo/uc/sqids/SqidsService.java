package com.ks.demo.uc.sqids;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.sqids.Sqids;

import java.util.Arrays;
import java.util.List;

@Service
public class SqidsService {
    //Bean单例，不存在线程安全问题
    private final Sqids sqids = Sqids.builder().build();

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
