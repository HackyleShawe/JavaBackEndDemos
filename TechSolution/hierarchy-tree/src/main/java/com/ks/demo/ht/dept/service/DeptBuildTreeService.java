package com.ks.demo.ht.dept.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeUtil;
import com.ks.demo.ht.dept.mapper.DeptMapper;
import com.ks.demo.ht.dept.model.DeptEntity;
import com.ks.demo.ht.dept.model.DeptVo;
import com.ks.demo.ht.util.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import java.util.stream.Collectors;

@Service
public class DeptBuildTreeService {
    @Autowired
    private DeptMapper deptMapper;

    public List<DeptVo> buildTree() {
        List<DeptEntity> deptEntities = deptMapper.selectList(null);
        List<DeptVo> deptVos = BeanCopyUtils.copyList(deptEntities, DeptVo.class);
        List<DeptVo> deptTreeList = this.buildTreeByMap(deptVos);
        return deptTreeList;
    }

    public List<DeptVo> buildTree(List<DeptVo> deptVos, long pid) {
        List<DeptVo> res = new ArrayList<>();
        if(CollectionUtils.isEmpty(deptVos)){
            return res;
        }
        Map<Long, List<DeptVo>> pidMap = deptVos.stream().collect(Collectors.groupingBy(DeptVo::getPid));

        for (DeptVo deptEntity : deptVos) {
            if(pid == deptEntity.getId()) {
                res.add(deptEntity);
            }
            List<DeptVo> children = pidMap.get(deptEntity.getId());
            deptEntity.setChildren(CollectionUtils.isEmpty(children) ? new ArrayList<>() : children);
        }

        return res;
    }

    public List<DeptVo> buildTreeByFor(List<DeptVo> deptList) {
        List<DeptVo> res = new ArrayList<>();
        if(CollectionUtils.isEmpty(deptList)){
            return res;
        }

        for (DeptVo deptEntity : deptList) {
            if(deptEntity.getPid() == 0L) {
                res.add(deptEntity);
            }

            for (DeptVo entity : deptList) {
                if(deptEntity.getChildren() == null) {
                    deptEntity.setChildren(new ArrayList<>());
                }
                if(Objects.equals(entity.getPid(), deptEntity.getId())) {
                    deptEntity.getChildren().add(entity);
                }
            }
        }

        return res;
    }


    public List<DeptVo> buildTreeByMap(List<DeptVo> deptList) {
        List<DeptVo> res = new ArrayList<>();
        if(CollectionUtils.isEmpty(deptList)){
            return res;
        }
        Map<Long, List<DeptVo>> pidMap = deptList.stream().collect(Collectors.groupingBy(DeptVo::getPid));

        for (DeptVo deptEntity : deptList) {
            if(deptEntity.getPid() == 0L) {
                res.add(deptEntity);
            }
            List<DeptVo> children = pidMap.get(deptEntity.getId());
            deptEntity.setChildren(CollectionUtils.isEmpty(children) ? new ArrayList<>() : children);
        }

        return res;
    }

    public List<DeptVo> buildTreeByRe(List<DeptVo> deptList) {
        List<DeptVo> res = new ArrayList<>();
        if(CollectionUtils.isEmpty(deptList)){
            return res;
        }

        Map<Long, List<DeptVo>> pidMap = deptList.stream().collect(Collectors.groupingBy(DeptVo::getPid));

        List<DeptVo> rootDeptList = pidMap.get(0L); //获取根节点
        for (DeptVo deptEntity : rootDeptList) {
            findChildren(deptEntity, pidMap);
        }

        return rootDeptList;
    }
    private void findChildren(DeptVo deptEntity, Map<Long, List<DeptVo>> pidMap) {
        if(deptEntity == null) {
            return;
        }

        List<DeptVo> chaild = pidMap.get(deptEntity.getId());
        deptEntity.setChildren(CollectionUtils.isEmpty(chaild) ? new ArrayList<>() : chaild);
        for (DeptVo child : deptEntity.getChildren()) {
            findChildren(child, pidMap);
        }
    }

    public List<DeptVo> buildTreeByStack(List<DeptVo> deptList) {
        List<DeptVo> res = new ArrayList<>();
        if(CollectionUtils.isEmpty(deptList)){
            return res;
        }

        Map<Long, List<DeptVo>> pidMap = deptList.stream().collect(Collectors.groupingBy(DeptVo::getPid));
        Stack<DeptVo> stack = new Stack<>();
        List<DeptVo> rootDeptList = pidMap.get(0L); //获取根节点
        stack.addAll(rootDeptList);

        while (!stack.isEmpty()) {
            DeptVo deptEntity = stack.pop();

            List<DeptVo> child = pidMap.get(deptEntity.getId());
            deptEntity.setChildren(CollectionUtils.isEmpty(child) ? new ArrayList<>() : child);

            if(CollectionUtil.isNotEmpty(child)) {
                stack.addAll(child);
            }
        }

        return rootDeptList;
    }


