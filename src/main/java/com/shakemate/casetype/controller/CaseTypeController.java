package com.shakemate.casetype.controller;

import com.shakemate.casetype.model.CaseType;
import com.shakemate.casetype.model.CaseTypeRepository;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/casetype")
public class CaseTypeController {

    @Autowired
    private CaseTypeRepository caseTypeRepository;

    @GetMapping
    public List<CaseType> getAll() {
    	System.out.println("✅ /api/casetype 被呼叫！");
        return caseTypeRepository.findAll();
    }
    
    @PostConstruct
    public void init() {
        System.out.println("📌 CaseTypeController 已被 Spring 掃描並初始化");
    }
    
}
