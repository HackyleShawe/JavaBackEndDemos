package com.ks.demo.uc.bitarrange;

import com.ks.demo.uc.confusion.FeistelFunction;
import com.ks.demo.uc.util.Base32ByBit5;
import lombok.val;

/**
 * bit编排后进行可逆性混淆
 */
public class BitArrangeAndConfusion {

    /**
     * bit位编排方案：最高位保持为1 +  int型ID的32个比特位 + 最后2位保持为1
     */
    public String encode(int raw) {
        //可逆性混淆
        FeistelFunction fc = new FeistelFunction(); //正式环境这里从Spring中注入
        int obfuscate = fc.feistelObfuscate(raw);

        long bitEncode = bitEncode(obfuscate);

        return Base32ByBit5.encode(bitEncode);
    }

    public int decode(String encoded) {
        long decoded = Base32ByBit5.decode(encoded);

        int de = bitDecode(decoded);

        FeistelFunction fc = new FeistelFunction();
        return fc.feistelDeobfuscate(de);
    }

    private long bitEncode(int id) {
        // int转无符号long，避免负数符号扩展
        long value = id & 0xFFFFFFFFL;
        // 最高位(第34位)
        long high = 1L << 34;
        // int左移2位
        long middle = value << 2;
        // 最低两位11
        long low = 0b11L;
        return high | middle | low;
    }
    private int bitDecode(long encoded) {
        // 去掉最低2位
        long value = encoded >>> 2;
        // 保留32位
        value &= 0xFFFFFFFFL;
        return (int) value;
    }

    public static void main(String[] args) {
        BitArrangeAndConfusion ba = new BitArrangeAndConfusion();
        for (int i = 0; i < 999999999; i++) {
            String encode = ba.encode(i);
            int decode = ba.decode(encode);

            if(i != decode) {
                throw new RuntimeException("BitArrangeAndConfusion 比对失败");
            }
        }
        System.out.println(ba.encode(454));

        System.out.println("BitArrangeAndConfusion 测试结束");
    }
}
