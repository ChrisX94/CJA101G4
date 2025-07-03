package com.shakemate.servicecase.controller;

import com.shakemate.servicecase.service.ServiceCaseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class CaseStatusController {
    private final ServiceCaseService caseService;

    public CaseStatusController(ServiceCaseService caseService) {
        this.caseService = caseService;
    }

    @GetMapping("/api/cases/{id}/status")
    public Map<String, Object> status(@PathVariable int id) {
        return caseService.getStatusResponse(id);
    }
}