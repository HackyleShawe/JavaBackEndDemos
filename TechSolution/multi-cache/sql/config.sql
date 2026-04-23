DROP TABLE IF EXISTS sys_config;
CREATE TABLE sys_config
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,

    config_key   VARCHAR(100) UNIQUE NOT NULL COMMENT '配置项唯一标识',
    config_value VARCHAR(500) NOT NULL COMMENT '配置项的值',
    config_desc VARCHAR(1000) DEFAULT '' COMMENT '配置项说明',

    status       BIT          DEFAULT 0 COMMENT '状态：0-无效 1-有效',
    deleted      BIT          DEFAULT 0 COMMENT '0-False-未删除, 1-True-已删除',
    create_by    BIGINT       DEFAULT 0,
    create_time  DATETIME     DEFAULT NOW(),
    update_by    BIGINT       DEFAULT 0,
    update_time  DATETIME     DEFAULT NOW() ON UPDATE NOW()
) COMMENT '参数配置';


INSERT INTO kdb.sys_config (id, config_key, config_value, config_desc, status, deleted, create_by, create_time, update_by, update_time) VALUES (1, 'sys.auth.captcha', '1', '系统登录是否启用验证码:1-启用，0-关闭', true, false, 0, '2026-04-16 14:00:21', 0, '2026-04-16 14:21:01');
INSERT INTO kdb.sys_config (id, config_key, config_value, config_desc, status, deleted, create_by, create_time, update_by, update_time) VALUES (2, 'sys.auth.sms', '1', '系统登录是否启用短信验证码：1-启用，0-关闭', true, false, 0, '2026-04-16 14:00:21', 0, '2026-04-16 14:21:01');
