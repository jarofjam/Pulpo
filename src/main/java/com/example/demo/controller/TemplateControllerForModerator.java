package com.example.demo.controller;

import com.example.demo.dto.TemplateDto;
import com.example.demo.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasAuthority('MODERATOR')")
public class TemplateControllerForModerator {
    @Autowired
    private TemplateService templateService;

    @RequestMapping(value = "/api/moderator/template", method = RequestMethod.POST)
    public void create(@RequestBody TemplateDto templateDto) {
        templateService.create(templateDto);
    }

    @RequestMapping(value = "/api/template", method = RequestMethod.GET)
    public List<TemplateDto> findAllByDepartment(
            @RequestParam(name = "department", required = false, defaultValue = "All") String department
    ) {
        return templateService.findAllByDepartment(department);
    }

    @RequestMapping(value = "/api/moderator/template/{id}", method = RequestMethod.PUT)
    public void update(@PathVariable Long id, @RequestBody TemplateDto templateDto) {
        templateService.update(id, templateDto);
    }

    @RequestMapping(value = "/api/moderator/template/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable Long id) {
        templateService.delete(id);
    }
}
