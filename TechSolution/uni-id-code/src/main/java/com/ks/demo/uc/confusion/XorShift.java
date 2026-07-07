package com.ks.demo.uc.confusion;

public class XorShift {
    /**
     * 混淆
     * @param id 待混淆的原始id
     * @param secretKey 异或值
     * @param shift 位移量
     */
    public static int encode(int id, int secretKey, int shift) {
        int x = id ^ secretKey;
        return (x << shift) | (x >>> (32 - shift));
    }
    public static int decode(int encoded, int secretKey, int shift) {
        int x = (encoded >>> shift) | (encoded << (32 - shift));
        return x ^ secretKey;
    }

    public static long encode(long id, long secretKey, int shift) {
        shift = shift & 63; // 避免 shift >= 64
        long x = id ^ secretKey;
        return (x << shift) | (x >>> (64 - shift));
    }
    public static long decode(long encoded, long secretKey, int shift) {
        shift = shift & 63; // 避免 shift >= 64
        long x = (encoded >>> shift) | (encoded << (64 - shift));
        return x ^ secretKey;
    }
    public static void batchEncode(long[] src, long[] dst, long secretKey,int shift) {
        final int n = src.length;
        final int s = shift & 63;
        for (int i = 0; i < n; i++) {
            long x = src[i] ^ secretKey;
            dst[i] = Long.rotateLeft(x, s);
        }
    }
    public static void batchDecode(long[] src, long[] dst, long secretKey, int shift) {
        final int n = src.length;
        final int s = shift & 63;
        for (int i = 0; i < n; i++) {
            long x = Long.rotateRight(src[i], s);
            dst[i] = x ^ secretKey;
        }
    }


    public static void main(String[] args) {

        for (int i = 0; i < 99999999; i++) {
            int encode = encode(i, 6666, 5555);
            int decode = decode(encode, 6666, 5555);
            if(i != decode) {
                throw new RuntimeException("Invalid encode/decode");
            }
        }
        System.out.println("XorShift 测试结束");
    }
}
