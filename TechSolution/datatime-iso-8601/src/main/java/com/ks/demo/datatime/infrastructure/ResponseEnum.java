package com.ks.demo.datatime.infrastructure;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 状态码以及状态描述枚举类
 */
@Getter
@AllArgsConstructor
public enum ResponseEnum {
    OP_OK(200, "操作成功"),
    OP_FAIL(500, "操作失败");

    private final Integer code;
    private final String message;

    public static ResponseEnum of(Integer code) {
        for (ResponseEnum e : ResponseEnum.values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }

}
