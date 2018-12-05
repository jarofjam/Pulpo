package com.example.demo.controller;

import com.example.demo.dto.RequestDto;
import com.example.demo.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasAuthority('PERFORMER')")
public class RequestControllerForPerformer {
    @Autowired
    RequestService requestService;

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
}
