package com.example.demo.controller;

import com.example.demo.dto.DepartmentDto;
import com.example.demo.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasAuthority('ADMIN')")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @RequestMapping(value = "/api/department", method = RequestMethod.GET)
    public List<DepartmentDto> findAll() {
        return departmentService.findAll();
    }

    @RequestMapping(value = "/api/department", method = RequestMethod.POST)
    public DepartmentDto create(@RequestBody DepartmentDto departmentDto) {
        return departmentService.create(departmentDto);
    }

    @RequestMapping(value = "/api/department/{id}", method = RequestMethod.GET)
    public DepartmentDto read(@PathVariable Long id) {
        return departmentService.read(id);
    }

    @RequestMapping(value = "/api/department/{id}", method = RequestMethod.PUT)
    public DepartmentDto update(@PathVariable Long id, @RequestBody DepartmentDto departmentDto) {
        return departmentService.update(id, departmentDto);
    }

    @RequestMapping(value = "/api/department/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable Long id) {
        departmentService.delete(id);
    }
}
