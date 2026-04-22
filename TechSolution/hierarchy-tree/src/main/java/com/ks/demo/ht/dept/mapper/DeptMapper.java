package com.ks.demo.ht.dept.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ks.demo.ht.dept.model.DeptEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DeptMapper extends BaseMapper<DeptEntity> {

    List<DeptEntity> getSubTree(@Param("ancestorPath") String ancestorPath);

    int updateChildrenPid(@Param("oldPid") long oldPid, @Param("newPid") long newPid);

    int updateSubTreeAncestorPath(@Param("ancestorPath")String ancestorPath, @Param("curPath")String curPath,
                                  @Param("levelDiff")int levelDiff);


    int delWithSubTree(@Param("id") Long id, @Param("ancestorPath") String ancestorPath);
}




