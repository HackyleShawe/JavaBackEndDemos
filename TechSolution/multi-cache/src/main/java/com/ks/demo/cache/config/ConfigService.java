package com.ks.demo.cache.config;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfigService {
    @Autowired
    private SysConfigMapper sysConfigMapper;

    @Autowired
    private ConfigCacheService configCacheService;

    public void add(SysConfigEntity config) {
        //check、configKey重复性检查

        int inserted = sysConfigMapper.insert(config);
        if (inserted > 0) {
            configCacheService.put(config.getConfigKey(), config);
        }
    }

    public SysConfigEntity get(String key) {
        return configCacheService.get(key);
    }

    public void update(SysConfigEntity configEntity) {
        //check

        int updated = sysConfigMapper.updateById(configEntity);
        if(updated < 0) {
            return;
        }

        String key = configEntity.getConfigKey();
        configCacheService.evict(key);
    }

    public void del(String key) {
        //check

        int deleted = sysConfigMapper.delete(Wrappers.<SysConfigEntity>lambdaQuery()
                .eq(SysConfigEntity::getConfigKey, key)
                .eq(SysConfigEntity::getStatus, Boolean.TRUE)
                .eq(SysConfigEntity::getDeleted, Boolean.FALSE)
                .last("limit 1"));
        if(deleted < 0) {
            return;
        }

        configCacheService.evict(key);
    }
}
