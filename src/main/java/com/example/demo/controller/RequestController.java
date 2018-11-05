package com.example.demo.controller;

import com.example.demo.domain.Request;
import com.example.demo.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RequestController {

    @Autowired
    RequestService requestService;

    @RequestMapping(value = "api/request", method = RequestMethod.GET)
    public List<Request> getAll() {
        return requestService.getAll();
    }

    @RequestMapping(value = "api/request", method = RequestMethod.POST)
    public Request create(@RequestBody Request request) {
        return requestService.create(request);
    }
}
