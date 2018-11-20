package com.example.demo.controller;

import com.example.demo.domain.User;
import com.example.demo.dto.UserDto;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/api/user", method = RequestMethod.GET)
    public List<UserDto> getAll() {
        return userService.findAll();
    }

    @RequestMapping(value = "/api/user", method = RequestMethod.POST)
    public UserDto create(@RequestBody UserDto userDto) {
        return userService.create(userDto);
    }

    @RequestMapping(value = "/api/user/{id}", method = RequestMethod.GET)
    public UserDto read(@PathVariable Long id) {
        return userService.read(id);
    }

    @RequestMapping(value = "/api/user/{id}", method = RequestMethod.PUT)
    public UserDto update(@PathVariable Long id, @RequestBody UserDto userDto) {
        return userService.update(id, userDto);
    }

    @RequestMapping(value = "/api/user/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }

}
