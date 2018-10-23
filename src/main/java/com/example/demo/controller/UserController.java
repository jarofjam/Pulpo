package com.example.demo.controller;

import com.example.demo.domain.AppUser;
import com.example.demo.domain.Views;
import com.example.demo.repository.AppUserRepository;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class UserController {

    private final AppUserRepository appUserRepository;

    @Autowired
    public UserController(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @RequestMapping(value = "api/user", method = RequestMethod.GET)
    @JsonView(Views.userInfo.class)
    public List<AppUser> getAll() {
        return appUserRepository.findAll();
    }

    @RequestMapping(value = "api/user", method = RequestMethod.POST)
    @JsonView(Views.userInfo.class)
    public AppUser create(@RequestBody AppUser appUser) {
        appUser.setJoined(LocalDate.now());
        return appUserRepository.save(appUser);
    }

    @RequestMapping(value = "api/user/{id}", method = RequestMethod.GET)
    @JsonView(Views.fullInfo.class)
    public AppUser read(@PathVariable("id") AppUser appUser) {
        return appUser;
    }

    @RequestMapping(value = "api/user/{id}", method = RequestMethod.PUT)
    @JsonView(Views.userInfo.class)
    public AppUser update(@PathVariable("id") AppUser appUserFromDb, @RequestBody AppUser appUser) {
        BeanUtils.copyProperties(appUser, appUserFromDb, "id");

        return appUserRepository.save(appUserFromDb);
    }

    @RequestMapping(value = "api/user/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("id") AppUser appUser) {
        appUserRepository.delete(appUser);
    }

}
