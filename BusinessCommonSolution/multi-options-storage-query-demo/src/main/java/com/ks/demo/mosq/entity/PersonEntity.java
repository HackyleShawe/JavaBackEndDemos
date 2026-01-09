package com.ks.demo.mosq.entity;

import java.util.Date;

public class PersonEntity {
    private Long id;
    private String name;
    private Integer gender;
    private String address;
    /**
     * 多选项。"职业发展多选项。可选项：收入无上限，培训与发展，职业价值感，行业稳定性，社交与人脉，塑造个人品牌，团队综合素质，终身学习。
     * 例如，全选："1111 1111"，保存为十进制=255，全不选："0000 0000"，保存为十进制=0，只选择培训发展："0000 0010"，保存为十进制=2
     * LONG最大支持64位，最多支持64个多选项的任意选择
     */
    private Long careers;
    private Long interests;

    private Date createTime;
    private Date updateTime;
    private Integer deleted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getCareers() {
        return careers;
    }

    public void setCareers(Long careers) {
        this.careers = careers;
    }

    public Long getInterests() {
        return interests;
    }

    public void setInterests(Long interests) {
        this.interests = interests;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
}
