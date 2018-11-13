package com.example.demo.controller;

import com.example.demo.entity.Status;
import com.example.demo.service.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class StatusController {

    @Autowired
    private StatusService statusService;

    @RequestMapping(value = "/api/status", method = RequestMethod.GET)
    public List<Status> findAll() {
        return statusService.findAll();
    }

    @RequestMapping(value = "/api/status/{id}", method = RequestMethod.GET)
    public Status read(@PathVariable String id) {
        return statusService.read(id);
    }

    @RequestMapping(value = "/api/status", method = RequestMethod.POST)
    public Status create(@RequestBody Status status) {
        return statusService.create(status);
    }

    @RequestMapping(value = "/api/status/{id}", method = RequestMethod.PUT)
    public Status update(@PathVariable String id, @RequestBody Status status) {
        return statusService.update(id, status);
    }

    @RequestMapping(value = "/api/status/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable String id) {
        statusService.delete(id);
    }
}
