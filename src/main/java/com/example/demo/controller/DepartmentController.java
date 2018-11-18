package com.example.demo.controller;

import com.example.demo.domain.Department;
import com.example.demo.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @RequestMapping(value = "/api/department", method = RequestMethod.GET)
    public List<Department> findAll() {
        return departmentService.findAll();
    }

    @RequestMapping(value = "/api/department", method = RequestMethod.POST)
    public Department create(@RequestBody Department department) {
        return departmentService.create(department);
    }

    @RequestMapping(value = "/api/department/{id}", method = RequestMethod.GET)
    public Department read(@PathVariable Long id) {
        return departmentService.read(id);
    }

    @RequestMapping(value = "/api/department/{id}", method = RequestMethod.PUT)
    public Department update(@PathVariable Long id, @RequestBody Department department) {
        return departmentService.update(id, department);
    }

    @RequestMapping(value = "/api/department/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable Long id) {
        departmentService.delete(id);
    }
}
