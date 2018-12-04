package com.example.demo.controller;

import com.example.demo.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasAuthority('ADMIN')")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @RequestMapping(value = "/api/role", method = RequestMethod.GET)
    public String findAll() {
        return roleService.findAll();
    }
}
