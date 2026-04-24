package com.ks.demo.ht.dept.service;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSONArray;
import com.ks.demo.ht.dept.model.DeptAddDto;
import com.ks.demo.ht.dept.model.DeptEntity;
import com.ks.demo.ht.dept.model.DeptVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class DeptService {

    @Autowired
    private DeptTreeCrudService deptTreeCrudService;
    @Autowired
    private DeptCacheService deptCacheService;
    @Autowired
    private DeptBuildTreeService deptBuildTreeService;

    public List<DeptVo> add(DeptAddDto deptAddDto) {
        DeptEntity deptEntity = deptTreeCrudService.add(deptAddDto);
        List<DeptVo> deptTree = deptBuildTreeService.buildTree();

        //清除缓存
        deptCacheService.putTree(deptTree);
        //新增了节点，意味着更改了树节点，因为不知道这个节点是否被其他子树、子节点缓存，所以需要全部删除
        deptCacheService.evictAllChildren();
        deptCacheService.evictAllSubTree();
        deptCacheService.evict(deptEntity.getId()); //新增的节点，按理来说不可能有，还是删除以前，防止脏数据

        return deptTree;
    }

    public List<DeptVo> delWithSubTree(long id) {
        //先查出这个节点id所在的子树，防止下面删除节点构建树时被覆盖
        List<DeptEntity> subTreeList = deptTreeCrudService.getSubTreeList(id);

        List<DeptVo> deptVos = deptTreeCrudService.delWithSubTree(id);

        deptCacheService.putTree(deptVos);
        //更改了树节点，因为不知道这个节点是否被其他子树、子节点缓存，所以需要全部删除
        deptCacheService.evictAllChildren();
        deptCacheService.evictAllSubTree();

        //清除缓存的子树节点信息
        deptCacheService.evict(id);
        subTreeList.stream().map(DeptEntity::getId).forEach(deptId -> {
                    deptCacheService.evict(deptId);
        });

        return deptVos;
    }

    public List<DeptVo> delWithoutSubTree(long id) {
        List<DeptVo> deptVos = deptTreeCrudService.delWithoutSubTree(id);

        deptCacheService.putTree(deptVos);
        //更改了树节点，因为不知道这个节点是否被其他子树、子节点缓存，所以需要全部删除
        deptCacheService.evictAllChildren();
        deptCacheService.evictAllSubTree();
        deptCacheService.evict(id);

        return deptVos;
    }

    public List<DeptVo> moveWithSubTree(long nodeId, long newParentId) {
        List<DeptVo> deptVos = deptTreeCrudService.moveWithSubTree(nodeId, newParentId);

        deptCacheService.putTree(deptVos);
        //更改了树节点，因为不知道这个节点是否被其他子树、子节点缓存，所以需要全部删除
        deptCacheService.evictAllChildren();
        deptCacheService.evictAllSubTree();
        deptCacheService.evict(nodeId);

        return deptVos;
    }


    public List<DeptVo> moveWithoutSubTree(long nodeId, long newParentId) {
        List<DeptVo> deptVos = deptTreeCrudService.moveWithoutSubTree(nodeId, newParentId);

        deptCacheService.putTree(deptVos);
        //更改了树节点，因为不知道这个节点是否被其他子树、子节点缓存，所以需要全部删除
        deptCacheService.evictAllChildren();
        deptCacheService.evictAllSubTree();
        deptCacheService.evict(nodeId);

        return deptVos;
    }

    public List<DeptVo> getTree() {
        String deptJson = deptCacheService.getTree();
        if(StringUtils.isNotBlank(deptJson)) {
            return JSONArray.parseArray(deptJson, DeptVo.class);
        }

        List<DeptVo> deptVos = deptBuildTreeService.buildTree();
        deptCacheService.putTree(deptVos);

        return deptVos;
    }


    public List<DeptVo> getChildren(long pid) {
        List<DeptVo> children = deptCacheService.getChildren(pid);
        if(CollectionUtil.isNotEmpty(children)) {
            return children;
        }

        List<DeptVo> deptVos = deptTreeCrudService.getChildren(pid);
        deptCacheService.putChildren(pid, deptVos);

        return deptVos;
    }

    public List<DeptVo> getSubTree(long id) {
        List<DeptVo> subTree = deptCacheService.getSubTree(id);
        if(CollectionUtil.isNotEmpty(subTree)) {
            return subTree;
        }

        List<DeptVo> deptVos = deptTreeCrudService.getSubTree(id);
        deptCacheService.putSubTree(id, deptVos);

        return deptVos;
    }

    public DeptVo get(long id) {
        DeptVo deptVo = deptCacheService.get(id);
        if(deptVo != null) {
            return deptVo;
        }

        DeptVo deptVoRes = deptTreeCrudService.get(id);
        if(deptVoRes != null) {
            deptCacheService.put(Collections.singletonList(deptVoRes));
        }

        return deptVoRes;
    }
}
