DROP DATABASE IF EXISTS sign_by_mobile;
CREATE DATABASE sign_by_mobile;
USE sign_by_mobile;

DROP TABLE IF EXISTS tb_user;
CREATE TABLE tb_user(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) DEFAULT '',
    password VARCHAR(200) DEFAULT '',
    mobile_phone VARCHAR(20) DEFAULT '',

    `avatar_url` varchar(500) DEFAULT '' COMMENT '头像',
    `nick_name` varchar(50) COMMENT '昵称',
    gender INT DEFAULT 1 COMMENT '0-woman,1-man',
    description VARCHAR(1000) DEFAULT '',
    create_time DATETIME DEFAULT now(),
    update_time DATETIME DEFAULT now() ON UPDATE now(),
    deleted BIT DEFAULT 0 COMMENT '0-no,1-yes'
) COMMENT '手机号用户信息表';

DROP TABLE IF EXISTS tb_sms_record;
CREATE TABLE tb_sms_record(
      id BIGINT PRIMARY KEY AUTO_INCREMENT,
      mobile_phone VARCHAR(20) DEFAULT '',
      sms_code VARCHAR(50) DEFAULT '',
      `status` VARCHAR(100) DEFAULT '',
      remark VARCHAR(1000) DEFAULT '',

      create_time DATETIME DEFAULT now(),
      update_time DATETIME DEFAULT now() ON UPDATE now(),
      deleted BIT DEFAULT 0 COMMENT '0-no,1-yes'
) COMMENT '短信发送记录表';


