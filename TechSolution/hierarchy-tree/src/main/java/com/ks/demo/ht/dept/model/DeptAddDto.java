package com.ks.demo.ht.dept.model;

import lombok.Data;

import java.util.List;

@Data
public class DeptAddDto {

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

}
