package com.ks.demo.uc.util;

import java.io.ByteArrayOutputStream;
import java.util.Random;

/**
 * 32个字符，2^5=32
 * 每5个比特位编码一个字符：5个比特位转换成十进制，从baseChars中按下标取值
 */
public class Base32ByBit5 {
    /**
     * 用于编码的字符：使用24个大写字母和8个数字（不要字母I、字母O、数字1、数字0）：ABCDEFGHJKLMNPQRSTUVWXYZ23456789，共计32个字符
     * 适用于编码为可读性较强的字符串，例如：兑换码
     */
    private final static String BASE_CHARS = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";

    /**
     * 将一个long型的数字（最大支持64个比特位），按照每5个比特位编码为一个字符
     * 注意，如果是int，直接强转为long即可，因为long完全兼容int
     */
    public static String encode(Long raw) {
        if (raw == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();

        //每次循环，取最低的5个bit位，然后转换为对应字符
        while (raw != 0) {
            int i = (int) (raw & 0b11111);
            sb.append(BASE_CHARS.charAt(i));
            raw = raw >>> 5;
        }
        return sb.toString();
    }
    public static long decode(String code) {
        if(code == null || code.isEmpty()) {
            throw new IllegalArgumentException("解码参数为空");
        }
        long res = 0;
        char[] chars = code.toCharArray();
        for (int i = chars.length - 1; i >= 0; i--) {
            long n = BASE_CHARS.indexOf(chars[i]);
            res = res | (n << (5*i));
        }
        return res;
    }



    public static void main(String[] args) {
        Random random = new Random();

        for (int i = 0; i < 99999999; i++) {
            int nextInt = random.nextInt();
            String enc = encode((long) nextInt);
            long dec = decode(enc);
            //System.out.println(nextInt + " " + enc + "  " + dec);
            if(nextInt != dec) {
                throw new RuntimeException("生成不一致");
            }

            long nextLong = random.nextLong();
            enc = encode(nextLong);
            dec = decode(enc);
            //System.out.println(nextLong + " " + enc + "  " + dec);
            if(nextLong != dec) {
                throw new RuntimeException("生成不一致");
            }

        }
        System.out.println("Bit5Base32测试结束");
    }
}
