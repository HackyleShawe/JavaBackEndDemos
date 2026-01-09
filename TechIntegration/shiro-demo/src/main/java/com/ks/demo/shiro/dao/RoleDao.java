package com.ks.demo.shiro.dao;

import com.ks.demo.shiro.entity.RoleEntity;
import com.ks.demo.shiro.entity.UserRoleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class RoleDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<RoleEntity> queryRoleByUserId(int userId) {
        String sql = "SELECT * FROM user_role WHERE user_id=?";
        List<UserRoleEntity> userRoleEntityList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<UserRoleEntity>(UserRoleEntity.class), userId);
        if(userRoleEntityList.isEmpty()) {
            return null;
        }

        List<Integer> roleIds = userRoleEntityList.stream().map(UserRoleEntity::getRoleId).collect(Collectors.toList());
        if(roleIds.isEmpty()) {
            return null;
        }

        sql = "SELECT * FROM role WHERE id in (?)";
        String roleIdStr = "";
        for (Integer roleId : roleIds) {
            roleIdStr += roleId + ",";
        }
        List<RoleEntity> roleEntityList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<RoleEntity>(RoleEntity.class), roleIdStr);

        return roleEntityList;
    }
}
