package com.example.demo.controller;

import com.example.demo.dto.RequestDto;
import com.example.demo.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RequestController {

    @Autowired
    RequestService requestService;

//Applicant
    @RequestMapping(value = "/api/applicant/request", method = RequestMethod.GET)
    public List<RequestDto> findAllByAuthorAndDepartmentAndStatus(
            @RequestParam(name = "status", required = false, defaultValue = "ALL") String status,
            @RequestParam(name = "department", required = false, defaultValue = "NONE") String department
    ) {
        return requestService.findAllByAuthorAndDepartmentAndStatus(department, status);
    }

    @RequestMapping(value = "/api/applicant/request", method = RequestMethod.POST)
    public RequestDto create(@RequestBody RequestDto requestDto) {
        return requestService.create(requestDto);
    }

    @RequestMapping(value = "/api/applicant/request/{id}", method = RequestMethod.PUT)
    public RequestDto updateByApplicant(@PathVariable Long id, @RequestBody RequestDto requestDto) {
        return requestService.updateByApplicant(id, requestDto);
    }

//Performer
    @RequestMapping(value = "/api/performer/department/request", method = RequestMethod.GET)
    public List<RequestDto> findAllByPerformerDepartment() {
        return requestService.findAllByPerformerDepartment();
    }

    @RequestMapping(value = "/api/performer/request", method = RequestMethod.GET)
    public List<RequestDto> findAllByPerformerAndStatus(
            @RequestParam(name = "status", required = false, defaultValue = "ALL") String status
    ) {
        return requestService.findAllByPerformerAndStatus(status);
    }

    @RequestMapping(value = "api/performer/request/{id}", method = RequestMethod.PUT)
    public RequestDto updateByPerformer(@PathVariable Long id, @RequestBody RequestDto requestDto) {
        return requestService.updateByPerformer(id, requestDto);
    }

//Moderator
    @RequestMapping(value = "/api/moderator/request", method = RequestMethod.GET)
    public List<RequestDto> findAllByDepartmentAndStatus(
            @RequestParam(name = "status", required = false, defaultValue = "ALL") String status,
            @RequestParam(name = "department", required = false, defaultValue = "NONE") String department
    ) {
        return requestService.findAllByDepartmentAndStatus(department, status);
    }

    @RequestMapping(value = "api/moderator/request/{id}", method = RequestMethod.PUT)
    public RequestDto updateByModerator(@PathVariable Long id, @RequestBody RequestDto requestDto) {
        return requestService.updateByModerator(id, requestDto);
    }

    @RequestMapping(value = "/api/moderator/request/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable Long id) {
        requestService.delete(id);
    }
}
