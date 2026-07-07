package com.ks.demo.uc.sqids;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.sqids.Sqids;

import javax.annotation.PostConstruct;
import java.util.List;


@Service
public class SqidsMinLenService {
    //@Value("${sqids.salt}") sqids没有加盐，为什么？加盐增加了维护成本和计算成本，sqids的本质是可逆性混淆
    //private String salt;
    @Value("${sqids.min-length}")
    private int minLength;

    //new在@Value注入之前
    //解决方案：后构造器，在构造器的入参使用@Value，使用@ConfigurationProperties单独注入
    private Sqids sqidsMinLen = null;

    @PostConstruct
    public void init() {
        sqidsMinLen = Sqids.builder().minLength(minLength).build();
    }


    public String encode(int code) {
        return sqidsMinLen.encode(List.of((long) code));
    }

    public String encode(long code) {
        return sqidsMinLen.encode(List.of(code));
    }

    public long decode(String decoded) {
        if(StringUtils.isBlank(decoded)) {
            throw new RuntimeException("Invalid decoded string");
        }
        List<Long> decodes = sqidsMinLen.decode(decoded);
        if(decodes.isEmpty()) {
            throw new RuntimeException("sqids decoded fail");
        }
        return decodes.get(0);
    }

    public String encodeArr(List<Long> codes) {
        return sqidsMinLen.encode(codes);
    }

    public List<Long> decodeArr(String decoded) {
        if(StringUtils.isBlank(decoded)) {
            throw new RuntimeException("Invalid decoded string");
        }
        List<Long> decodes = sqidsMinLen.decode(decoded);
        if(decodes.isEmpty()) {
            throw new RuntimeException("sqids decoded fail");
        }
        return decodes;
    }

}
