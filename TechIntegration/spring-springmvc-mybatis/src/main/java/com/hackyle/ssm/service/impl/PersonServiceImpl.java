package com.hackyle.ssm.service.impl;

import com.hackyle.ssm.mapper.PersonMapper;
import com.hackyle.ssm.pojo.Person;
import com.hackyle.ssm.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("personServiceImpl")
public class PersonServiceImpl implements PersonService {
    @Autowired
    PersonMapper personMapper;

    /**
     * 方便Spring注入
     */
    public void setPersonMapper(PersonMapper personMapper) {
        this.personMapper = personMapper;
    }

    @Override
    public void addPerson(Person person) {
        this.personMapper.addPerson(person);
    }

    @Override
    public void deletePerson(int id) {
        this.personMapper.deletePerson(id);
    }

    @Override
    public void updatePerson(Person person) {
        this.personMapper.updatePerson(person);
    }

    @Override
    public List<Person> readAllPerson() {
        return this.personMapper.readAllPerson();
    }
}
