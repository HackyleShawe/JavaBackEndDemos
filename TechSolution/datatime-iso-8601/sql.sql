DROP TABLE IF EXISTS datetime_demo;
CREATE TABLE datetime_demo
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    time_stamp  TIMESTAMP DEFAULT NULL COMMENT '时间戳、UTC',
    epoch_milli BIGINT    DEFAULT NULL COMMENT 'UTC标准时间的毫秒数，可解决Timestamp的2038问题'
);
