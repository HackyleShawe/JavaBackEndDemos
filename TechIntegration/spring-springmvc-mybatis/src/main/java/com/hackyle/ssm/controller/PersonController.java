package com.hackyle.ssm.controller;

import com.hackyle.ssm.pojo.Person;
import com.hackyle.ssm.service.PersonService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/person")
public class PersonController {
    @Autowired
    private PersonService personService;

    @RequestMapping("/addPerson")
    public String addPerson(Person person, Model model) {
        System.out.println("/person/addPerson");
        System.out.println("Parameter: "+person.toString());

        personService.addPerson(person);

        model.addAttribute("info","添加成功");

        return "success";
    }



    @RequestMapping("/deletePerson")
    public String deletePerson(@Param("id") int id, Model model) {
        System.out.println("person/deletePerson");
        System.out.println("Parameter: "+id);

        personService.deletePerson(id);

        model.addAttribute("info", "删除成功");
        return "success";
    }


    @RequestMapping("/updatePerson")
    public String updatePerson(Person person, Model model) {
        System.out.println("/person/updatePerson");
        System.out.println("Parameter: "+person.toString());

        personService.updatePerson(person);

        model.addAttribute("info","修改成功");

        return "success";
    }

    @RequestMapping("/readAllPerson")
    public String readAllPerson(Model model) {
        List<Person> personList = personService.readAllPerson();
        model.addAttribute("info",personList);
        return "success";
    }

}
