package com.ks.demo.ht.dept.model;

import lombok.Data;

import java.util.List;

@Data
public class DeptVo {
    /**
     * 部门ID
     */
    private Long id;

    /**
     * 部门名称
     */
    private String name;

    /**
     * 父部门ID，顶级部门为0
     */
    private Long pid;

    /**
     * 排序权重，值越小越靠前
     */
    private Integer weight;

    /**
     * 祖级节点，从根节点到当前节点的父节点（只包含父级，不包含本身），例如：/1/3
     */
    private String ancestorPath;

    /**
     * 层级，从1开始
     */
    private Integer level;

    /**
     * 子节点
     */
    private List<DeptVo> children;


}
