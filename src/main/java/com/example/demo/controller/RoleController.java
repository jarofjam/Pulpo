package com.example.demo.controller;

import com.example.demo.domain.Role;
import com.example.demo.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RoleController {

    @Autowired
    private RoleService roleService;

    @RequestMapping(value = "/api/role", method = RequestMethod.GET)
    public List<Role> getAll() {
        return roleService.getAll();
    }

    @RequestMapping(value = "/api/role", method = RequestMethod.POST)
    public Role create(@RequestBody Role role) {
        return roleService.create(role);
    }

    @RequestMapping(value = "/api/role/{id}", method = RequestMethod.GET)
    public Role read(@PathVariable Long id) {
        return roleService.read(id);
    }

    @RequestMapping(value = "/api/role/{id}", method = RequestMethod.PUT)
    public Role update(@PathVariable Long id, @RequestBody Role role) {
        return roleService.update(id, role);
    }

    @RequestMapping(value = "/api/role/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable Long id) {
        roleService.delete(id);
    }
}
