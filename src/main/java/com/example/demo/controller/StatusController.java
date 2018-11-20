package com.example.demo.controller;

import com.example.demo.domain.Status;
import com.example.demo.dto.StatusDto;
import com.example.demo.service.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public StatusDto read(@PathVariable String id) {
        return statusService.read(id);
    }

    @RequestMapping(value = "/api/status", method = RequestMethod.POST)
    public StatusDto create(@RequestBody StatusDto statusDto) {
        return statusService.create(statusDto);
    }

    @RequestMapping(value = "/api/status/{id}", method = RequestMethod.PUT)
    public StatusDto update(@PathVariable String id, @RequestBody StatusDto statusDto) {
        return statusService.update(id, statusDto);
    }

    @RequestMapping(value = "/api/status/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable String id) {
        statusService.delete(id);
    }
}
