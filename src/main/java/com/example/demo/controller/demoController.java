package com.example.demo.controller;

import com.example.demo.domain.AppUser;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class demoController {

    private final UserRepository userRepository;

    @Autowired
    public demoController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RequestMapping(value = "/api/demo", method = RequestMethod.GET)
    public List<AppUser> getAll() {
        return userRepository.findAll();
    }

    @RequestMapping(value = "api/demo", method = RequestMethod.POST)
    public AppUser create(@RequestBody AppUser appUser) {
        return userRepository.save(appUser);
    }

    @RequestMapping(value = "api/demo/{id}", method = RequestMethod.GET)
    public AppUser read(@PathVariable("id") AppUser appUser) {
        return appUser;
    }

    @RequestMapping(value = "api/demo/{id}", method = RequestMethod.PUT)
    public AppUser update(@PathVariable("id") AppUser appUserFromDb, @RequestBody AppUser appUser) {
        BeanUtils.copyProperties(appUser, appUserFromDb, "id");

        return userRepository.save(appUserFromDb);
    }

    @RequestMapping(value = "api/demo/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("id") AppUser appUser) {
        userRepository.delete(appUser);
    }

}
