package com.ks.demo.shiro.entity;

import java.io.Serializable;
import java.util.Date;

public class PermissionEntity implements Serializable {
    private Integer id;
    private String path;
    private String name;
    private String description;
    private Date createTime;
    private Date updateTime;
    private Boolean isDeleted;

    /**
     * 主键
     */
    public Integer getId() {
        return id;
    }

    /**
     * 主键
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 权限所代表的请求路径
     */
    public String getPath() {
        return path;
    }

    /**
     * 权限所代表的请求路径
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 权限的名称
     */
    public String getName() {
        return name;
    }

    /**
     * 权限的名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     *
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     *
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     *
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 0-False-未删除, 1-True-已删除
     */
    public Boolean getIsDeleted() {
        return isDeleted;
    }

    /**
     * 0-False-未删除, 1-True-已删除
     */
    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
