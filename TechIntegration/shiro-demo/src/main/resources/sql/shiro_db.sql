DROP DATABASE IF EXISTS shiro_db;
CREATE DATABASE shiro_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE shiro_db;

-- 授权管理，基于RBAC(Role-Based Access Control)
-- 用户表
DROP TABLE IF EXISTS user;
CREATE TABLE user(
	id INT PRIMARY KEY AUTO_INCREMENT,
	username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
	password VARCHAR(255) NOT NULL COMMENT '密码',
	is_locked BIT DEFAULT 0 COMMENT '账户状态：0-正常，1-锁定',

	create_time DATETIME DEFAULT now(),
	update_time DATETIME DEFAULT now() on UPDATE now(),
	is_deleted BIT DEFAULT 0 COMMENT '0-False-未删除, 1-True-已删除'
);

INSERT INTO user(username, password, create_time, update_time)
VALUES ('admin', '123456', CURRENT_TIME(), CURRENT_TIME());

INSERT INTO user(username, password, create_time, update_time)
VALUES ('user1', '123456', CURRENT_TIME(), CURRENT_TIME());

INSERT INTO user(username, password, create_time, update_time)
VALUES ('user2', '123456', CURRENT_TIME(), CURRENT_TIME());

-- 角色表
DROP TABLE IF EXISTS role;
CREATE TABLE role
(
	id INT NOT NULL AUTO_INCREMENT,
	name VARCHAR(128) NOT NULL COMMENT '角色名',
	description VARCHAR(255) DEFAULT NULL COMMENT '描述',

	create_time DATETIME DEFAULT now(),
	update_time DATETIME DEFAULT now() on UPDATE now(),
	is_deleted BIT DEFAULT 0 COMMENT '0-False-未删除, 1-True-已删除',
	PRIMARY KEY (id)
);

INSERT INTO role(name, description) VALUES ('amdin','超级管理员');
insert INTO role(name, description) VALUES ('user1','A级系统管理员');
insert INTO role(name, description) VALUES ('user2','B级系统管理员');


-- 用户角色表
drop TABLE if EXISTS user_role;
create TABLE user_role (
	id INT not NULL AUTO_INCREMENT COMMENT '主键',
	user_id int NOT NULL COMMENT '用户ID',
	role_id int not NULL COMMENT '角色ID',
	create_time DATETIME DEFAULT now(),
	update_time DATETIME DEFAULT now() on UPDATE now(),
	is_deleted BIT DEFAULT 0 COMMENT '0-False-未删除, 1-True-已删除',
	PRIMARY KEY (id),
	INDEX user_id_idx (user_id),
	INDEX role_id_idx (role_id)
);

INSERT INTO user_role(user_id,role_id) VALUES (1,1);
INSERT INTO user_role(user_id,role_id) VALUES (2,2);
INSERT INTO user_role(user_id,role_id) VALUES (3,3);

-- 权限表
drop TABLE IF EXISTS permission;
CREATE TABLE permission (
	id int NOT NULL AUTO_INCREMENT COMMENT '主键',
	path VARCHAR(128) NOT NULL COMMENT '权限所代表的请求路径',
	name VARCHAR(255) NOT NULL COMMENT '权限的名称',
	description VARCHAR(255) DEFAULT NULL COMMENT '描述',
	create_time DATETIME DEFAULT now(),
	update_time DATETIME DEFAULT now() on UPDATE now(),
	is_deleted BIT DEFAULT 0 COMMENT '0-False-未删除, 1-True-已删除',
	PRIMARY KEY (id)
);

INSERT INTO permission(path,name,description) VALUES ('/resource/create','resource:create','增的权限');
INSERT INTO permission(path,name,description) VALUES ('/resource/delete','resource:delete','删的权限');
INSERT INTO permission(path,name,description) VALUES ('/resource/update','resource:update','改的权限');
INSERT INTO permission(path,name,description) VALUES ('/resource/read','resource:read','查的权限');


-- 角色权限关联表
create TABLE role_permission(
	id int NOT NULL AUTO_INCREMENT COMMENT '主键',
	role_id int NOT NULL COMMENT '角色ID',
	permission_id int NOT NULL  COMMENT '权限ID',
	create_time DATETIME DEFAULT now(),
	update_time DATETIME DEFAULT now() on UPDATE now(),
	is_deleted BIT DEFAULT 0 COMMENT '0-False-未删除, 1-True-已删除',
	PRIMARY KEY (id)
);

-- 超级管理员拥有增、删、改、查所有的权限
INSERT INTO role_permission(role_id, permission_id) VALUES (1,1);
INSERT INTO role_permission(role_id, permission_id) VALUES (1,2);
INSERT INTO role_permission(role_id, permission_id) VALUES (1,3);
INSERT INTO role_permission(role_id, permission_id) VALUES (1,4);

-- A级系统管理员拥有增、删权限
INSERT INTO role_permission(role_id, permission_id) VALUES (2,1);
INSERT INTO role_permission(role_id, permission_id) VALUES (2,2);

-- B级系统管理员拥有改、查权限
INSERT INTO role_permission(role_id, permission_id) VALUES (3,3);
INSERT INTO role_permission(role_id, permission_id) VALUES (3,4);

