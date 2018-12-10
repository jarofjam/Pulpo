package com.example.demo.controller;

import com.example.demo.dto.StatusDto;
import com.example.demo.service.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasAuthority('ADMIN')")
public class StatusControllerForAdmin {
    @Autowired
    StatusService statusService;

    @RequestMapping(value = "/api/status", method = RequestMethod.POST)
    public StatusDto create(@RequestBody StatusDto statusDto) {
        return statusService.create(statusDto);
    }

    @RequestMapping(value = "/api/status/{id}", method = RequestMethod.PUT)
    public StatusDto update(@PathVariable Long id, @RequestBody StatusDto statusDto) {
        return statusService.update(id, statusDto);
    }

    @RequestMapping(value = "/api/status/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable Long id) {
        statusService.delete(id);
    }
}
