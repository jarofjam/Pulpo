package com.example.demo.controller;

import com.example.demo.dto.DepartmentDto;
import com.example.demo.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @RequestMapping(value = "/api/department", method = RequestMethod.GET)
    public List<DepartmentDto> findAll() {
        return departmentService.findAll();
    }

    @RequestMapping(value = "/api/department/{id}", method = RequestMethod.GET)
    public DepartmentDto read(@PathVariable Long id) {
        return departmentService.read(id);
    }
}
