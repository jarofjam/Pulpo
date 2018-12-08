package com.example.demo.controller;

import com.example.demo.dto.TypicalRequestDto;
import com.example.demo.service.TypicalRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasAuthority('MODERATOR')")
public class TypicalRequestControllerForModerator {
    @Autowired
    private TypicalRequestService typicalRequestService;

    @RequestMapping(value = "/api/moderator/typicalrequest", method = RequestMethod.GET)
    public List<TypicalRequestDto> findAllByDepartmentAndStatus(
            @RequestParam(name = "status", required = false, defaultValue = "All") String status,
            @RequestParam(name = "department", required = false, defaultValue = "All") String department
    ) {
        return typicalRequestService.findAllByDepartmentAndStatus(department, status);
    }

    @RequestMapping(value = "/api/moderator/typicalrequest/{id}", method = RequestMethod.PUT)
    public void updateByModerator(@PathVariable Long id, @RequestBody TypicalRequestDto typicalRequestDto) {
        typicalRequestService.updateByModerator(id, typicalRequestDto);
    }

    @RequestMapping(value = "/api/moderator/typicalrequest/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable Long id) {
        typicalRequestService.delete(id);
    }
}
