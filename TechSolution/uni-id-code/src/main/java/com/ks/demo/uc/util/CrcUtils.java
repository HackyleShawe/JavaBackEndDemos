package com.ks.demo.uc.util;

import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * CRC（Cyclic Redundancy Check，循环冗余校验）算法工具类
 */
public class CrcUtils {

    private static final int POLY = 0x07; // CRC-8 标准多项式

    //用于生成校验位的字符集
    //private static final char[] BASE32 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567".toCharArray();
    private static final char[] BASE62 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

    /**
     * 标准CRC8
     * 多项式	0x07
     * 初始值	0x00
     */
    private static byte crc8(byte[] data) {
        int crc = 0x00;

        for (byte b : data) {
            crc ^= (b & 0xFF);
            for (int i = 0; i < 8; i++) {
                if ((crc & 0x80) != 0) {
                    crc = ((crc << 1) ^ POLY) & 0xFF;
                } else {
                    crc = (crc << 1) & 0xFF;
                }
            }
        }
        return (byte) crc;
    }

    /**
     * CRC8校验码编码
     * @param data 原始数据
     * @return 校验
     */
    public static char crc8CheckSum(String data) {
        //**isEmpty()**：判断是不是“空字符串”
        //**isBlank()**：判断是不是“空白字符串”（包含空格、制表符、换行符这些）
        if(data == null || data.trim().isBlank()) {
            throw new IllegalArgumentException("CRC8 Encoded String is null or empty");
        }
        byte crc8 = crc8(data.getBytes(StandardCharsets.UTF_8));
        return BASE62[(crc8 & 0xFF) % BASE62.length];
    }

    public static void main(String[] args) {
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            String str = "Asdfaddu1234" + random.nextLong();
            char crc8CheckSum = CrcUtils.crc8CheckSum(str);
            System.out.println(crc8CheckSum);

            String code = str + crc8CheckSum;

            int randIndex = random.nextInt(3);
            String body = code.substring(0, code.length() - randIndex);
            char check = code.charAt(code.length() - 1);
            boolean result = CrcUtils.crc8CheckSum(body) == check;
            //如果randIndex=1，则校验结果应该为true
            System.out.println("====== randIndex=" + randIndex + " result=" +result);

        }
    }

}
