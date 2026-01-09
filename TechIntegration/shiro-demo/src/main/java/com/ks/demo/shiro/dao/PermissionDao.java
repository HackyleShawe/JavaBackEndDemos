package com.ks.demo.shiro.dao;

import com.ks.demo.shiro.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class PermissionDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<PermissionEntity> queryPermissionByUserId(int userId) {
        String sql = "SELECT * FROM user_role WHERE user_id=?";
        List<UserRoleEntity> userRoleEntityList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<UserRoleEntity>(UserRoleEntity.class), userId);
        if(userRoleEntityList.isEmpty()) {
            return null;
        }

        List<Integer> roleIds = userRoleEntityList.stream().map(UserRoleEntity::getRoleId).collect(Collectors.toList());
        if(roleIds.isEmpty()) {
            return null;
        }

        sql = "SELECT * FROM role_permission WHERE role_id in ("; //jdbcTemplate不支持IN子句，使用拼接
        String roleIdStr = "";
        for (Integer rid : roleIds) {
            roleIdStr += rid + ",";
        }
        roleIdStr = roleIdStr.substring(0, roleIdStr.length()-1);
        sql = sql + roleIdStr + ")";
        List<RolePermissionEntity> rolePermissionList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<RolePermissionEntity>(RolePermissionEntity.class));
        if(rolePermissionList.isEmpty()) {
            return null;
        }

        List<Integer> PermissionIds = rolePermissionList.stream().map(RolePermissionEntity::getPermissionId).collect(Collectors.toList());
        if(PermissionIds.isEmpty()) {
            return null;
        }

        sql = "SELECT * FROM permission WHERE id in (";
        String permissionIdStr = "";
        for (Integer pid : PermissionIds) {
            permissionIdStr += pid + ",";
        }
        permissionIdStr = permissionIdStr.substring(0, permissionIdStr.length()-1);
        sql = sql + permissionIdStr + ")";
        List<PermissionEntity> permissionList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<PermissionEntity>(PermissionEntity.class));
        if(permissionList.isEmpty()) {
            return null;
        }

        return permissionList;
    }

}
