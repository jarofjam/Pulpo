package com.example.demo.controller;

import com.example.demo.dto.TypicalRequestDto;
import com.example.demo.service.TypicalRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasAuthority('APPLICANT')")
public class TypicalRequestControllerForApplicant {
    @Autowired
    private TypicalRequestService typicalRequestService;

    @RequestMapping(value = "/api/applicant/typicalrequest", method = RequestMethod.POST)
    public void create(@RequestBody TypicalRequestDto typicalRequestDto) {
        typicalRequestService.create(typicalRequestDto);
    }

    @RequestMapping(value = "/api/applicant/typicalrequest", method = RequestMethod.GET)
    public List<TypicalRequestDto> findAllByAuthorAndDepartmentAndStatus(
            @RequestParam(name = "status", required = false, defaultValue = "All") String status,
            @RequestParam(name = "department", required = false, defaultValue = "All") String department
    ) {
        return typicalRequestService.findAllByAuthorAndDepartmentAndStatus(department, status);
    }

    @RequestMapping(value = "/api/applicant/typicalrequest/{id}", method = RequestMethod.PUT)
    public void updateByApplicant(@PathVariable Long id, @RequestBody TypicalRequestDto typicalRequestDto) {
        typicalRequestService.updateByApplicant(id, typicalRequestDto);
    }
}
