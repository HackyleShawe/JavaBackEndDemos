DROP TABLE IF EXISTS dept;
CREATE TABLE dept
(
    id     BIGINT PRIMARY KEY AUTO_INCREMENT,
    name   VARCHAR(100) DEFAULT '' COMMENT '部门名称',
    pid    BIGINT       DEFAULT 0 COMMENT '父部门ID，顶级部门为0',
    weight INT          DEFAULT 0 COMMENT '排序权重，值越小越靠前',
    ancestor_path   VARCHAR(500) DEFAULT '' COMMENT '祖级节点，从根节点到当前节点的父节点（只包含父级，不包含本身），例如：/1/3',
    level  INT          DEFAULT 1 COMMENT '层级，从1开始',

    INDEX idx_pid (pid),
    INDEX idx_ancestor_path (ancestor_path)
);
INSERT INTO dept (id, pid, name, weight, ancestor_path, level) VALUES
-- 顶级
(1, 0, '总部', 1, '', 1),

-- 一级部门
(2, 1, '技术中心', 1, '/1', 2),
(3, 1, '产品中心', 2, '/1', 2),
(4, 1, '运营中心', 3, '/1', 2),

-- 技术中心子部门
(5, 2, '后端部', 1, '/1/2', 3),
(6, 2, '前端部', 2, '/1/2', 3),
(7, 2, '测试部', 3, '/1/2', 3),

-- 产品中心子部门
(8, 3, '产品部', 1, '/1/3', 3),
(9, 3, '设计部', 2, '/1/3', 3),

-- 运营中心子部门
(10, 4, '运营部', 1, '/1/4', 3),

(11, 5, 'Java开发', 1, '/1/2/5', 4),
(12, 5, 'Go开发', 1, '/1/2/5', 4),
(13, 5, 'Python开发', 3, '/1/2/5', 4);



