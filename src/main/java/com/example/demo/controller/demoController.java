package com.example.demo.controller;

import com.example.demo.domain.AppUser;
import com.example.demo.repository.AppUserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class demoController {

    private final AppUserRepository appUserRepository;

    @Autowired
    public demoController(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @RequestMapping(value = "/api/user", method = RequestMethod.GET)
    public List<AppUser> getAll() {
        return appUserRepository.findAll();
    }

    @RequestMapping(value = "api/user", method = RequestMethod.POST)
    public AppUser create(@RequestBody AppUser appUser) {
        return appUserRepository.save(appUser);
    }

    @RequestMapping(value = "api/user/{id}", method = RequestMethod.GET)
    public AppUser read(@PathVariable("id") AppUser appUser) {
        return appUser;
    }

    @RequestMapping(value = "api/user/{id}", method = RequestMethod.PUT)
    public AppUser update(@PathVariable("id") AppUser appUserFromDb, @RequestBody AppUser appUser) {
        BeanUtils.copyProperties(appUser, appUserFromDb, "id");

        return appUserRepository.save(appUserFromDb);
    }

    @RequestMapping(value = "api/user/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("id") AppUser appUser) {
        appUserRepository.delete(appUser);
    }

}
