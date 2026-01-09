package com.ks.demo.shiro.dao;

import com.ks.demo.shiro.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public UserEntity loginCheck(String username, String password) {
        String sql = "SELECT * FROM user WHERE username=? and password=?";
        UserEntity user = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<UserEntity>(UserEntity.class), username, password);
        return user;
    }
}
