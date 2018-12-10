package com.example.demo.controller;

import com.example.demo.dto.StatusDto;
import com.example.demo.service.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StatusController {

    @Autowired
    private StatusService statusService;

    @RequestMapping(value = "/api/status", method = RequestMethod.GET)
    public List<StatusDto> findAll() {
        return statusService.findAll();
    }

    @RequestMapping(value = "/api/status/{id}", method = RequestMethod.GET)
    public StatusDto read(@PathVariable Long id) {
        return statusService.read(id);
    }
}
