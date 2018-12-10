package com.example.demo.controller;

import com.example.demo.domain.Role;
import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

@Controller
public class MappingController {

    @Autowired
    private UserRepository userRepository;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home() {
        return "index";
    }

//    @PreAuthorize("hasAuthority('ADMIN')")
//    @RequestMapping(value = "/admin", method = RequestMethod.GET)
//    public String admin() {
//        return "admin";
//    }
    @PreAuthorize("hasAuthority('MODERATOR')")
    @RequestMapping(value = "/moderator", method = RequestMethod.GET)
    public String moderator() {
        return "moderator";
    }

    @PreAuthorize("hasAuthority('PERFORMER')")
    @RequestMapping(value = "/performer", method = RequestMethod.GET)
    public String performer() {
        return "performer";
    }

    @PreAuthorize("hasAuthority('APPLICANT')")
    @RequestMapping(value = "/applicant", method = RequestMethod.GET)
    public String applicant() {
        return "applicant";
    }

    @PreAuthorize("hasAuthority('APPLICANT')")
    @RequestMapping(value = "/request", method = RequestMethod.GET)
    public String request() {
        return "request";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public String registration() {
        return "registration";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String addUser(User user, Map<String, Object> model) {
        user.setCreated(LocalDateTime.now());
        user.setActive(true);
        user.setRoles(Collections.singleton(Role.ADMIN));
        userRepository.save(user);
        return "redirect:/login";
    }
}
