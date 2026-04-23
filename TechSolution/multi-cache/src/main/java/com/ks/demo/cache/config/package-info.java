package com.ks.demo.cache.config;
/**
 * 系统配置表（sys_config）实现多级缓存
 * L1：Caffeine
 * L2：Redis
 * L3：MySQL
 *
 * Key:prefix+configKey Val:JSON Entity
 *
 */
