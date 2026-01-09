package com.ks.demo.mosq.dao;

import com.ks.demo.mosq.entity.PersonEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PersonDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean add(PersonEntity personEntity) {
        String sql = "insert into person(id,name,gender,address,careers,interests,create_time,update_time,deleted) " +
                "values(?,?,?,?,?,?,?,?,?)";
        Object[] sqlArgs = {System.currentTimeMillis(), personEntity.getName(), personEntity.getGender(),
                    personEntity.getAddress(), personEntity.getCareers(), personEntity.getInterests(),
                personEntity.getCreateTime(), personEntity.getUpdateTime(), personEntity.getDeleted()};

        return jdbcTemplate.update(sql, sqlArgs) == 1;
    }

    public boolean remove(Long id) {
        String sql = "update person set deleted = 1 where id = ?";
        return jdbcTemplate.update(sql, id) == 1;
    }

    public boolean update(PersonEntity personEntity) {
        if(personEntity.getId() == null) {
            return false;
        }

        String sql = "update person set name=?,gender=?,address=?,careers=?,interests=?,update_time=? " +
                "where id = ?";
        Object[] sqlArgs = {personEntity.getName(), personEntity.getGender(),
                personEntity.getAddress(), personEntity.getCareers(), personEntity.getInterests(),
                personEntity.getUpdateTime(), personEntity.getDeleted(),
                personEntity.getId()};

        return jdbcTemplate.update(sql, sqlArgs) == 1;
    }

    public List<PersonEntity> query(PersonEntity personEntity) {
        if(null == personEntity) {
            return new ArrayList<>();
        }

        String sql = "select * from person where deleted = 0 ";
        if(null != personEntity.getName() && !"".equals(personEntity.getName())) {
            sql += " AND name LIKE '%" + personEntity.getName() + "%'";
        }
        if(null != personEntity.getCareers()) {
            sql += " AND careers & " + personEntity.getCareers() + " = " + personEntity.getCareers();
        }
        if(null != personEntity.getInterests()) {
            sql += " AND interests & " + personEntity.getInterests() + " = " + personEntity.getInterests();
        }
        System.out.println("query sql :" + sql);

        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<PersonEntity>(PersonEntity.class));
    }
}
