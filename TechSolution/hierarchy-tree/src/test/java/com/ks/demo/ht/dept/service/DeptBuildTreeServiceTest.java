package com.ks.demo.ht.dept.service;


import com.alibaba.fastjson.JSON;
import com.ks.demo.ht.dept.model.DeptVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class DeptBuildTreeServiceTest {
    @Autowired
    private DeptBuildTreeService deptBuildTreeService;


    @Test
    public void test() {
        //List<DeptVo> deptList = deptTreeService.getDeptList();
        //
        //List<DeptVo> deptVos1 = deptTreeService.buildTreeByFor(deptList); 不能每个都复用同一个list，因为每个都改变了list的引用
        //System.out.println(JSON.toJSONString(deptVos1));
        //List<DeptVo> deptVos2 = deptTreeService.buildTreeByRe(deptList);
        //System.out.println(JSON.toJSONString(deptVos2));

        List<DeptVo> deptVos1 = deptBuildTreeService.buildTreeByFor(deptBuildTreeService.getDeptList());
        System.out.println("deptVos1=" + JSON.toJSONString(deptVos1));
        List<DeptVo> deptVos2 = deptBuildTreeService.buildTreeByRe(deptBuildTreeService.getDeptList());
        System.out.println("deptVos2=" + JSON.toJSONString(deptVos2));

        List<DeptVo> deptVos3 = deptBuildTreeService.buildTreeByMap(deptBuildTreeService.getDeptList());
        System.out.println("deptVos3=" + JSON.toJSONString(deptVos3));
        List<DeptVo> deptVos4 = deptBuildTreeService.buildTreeByStack(deptBuildTreeService.getDeptList());
        System.out.println("deptVos4==="+JSON.toJSONString(deptVos4));
        List<DeptVo> deptVos5 = deptBuildTreeService.buildTreeByHutool(deptBuildTreeService.getDeptList());
        System.out.println("deptVos5="+JSON.toJSONString(deptVos5));

        List<DeptVo> deptListVos1 = deptBuildTreeService.tree2listByRe(deptVos3);
        System.out.println(JSON.toJSONString(deptListVos1));
        List<DeptVo> deptListVos2 = deptBuildTreeService.tree2listByStack(deptVos4);
        System.out.println(JSON.toJSONString(deptListVos2));
    }
}
