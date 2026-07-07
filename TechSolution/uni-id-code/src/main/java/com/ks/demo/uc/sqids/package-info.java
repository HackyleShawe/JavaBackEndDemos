package com.ks.demo.uc.sqids;
/**
 * Sqids 是什么？安全、确定性、可预测性低地把整数 ID 转成短字符串
 * Hashids 是“把 ID 伪装成字符串”，Sqids 是“把 ID 经过一套可逆数学混合后再编码”。
 *
 * 推荐 Sqids 的场景
 * - C 端业务（订单号、分享链接、资源ID）
 * - 有防爬、防枚举、防推断诉求
 * - 多语言 SDK / 多端一致性要求高
 *
 * 实现：
 * - HashidsService
 * - HashidsSaltService：加盐、指定最小长度
 * - HashidsBaseCharService：加盐、指定最小长度、自定义输出字符集
 */
