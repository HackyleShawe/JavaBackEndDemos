package com.ks.demo.uc.hashids;
/**
 * 主要思路：把数字（int / long / 数字数组）变成不规则、可读、不可反推的短字符串（如 jR）
 *
 * 应用场景
 *  - URL 中隐藏自增 ID，防止前端“遍历接口”
 *  - 影藏对外暴露订单号
 *  - 将多个数字（数组）进行混淆，防止参数被篡改
 *
 * 实现：
 * - HashidsService
 * - HashidsSaltService：加盐、指定最小长度
 * - HashidsBaseCharService：加盐、指定最小长度、自定义输出字符集
 */