    /**
     * 主要步骤：
     * 传入原始list、指定跟节点，建立Hutool的节点与我们传入节点的字段映射关系，因为hutool并不知道我们的节点字段是什么样子的
     */
    public List<DeptVo> buildTreeByHutool(List<DeptVo> deptList) {
        List<Tree<Long>> treeList = TreeUtil.build(
                deptList,
                0L,
                //把我们的节点转换为hutool的节点，因为hutool并不知道我们的节点字段是什么样子的
                (sourceNode, targetNode) -> {
                    targetNode.setId(sourceNode.getId());
                    targetNode.setParentId(sourceNode.getPid());
                    targetNode.setWeight(sourceNode.getWeight());
                    targetNode.setName(sourceNode.getName());
                }
        );

        //把Hutool的树节点字段成我们的
        return treeList.stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }
    private DeptVo convert(Tree<Long> tree) {
        DeptVo dept = new DeptVo();
        dept.setId(tree.getId());
        dept.setPid(tree.getParentId());
        dept.setName(tree.getName().toString());

        Object weight = tree.getWeight();
        if (weight != null) {
            dept.setWeight(Integer.parseInt(weight.toString()));
        }

        // 递归处理 children
        if (tree.getChildren() != null && !tree.getChildren().isEmpty()) {
            List<DeptVo> children = tree.getChildren()
                    .stream()
                    .map(this::convert)
                    .collect(Collectors.toList());

            dept.setChildren(children);
        }

        return dept;
    }

    public List<DeptVo> getDeptList() {
        //String deptJson = "[{\"id\":1,\"pid\":0,\"name\":\"总部\",\"weight\":1,\"path\":\"/1\",\"level\":1}" +
        //        "{\"id\":2,\"pid\":1,\"name\":\"技术中心\",\"weight\":1,\"path\":\"/1/2\",\"level\":2}" +
        //        "{\"id\":3,\"pid\":1,\"name\":\"产品中心\",\"weight\":2,\"path\":\"/1/3\",\"level\":2}" +
        //        "{\"id\":4,\"pid\":1,\"name\":\"运营中心\",\"weight\":3,\"path\":\"/1/4\",\"level\":2}" +
        //        "{\"id\":5,\"pid\":2,\"name\":\"后端部\",\"weight\":1,\"path\":\"/1/2/5\",\"level\":3}" +
        //        "{\"id\":6,\"pid\":2,\"name\":\"前端部\",\"weight\":2,\"path\":\"/1/2/6\",\"level\":3}" +
        //        "{\"id\":7,\"pid\":2,\"name\":\"测试部\",\"weight\":3,\"path\":\"/1/2/7\",\"level\":3}" +
        //        "{\"id\":8,\"pid\":3,\"name\":\"产品部\",\"weight\":1,\"path\":\"/1/3/8\",\"level\":3}" +
        //        "{\"id\":9,\"pid\":3,\"name\":\"设计部\",\"weight\":2,\"path\":\"/1/3/9\",\"level\":3}" +
        //        "{\"id\":10,\"pid\":4,\"name\":\"运营部\",\"weight\":1,\"path\":\"/1/4/10\",\"level\":3}]";
        //return JSONArray.parseArray(deptJson, DeptVo.class);
        List<DeptEntity> deptEntities = deptMapper.selectList(null);
        return BeanCopyUtils.copyList(deptEntities, DeptVo.class);
    }

    /**
     * 层级树转list，递归实现
     */
    public List<DeptVo> tree2listByRe(List<DeptVo> deptTreeList) {
        if(CollectionUtils.isEmpty(deptTreeList)){
            return Collections.emptyList();
        }

        List<DeptVo> res = new ArrayList<>();
        for (DeptVo deptVo : deptTreeList) {
            findChildren(deptVo, res);
        }

        return res;
    }

    private void findChildren(DeptVo deptVo, List<DeptVo> res) {
        if(deptVo == null) {
            return;
        }

        List<DeptVo> children = deptVo.getChildren();
        deptVo.setChildren(null);
        res.add(deptVo);

        if(CollectionUtil.isNotEmpty(children)) {
            for (DeptVo child : children) {
                findChildren(child, res);
            }
        }
    }


    /**
     * 层级树转list，栈实现
     */
    public List<DeptVo> tree2listByStack(List<DeptVo> deptTreeList) {
        if(CollectionUtils.isEmpty(deptTreeList)){
            return Collections.emptyList();
        }

        List<DeptVo> res = new ArrayList<>();
        Stack<DeptVo> stack = new Stack<>();

        for (DeptVo deptVo : deptTreeList) {
            //如果有子树，当前节点压栈
            if(CollectionUtil.isNotEmpty(deptVo.getChildren())) {
                stack.add(deptVo);
            } else {
                res.add(deptVo);
            }
        }

        while (!stack.isEmpty()) {
            DeptVo deptVo = stack.pop();
            List<DeptVo> children = deptVo.getChildren();
            deptVo.setChildren(null);
            res.add(deptVo);

            for (DeptVo child : children) {
                //如果有子树，当前节点压栈
                if(CollectionUtil.isNotEmpty(child.getChildren())) {
                    stack.add(child);
                } else {
                    res.add(child);
                }
            }
        }

        return res;
    }


}
