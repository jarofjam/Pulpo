package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/api/user", method = RequestMethod.GET)
    public List<User> getAll() {
        return userService.findAll();
    }

    @RequestMapping(value = "/api/user", method = RequestMethod.POST)
    public User create(@RequestBody User user) {
        return userService.create(user);
    }

    @RequestMapping(value = "/api/user/{id}", method = RequestMethod.GET)
    public User read(@PathVariable Long id) {
        return userService.read(id);
    }

    @RequestMapping(value = "/api/user/{id}", method = RequestMethod.PUT)
    public User update(@PathVariable Long id, @RequestBody User user) {
        return userService.update(id, user);
    }

    @RequestMapping(value = "/api/user/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }

}
