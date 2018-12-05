package com.example.demo.controller;

import com.example.demo.dto.RequestDto;
import com.example.demo.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasAuthority('APPLICANT')")
public class RequestControllerForApplicant {
    @Autowired
    RequestService requestService;

    @RequestMapping(value = "/api/applicant/request", method = RequestMethod.GET)
    public List<RequestDto> findAllByAuthorAndDepartmentAndStatus(
            @RequestParam(name = "status", required = false, defaultValue = "ALL") String status,
            @RequestParam(name = "department", required = false, defaultValue = "ALL") String department
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
}
