package com.hackyle.ssm.mapper;

import com.hackyle.ssm.pojo.Person;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonMapper {
    //增
    void addPerson(Person person);

    //删
    void deletePerson(int id);

    //改
    void updatePerson(Person person);

    //查
    List<Person> readAllPerson();
}
