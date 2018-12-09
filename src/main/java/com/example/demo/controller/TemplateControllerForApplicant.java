package com.example.demo.controller;

import com.example.demo.dto.TemplateDto;
import com.example.demo.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@PreAuthorize("hasAuthority('APPLICANT')")
public class TemplateControllerForApplicant {
    @Autowired
    private TemplateService templateService;

    @RequestMapping(value = "/api/applicant/template", method = RequestMethod.GET)
    public List<TemplateDto> findAllByDepartment(
            @RequestParam(name = "department", required = false, defaultValue = "All") String department
    ) {
        return templateService.findAllByDepartment(department);
    }
}
