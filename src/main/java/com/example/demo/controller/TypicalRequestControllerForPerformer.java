package com.example.demo.controller;

import com.example.demo.dto.TypicalRequestDto;
import com.example.demo.service.TypicalRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasAuthority('PERFORMER')")
public class TypicalRequestControllerForPerformer {
    @Autowired
    private TypicalRequestService typicalRequestService;

    @RequestMapping(value = "/api/performer/department/typicalrequest", method = RequestMethod.GET)
    public List<TypicalRequestDto> findAllByPerformerDepartment() {
        return typicalRequestService.findAllByPerformerDepartment();
    }

    @RequestMapping(value = "/api/performer/typicalrequest", method = RequestMethod.GET)
    public List<TypicalRequestDto> findAllByPerformerAndStatus(
            @RequestParam(name = "status", required = false, defaultValue = "All") String status
    ) {
        return typicalRequestService.findAllByPerformerAndStatus(status);
    }

    @RequestMapping(value = "/api/performer/typicalrequest/{id}", method = RequestMethod.PUT)
    public void updateByPerformer(@PathVariable Long id, @RequestBody TypicalRequestDto typicalRequestDto) {
        typicalRequestService.updateByPerformer(id, typicalRequestDto);
    }
}
