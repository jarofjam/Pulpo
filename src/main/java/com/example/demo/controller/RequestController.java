package com.example.demo.controller;

import com.example.demo.domain.Request;
import com.example.demo.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RequestController {

    @Autowired
    RequestService requestService;

//Client

    @RequestMapping(value = "/api/client/request", method = RequestMethod.GET)
    public List<Request> findAllByClient(@RequestParam(name="status", required=false, defaultValue="ALL") String status) {
        return requestService.findAllByClientAndStatus(status);
    }

    @RequestMapping(value = "/api/client/request", method = RequestMethod.POST)
    public Request create(@RequestBody Request request) {
        return requestService.create(request);
    }

    @RequestMapping(value = "/api/client/request/{id}", method = RequestMethod.PUT)
    public Request updateByClient(@PathVariable Long id, @RequestBody Request request) {
        return requestService.updateByClient(id, request);
    }

//Performer
    @RequestMapping(value = "/api/performer/department/request", method = RequestMethod.GET)
    public List<Request> findAllByDepartment() {
        return requestService.findAllByPerformerDepartment();
    }

    @RequestMapping(value = "/api/performer/request", method = RequestMethod.GET)
    public List<Request> findAllByPerformer() {
        return requestService.findAllByPerformer();
    }

    @RequestMapping(value = "api/performer/request/{id}", method = RequestMethod.PUT)
    public Request updatePerformerStatusComment(@PathVariable Long id, @RequestBody Request request) {
        return requestService.updatePerformerStatusComment(id, request);
    }

//Moderator
    @RequestMapping(value = "/api/moderator/request", method = RequestMethod.GET)
    public List<Request> findAll() {
        return requestService.getAll();
    }

    @RequestMapping(value = "/api/moderator/request/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable Long id) {
        requestService.delete(id);
    }
}
