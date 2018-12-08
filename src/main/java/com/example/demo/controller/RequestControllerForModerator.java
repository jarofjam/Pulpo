package com.example.demo.controller;

import com.example.demo.dto.RequestDto;
import com.example.demo.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasAuthority('MODERATOR')")
public class RequestControllerForModerator {
    @Autowired
    RequestService requestService;

    @RequestMapping(value = "/api/moderator/request", method = RequestMethod.GET)
    public List<RequestDto> findAllByDepartmentAndStatus(
            @RequestParam(name = "status", required = false, defaultValue = "All") String status,
            @RequestParam(name = "department", required = false, defaultValue = "All") String department
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
