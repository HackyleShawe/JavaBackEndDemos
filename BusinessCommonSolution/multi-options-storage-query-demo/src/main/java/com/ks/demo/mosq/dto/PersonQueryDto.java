package com.ks.demo.mosq.dto;

public class PersonQueryDto {
    private String queryName;
    /**
     * 职业发展选项：1-收入无上限，2-培训与发展，3-职业价值感，4-行业稳定性，5-社交与人脉，6-塑造个人品牌，7-团队综合素质，8-终身学习
     * 接收前端传递的数据，要查询那些就传递对应的编号，例如：1,2,3,4
     */
    private String queryCareerOption;
    /**
     * 兴趣爱好多选项：1-编程，2-听音乐唱歌，3-篮球，4-玩游戏，5-看电影，6-享美食，7-健身，8-旅游，9-看书，10-写作
     * 接收前端传递的数据，要查询那些就传递对应的编号，例如：1,2,3,4,8,9,10
     */
    private String queryInterestOption;

    public String getQueryName() {
        return queryName;
    }

    public void setQueryName(String queryName) {
        this.queryName = queryName;
    }

    public String getQueryCareerOption() {
        return queryCareerOption;
    }

    public void setQueryCareerOption(String queryCareerOption) {
        this.queryCareerOption = queryCareerOption;
    }

    public String getQueryInterestOption() {
        return queryInterestOption;
    }

    public void setQueryInterestOption(String queryInterestOption) {
        this.queryInterestOption = queryInterestOption;
    }
}
