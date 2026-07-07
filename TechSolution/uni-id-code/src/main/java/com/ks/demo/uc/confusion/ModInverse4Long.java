package com.ks.demo.uc.confusion;

import com.ks.demo.uc.util.Base32ByBit5;

import java.math.BigInteger;
import java.util.Random;

public class ModInverse4Long {
    /** 乘数 M（奇数，与 2^64 互素） */
    private static final long M = 0x9E3779B97F4A7C15L; // Knuth推荐大质数

    /** M 在 mod 2^64 下的乘法逆元 */
    private static final long M_INV = computeInverse(M);

    /** 编码 */
    public static long encode(long id) {
        return id * M; // 溢出自然等价 mod 2^64
    }

    /** 解码 */
    public static long decode(long encoded) {
        return encoded * M_INV; // 溢出自然等价 mod 2^64
    }

    /** 使用 BigInteger 扩展欧几里得求逆元 */
    private static long computeInverse(long a) {
        BigInteger bigA = BigInteger.valueOf(a);
        BigInteger mod = BigInteger.ONE.shiftLeft(64);
        BigInteger inv = bigA.modInverse(mod); // 如果 a 与 2^64 互素，必有逆元
        return inv.longValue();
    }

    public static void main(String[] args) {
        Random random = new Random();
        for (int i = 0; i < 999999999; i++) {
            long id = random.nextLong();
            long encoded = encode(id);
            String base32En = Base32ByBit5.encode(encoded);

            long base32De = Base32ByBit5.decode(base32En);
            long decode = decode(base32De);
            //System.out.println("id="+id + "  encoded="+encoded + "  decoded="+decode);
            if(id != decode) {
                throw new IllegalStateException("发现编码解码不一致");
            }
        }
        System.out.println("ModInverse4Long 测试结束");
    }
}
