package com.ks.demo.uc.confusion;

import com.ks.demo.uc.util.Base32ByBit5;

public class ModInverse4Int {
    /**
     * 乘数 M
     * 为什么用这个数？
     * 奇数
     * 与 2^32 互素
     * 乘法散列效果好
     * 工业界广泛使用（Knuth hash）
     */
    private static final int M = 0x9E3779B1; // 2654435761

    /** M 在 mod 2^32 下的乘法逆元 */
    private static final int M_INV = modInverse(M);


    /**
     * 编码（混淆）
     */
    public static int encode(int id) {
        // int 溢出 == mod 2^32
        return id * M;
    }

    /**
     * 解码（还原）
     */
    public static int decode(int encoded) {
        return encoded * M_INV;
    }

    /**
     * 使用扩展欧几里得算法求逆元（mod 2^32）
     */
    private static int modInverse(int a) {
        long mod = 1L << 32;

        long t = 0, newT = 1;
        long r = mod, newR = Integer.toUnsignedLong(a);

        while (newR != 0) {
            long q = r / newR;

            long tmpT = t - q * newT;
            t = newT;
            newT = tmpT;

            long tmpR = r - q * newR;
            r = newR;
            newR = tmpR;
        }

        if (r != 1) {
            throw new IllegalArgumentException("M 与 2^32 不互素，逆元不存在");
        }

        if (t < 0) {
            t += mod;
        }

        return (int) t;
    }


    public static void main(String[] args) {
        //Random random = new Random();
        for (int i = 1; i < 999999999; i++) {
            int id = i; //random.nextInt(1, 999999999);
            int encoded = encode(id);

            String encode = Base32ByBit5.encode((long)encoded); //BitConverter.getBytes(encoded)
            //System.out.println("id " + id + " encoded " + encoded + " encode " + encode);

            long decoded = Base32ByBit5.decode(encode);
            int dd = Long.valueOf(decoded).intValue();
            int decode = decode(dd);
            //System.out.println("decoded="+decoded + "  dd " + dd + "  decoded " + decode);

            //byte[] encode = Base64.getEncoder().encode(BitConverter.getBytes(encoded));
            //byte[] ddd = Base64.getDecoder().decode(encode);
            //int anInt = BitConverter.toInt(ddd);
            //System.out.println("encode  " + new String(encode) + "   anInt="+anInt);

            if(id != decode) {
                throw new IllegalStateException("发现编码解码不一致");
            }
        }
        System.out.println("ModInverse4Int 测试结束");
    }
}
