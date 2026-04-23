package com.ks.demo.cache.config;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 参数配置
 */
@Data
@TableName("sys_config")
public class SysConfigEntity implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 参数键名
     */
    private String configKey;
    /**
     * 参数键值
     */
    private String configValue;
    /**
     * 参数说明
     */
    private String configDesc;

    /**
     * 状态：0-无效 1-有效
     */
    private Boolean status;

    /**
     * 0-False-未删除, 1-True-已删除
     */
    private Boolean deleted;

    private Long createBy;

    private LocalDateTime createTime;

    private Long updateBy;

    private LocalDateTime updateTime;

}
