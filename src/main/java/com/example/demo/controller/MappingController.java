package com.example.demo.controller;

import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.LocalDateTime;
import java.util.Map;

@Controller
public class MappingController {

    @Autowired
    private UserRepository userRepository;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home() {
        return "index";
    }

    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String admin() {
        return "admin";
    }

    @RequestMapping(value = "/performer", method = RequestMethod.GET)
    public String performer() {
        return "performer";
    }

    @RequestMapping(value = "/client", method = RequestMethod.GET)
    public String client() {
        return "client";
    }

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
        User userFromDb = userRepository.findByUsername(user.getUsername());

        if (userFromDb != null) {
            model.put("message", "Username already exists");
            return "registration";
        }

        user.setCreated(LocalDateTime.now());
        user.setActive(true);
        userRepository.save(user);

        return "redirect:/login";
    }
}
