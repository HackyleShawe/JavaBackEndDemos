CREATE DATABASE IF NOT EXISTS kdb;
use kdb;

DROP TABLE IF EXISTS person;
CREATE TABLE person (
    id BIGINT AUTO_INCREMENT,
    name VARCHAR(50) DEFAULT NULL COMMENT '姓名',
    gender INT DEFAULT NULL COMMENT '性别，0-女，1-男',
    address VARCHAR(128) DEFAULT NULL COMMENT '地址',
    -- 多选项。"职业发展多选项。可选项：收入无上限，培训与发展，职业价值感，行业稳定性，社交与人脉，塑造个人品牌，团队综合素质，终身学习。
    -- 例如，全选："1111 1111"，保存为十进制=255，全不选："0000 0000"，保存为十进制=0，只选择培训发展："0000 0010"，保存为十进制=2
    -- LONG最大支持64位，最多支持64个多选项的任意选择
    careers BIGINT DEFAULT NULL COMMENT '职业发展多选项',
    interests BIGINT DEFAULT NULL COMMENT '兴趣爱好多选项',
    create_time DATETIME DEFAULT NULL,
    update_time DATETIME DEFAULT NULL,
    deleted INT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY(id)
);



