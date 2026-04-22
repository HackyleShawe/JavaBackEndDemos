package com.ks.demo.ht.dept.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ks.demo.ht.dept.mapper.DeptMapper;
import com.ks.demo.ht.dept.model.DeptAddDto;
import com.ks.demo.ht.dept.model.DeptEntity;
import com.ks.demo.ht.dept.model.DeptVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DeptService {
    @Autowired
    private DeptMapper deptMapper;
    @Autowired
    private DeptTreeService deptTreeService;


    /**
     * 新增节点，并返回树
     * 新增顶级节点，则直接新增
     * 新增子节点
     * 新增父节点，等价于新增子节点
     * 不存在新增节点还有子树的这种情况，需要先新增子节点，再移动子树到此节点
     */
    @Transactional
    public List<DeptVo> add(DeptAddDto deptAddDto) {
        DeptEntity deptEntity = new DeptEntity();

        Long pid = deptAddDto.getPid();
        if(pid == null || pid < 1) { //表示添加顶级节点
            deptEntity.setPid(0L);
            deptEntity.setAncestorPath("");
            deptEntity.setLevel(1);
        } else {
            //检查pid是否存在
            DeptEntity existEntity = deptMapper.selectById(pid);
            if(existEntity == null) {
                throw new IllegalArgumentException("父部门不存在");
            }
            deptEntity.setPid(pid);
            deptEntity.setAncestorPath(existEntity.getAncestorPath()+"/"+existEntity.getId());
            deptEntity.setLevel(existEntity.getLevel()+1);
        }
        deptEntity.setName(deptAddDto.getName());
        deptEntity.setWeight(deptAddDto.getWeight());

        int inserted = deptMapper.insert(deptEntity);
        if(inserted != 1) {
            throw new IllegalStateException("新增失败");
        }

        return deptTreeService.buildTree();
    }

    @Transactional
    public List<DeptVo> delWithSubTree(long id) {
        if(id <= 0) {
            return null;
        }
        DeptEntity dept = deptMapper.selectById(id);
        if(dept == null) {
            throw new IllegalArgumentException("节点不存在");
        }

        int deleted = deptMapper.deleteById(dept.getId());
        if(deleted < 1) {
            throw new RuntimeException("删除节点失败");
        }

        boolean exists = this.existsChildren(id);
        if(exists) {
            String ancestorPath = dept.getAncestorPath()+"/"+dept.getId();
            int deletedSubTree = deptMapper.delWithSubTree(id, ancestorPath);
            log.info("删除子树deleted={}", deletedSubTree);
            if(deletedSubTree < 1) {
                throw new RuntimeException("删除子树失败");
            }
        }

        return deptTreeService.buildTree();
    }

    @Transactional
    public List<DeptVo> delWithoutSubTree(long id) {
        if(id <= 0) {
            return null;
        }
        DeptEntity dept = deptMapper.selectById(id);
        if(dept == null) {
            throw new IllegalArgumentException("节点不存在");
        }
        int deleted = deptMapper.deleteById(dept.getId());
        if(deleted < 1) {
            throw new RuntimeException("删除节点失败");
        }

        boolean exists = this.existsChildren(id);
        if (exists) {
            long oldPid = dept.getId();
            long newPid = dept.getPid();
            int updatedPId = deptMapper.updateChildrenPid(oldPid, newPid);
            if (updatedPId < 1) {
                throw new RuntimeException("更新子节点失败");
            }

            String ancestorPath = dept.getAncestorPath();
            String curPath = dept.getAncestorPath() + "/" + dept.getId();
            int updatedChildren = deptMapper.updateSubTreeAncestorPath(ancestorPath, curPath, -1);
            if (updatedChildren < 1) {
                throw new RuntimeException("更新子树失败");
            }
        }

        return deptTreeService.buildTree();
    }

    /**
     * 检查某个节点是否有子节点，有子节点就一定有子树
     */
    private boolean existsChildren(long pid) {
        Long pidCount = deptMapper.selectCount(Wrappers.<DeptEntity>lambdaQuery().eq(DeptEntity::getPid, pid));
        return pidCount != null && pidCount > 0;
    }

    /**
     * 移动节点，子树也同步跟着移动
     * @param nodeId 当前移动的节点是谁
     * @param newParentId 新移动的节点的父节点是谁
     */
    public List<DeptVo> moveWithSubTree(Long nodeId, Long newParentId) {
        if(nodeId == null || newParentId == null) {
            throw new IllegalArgumentException("参与移动的节点不能为空");
        }
        if(nodeId.equals(newParentId)) {
            throw new IllegalArgumentException("不能自己移动到自己");
        }

        DeptEntity node = deptMapper.selectById(nodeId);
        if(node == null) {
            throw new IllegalArgumentException("移动的节点不存在");
        }
        DeptEntity newParent = deptMapper.selectById(newParentId);
        if(newParent == null) {
            throw new IllegalArgumentException("新移动的父节点不存在");
        }

        //判定是否移动到自己的子树，防止回环
        List<DeptEntity> subTreeList = this.getSubTree(nodeId);
        if(CollectionUtil.isNotEmpty(subTreeList)) {
            Set<Long> idSet = subTreeList.stream().map(DeptEntity::getId).collect(Collectors.toSet());
            if(idSet.contains(newParentId)) {
                throw new IllegalArgumentException("不能移动到自己的子树");
            }
        }

        boolean exists = this.existsChildren(node.getId());
        if (exists) {
            String ancestorPath = newParent.getAncestorPath() + "/" + newParentId + "/" + nodeId;
            String curPath = node.getAncestorPath() + "/" + node.getId();
            int levelDiff = (newParent.getLevel() + 1) - node.getLevel();

            // 更新子树
            int updatedChildren = deptMapper.updateSubTreeAncestorPath(ancestorPath, curPath, levelDiff);
            if (updatedChildren < 1) {
                throw new RuntimeException("更新子树失败");
            }
        }

        // 更新当前节点
        node.setPid(newParentId);
        node.setAncestorPath(newParent.getAncestorPath() + "/" + newParentId);
        node.setLevel(newParent.getLevel() + 1);

        int updated = deptMapper.updateById(node);
        if(updated < 1) {
            throw new RuntimeException("更新节点失败");
        }

        return deptTreeService.buildTree();
    }

    /**
     * 移动节点，子树不跟着同步，子树自动升降级
     * @param nodeId 当前移动的节点是谁
     * @param newParentId 新移动的节点的父节点是谁
     */
    public List<DeptVo> moveWithoutSubTree(Long nodeId, Long newParentId) {
        if(nodeId == null || newParentId == null) {
            throw new IllegalArgumentException("参与移动的节点不能为空");
        }
        if(nodeId.equals(newParentId)) {
            throw new IllegalArgumentException("不能自己移动到自己");
        }

        DeptEntity node = deptMapper.selectById(nodeId);
        if(node == null) {
            throw new IllegalArgumentException("移动的节点不存在");
        }
        DeptEntity newParent = deptMapper.selectById(newParentId);
        if(newParent == null) {
            throw new IllegalArgumentException("新移动的父节点不存在");
        }

        boolean exists = this.existsChildren(node.getId());
        if (exists) {
            //获取当前节点的上一级，便于直接子节点挂载
            DeptEntity parent = deptMapper.selectById(node.getPid());

            //更新直接子节点的pid
            long oldPid = node.getId();
            long newPid = node.getPid();
            int updateChildrenPid = deptMapper.updateChildrenPid(oldPid, newPid);
            if(updateChildrenPid < 1) {
                throw new RuntimeException("更新子节点的pid失败");
            }

            //更新子树的AncestorPath和level
            String ancestorPath = node.getAncestorPath();
            String curPath = node.getAncestorPath() + "/" + nodeId;
            //int levelDiff = (newParent.getLevel() + 1) - node.getLevel();
            int updateSubTree = deptMapper.updateSubTreeAncestorPath(ancestorPath, curPath, -1);
            if(updateSubTree < 1) {
                throw new RuntimeException("更新子树失败");
            }
        }

        // 更新当前节点
        node.setPid(newParentId);
        node.setAncestorPath(newParent.getAncestorPath() + "/" + newParentId);
        node.setLevel(newParent.getLevel() + 1);

        int updated = deptMapper.updateById(node);
        if(updated < 1) {
            throw new RuntimeException("更新节点失败");
        }
        return deptTreeService.buildTree();
    }

    /**
     * 获取某个节点的子树节点
     */
    public List<DeptEntity> getSubTree(Long id) {
        if(id == null || id < 1) {
            return null;
        }
        DeptEntity deptEntity = deptMapper.selectById(id);
        List<DeptEntity> subTreeList = deptMapper.getSubTree(deptEntity.getAncestorPath()+"/"+deptEntity.getId());

        return subTreeList;
    }

    /**
     * 获取某个节点的直接子节点
     */
    public List<DeptEntity> getChildren(Long pid) {
        if(pid == null || pid < 1) {
            return null;
        }
        List<DeptEntity> deptEntityList = deptMapper.selectList(Wrappers.<DeptEntity>lambdaQuery().eq(DeptEntity::getPid, pid));
        return deptEntityList;
    }



}
