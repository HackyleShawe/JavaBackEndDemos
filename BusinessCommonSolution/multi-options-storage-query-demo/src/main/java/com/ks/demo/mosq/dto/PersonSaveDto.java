package com.ks.demo.mosq.dto;

public class PersonSaveDto {
    private Long id;
    private String name;
    private Integer gender;
    private String address;
    /**
     * 职业发展选项：1-收入无上限，2-培训与发展，3-职业价值感，4-行业稳定性，5-社交与人脉，6-塑造个人品牌，7-团队综合素质，8-终身学习
     * 接收前端传递的数据，选了那些就传递对应的编号，例如：1,2,3,4
     */
    private String careerOption;
    /**
     * 兴趣爱好多选项：1-编程，2-听音乐唱歌，3-篮球，4-玩游戏，5-看电影，6-享美食，7-健身，8-旅游，9-看书，10-写作
     * 接收前端传递的数据，选了那些就传递对应的编号，例如：1,2,3,4,8,9,10
     */
    private String interestOption;

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

    public String getCareerOption() {
        return careerOption;
    }

    public void setCareerOption(String careerOption) {
        this.careerOption = careerOption;
    }

    public String getInterestOption() {
        return interestOption;
    }

    public void setInterestOption(String interestOption) {
        this.interestOption = interestOption;
    }
}
