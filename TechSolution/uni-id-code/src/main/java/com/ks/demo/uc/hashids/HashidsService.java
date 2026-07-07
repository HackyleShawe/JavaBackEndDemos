package com.ks.demo.uc.hashids;

import org.hashids.Hashids;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class HashidsService {

    //Bean单例，不存在线程安全问题
    private final Hashids hashids = new Hashids();

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
