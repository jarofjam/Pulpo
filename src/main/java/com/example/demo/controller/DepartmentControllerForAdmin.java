package com.example.demo.controller;

import com.example.demo.dto.DepartmentDto;
import com.example.demo.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasAuthority('ADMIN')")
public class DepartmentControllerForAdmin {
    @Autowired
    DepartmentService departmentService;

    @RequestMapping(value = "/api/department", method = RequestMethod.POST)
    public DepartmentDto create(@RequestBody DepartmentDto departmentDto) {
        return departmentService.create(departmentDto);
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
