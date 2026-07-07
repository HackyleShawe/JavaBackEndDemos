package com.ks.demo.uc.mod;

import com.ks.demo.uc.util.CrcUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Long型ID模后Base编码
 * 主要思路：long --> mod --> Base --> 校验码
 */
@Service
public class ModConfusionService {

    @Value("${mod.base-char}")
    private String baseChar;

    /**
     * 定义Base62（0-9，a-z，A-Z）于字符数组，长度为62
     * 输入一个long型的num
     * 先模62，从Base62字符数组中取得一个字符
     * 再除62，直到num小于0时停止
     * 得到一个唯一串
     * 计算CRC8校验码
     */
    public String encode(long id) {
        if(id < 0) {
            throw new IllegalArgumentException("待编码的id必须不能小于0");
        }
        int baseSize = baseChar.length();
        StringBuilder sb = new StringBuilder();
        while (id > 0) {
            int index = (int) (id % baseSize);
            sb.append(baseChar.charAt(index));
            id /= baseSize;
        }
        // 补足长度为8位
        //while (sb.length() < CODE_LENGTH) {
        //    sb.append('0'); // 也可填充随机字符
        //}

        //计算CRC8校验码
        String code = sb.reverse().toString();
        char crc8CheckSum = CrcUtils.crc8CheckSum(code);
        if(Character.isWhitespace(crc8CheckSum)) {
            throw new IllegalStateException("CRC8 Check Sum 计算失败");
        }

        return code + crc8CheckSum; // 高位在前
    }

    /**
     * 检查校验码
     * 依次遍历唯一串的每个字符c
     * 从字符数组中找到该个字符c所在的下标i
     * id = id*62+i
     * 最终解析出的值即为原始id
     */
    public long decode(String code) {
        String body = code.substring(0, code.length() - 1);
        char crc8CheckSum = code.charAt(code.length() - 1);
        if(!Objects.equals(crc8CheckSum, CrcUtils.crc8CheckSum(body))) {
            throw new IllegalStateException("CRC8 Check Sum 校验失败");
        }

        long id = 0;
        int baseSize = baseChar.length();

        for (char cc : body.toCharArray()) {
            Integer mod = null;
            for (int j = 0; j < baseSize; j++) {
                if(cc == baseChar.charAt(j)) {
                    mod = j;
                    break;
                }
            }
            if(mod == null) {
                throw new IllegalArgumentException("待解码的串中存在非法字符");
            }

            id = id * baseSize + mod;
        }

        return id;
    }

}
