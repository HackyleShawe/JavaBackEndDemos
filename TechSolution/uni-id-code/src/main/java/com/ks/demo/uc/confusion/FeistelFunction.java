package com.ks.demo.uc.confusion;


import com.ks.demo.uc.util.Base32ByBit5;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;


public class FeistelFunction {

    //3轮足够防枚举、防线性推断，性能也可以接受
    //@Value("${confusion.feistel.rounds}")
    private int rounds = 3;
    //轮密钥，你可以理解为加密中的秘钥，可以放在配置文件中
    //@Value("${confusion.feistel.round-keys}")
    private int[] roundKeys  = new int[]{0x1F123, 0x17C45, 0x0BEEF};

    /**
     * 强制把计算结果限制在“指定的 bit 宽度”内，防止脏高位污染 Feistel 的数学结构。
     * 没有它：
     * 可逆性可能被破坏
     * 不同 bit 长度下行为不一致
     * long / byte[] 版本无法统一
     * 安全性和稳定性都会下降
     */
    private long bitMask(int bits) {
        return bits == 64 ? -1L : (1L << bits) - 1;
    }

    private long feistelObfuscate(long value, int totalBits) {
        int leftBits = totalBits / 2;
        int rightBits = totalBits - leftBits;

        long left = (value >>> rightBits) & bitMask(leftBits);
        long right = value & bitMask(rightBits);

        for (int i = 0; i < rounds; i++) {
            long newLeft = right;

            long f = ((right * roundKeys[i]) ^ (right >>> 3))
                    & bitMask(leftBits);

            long newRight = left ^ f;

            left = newLeft;
            right = newRight;
        }

        return (left << rightBits) | right;
    }

    private long feistelDeobfuscate(long value, int totalBits) {
        int leftBits = totalBits / 2;
        int rightBits = totalBits - leftBits;

        long left = (value >>> rightBits) & bitMask(leftBits);
        long right = value & bitMask(rightBits);

        for (int i = rounds - 1; i >= 0; i--) {
            long newRight = left;

            long f = ((left * roundKeys[i]) ^ (left >>> 3))
                    & bitMask(leftBits);

            long newLeft = right ^ f;

            left = newLeft;
            right = newRight;
        }

        return (left << rightBits) | right;
    }

    public int feistelObfuscate(int val) {
        long obfuscate = feistelObfuscate(val, 32);
        return (int) obfuscate;
    }
    public int feistelDeobfuscate(int val) {
        long deobfuscate = feistelDeobfuscate(val, 32);
        return (int) deobfuscate;
    }

    public long feistelObfuscate(long val) {
        return feistelObfuscate(val, 64);
    }
    public long feistelDeobfuscate(long val) {
        return feistelDeobfuscate(val, 64);
    }


    public static void main(String[] args) {
        Random random = new Random();

        FeistelFunction fcs = new FeistelFunction();
        fcs.rounds = 3;
        fcs.roundKeys = new int[]{0x1F123, 0x17C45, 0x0BEEF};

        //测试轮函数混淆前后的数据是否一致
        for (int i = 0; i < 99999999; i++) {
            int nextInt = random.nextInt(0, 999999999);
            int obf = fcs.feistelObfuscate(nextInt);
            int deobf = fcs.feistelDeobfuscate(obf);
            if(nextInt != deobf) {
                throw new RuntimeException("int解码失败");
            }

            long nextLong = random.nextLong(0, 999999999999999L);
            long obfLong = fcs.feistelObfuscate(nextLong);
            long deobfLong = fcs.feistelDeobfuscate(obfLong);
            //System.out.println(nextLong + " " + obfLong + " " + deobfLong);
            if(nextLong != deobfLong) {
                throw new RuntimeException("long解码失败");
            }
        }

        //测试轮函数混淆前后进行base32处理数据是否一致
        for (int i = 0; i < 99999999; i++) {
            int nextInt = random.nextInt(0, 999999999);
            int obf = fcs.feistelObfuscate(nextInt);
            String base32Encode = Base32ByBit5.encode((long)obf);

            long decoded = Base32ByBit5.decode(base32Encode);
            int deobf = fcs.feistelDeobfuscate((int)decoded);
            if(nextInt != deobf) {
                System.out.println("nextInt " + nextInt + "  obf " + obf + " base32Encode " + base32Encode
                        + " deobf  " + deobf + " decoded " + decoded );
                throw new RuntimeException("int Bit5Base32 解码失败");
            }

            long nextLong = random.nextLong(0, 999999999999999L);
            long obfLong = fcs.feistelObfuscate(nextLong);
            String base32En = Base32ByBit5.encode(obfLong);

            long base32De = Base32ByBit5.decode(base32En);
            long deobfLong = fcs.feistelDeobfuscate(base32De);
            if(nextLong != deobfLong) {
                System.out.println("nextInt " + nextInt + "  obf " + obf + " base32Encode " + base32Encode
                        + " deobf  " + deobf + " decoded " + decoded );
                throw new RuntimeException("long Bit5Base32 解码失败");
            }

        }

        System.out.println("FeistelFunction测试结束");
    }

}
