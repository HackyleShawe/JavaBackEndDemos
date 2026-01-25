package com.ks.demo.wh.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RegionEnum {
    CN("CN", "中国大陆"),
    HK("HK", "中国香港"),
    TW("TW", "中国台湾"),
    ;

    private String code;
    private String desc;

}
