package com.ks.demo.mosq.controller;

import com.ks.demo.mosq.dto.PersonQueryDto;
import com.ks.demo.mosq.dto.PersonQueryResDto;
import com.ks.demo.mosq.dto.PersonSaveDto;
import com.ks.demo.mosq.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/person")
public class PersonController {
    @Autowired
    private PersonService personService;

    @PostMapping("/save")
    public String save(@RequestBody PersonSaveDto personSaveDto) {
        //入参校验

        boolean save = personService.save(personSaveDto);
        return save ? "更新成功" : "更新失败";
    }

    @PostMapping("/query")
    public List<PersonQueryResDto> query(@RequestBody PersonQueryDto personQueryDto) {
        //入参校验
        return personService.query(personQueryDto);
    }
}
