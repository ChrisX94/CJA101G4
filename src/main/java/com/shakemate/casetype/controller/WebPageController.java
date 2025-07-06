package com.shakemate.casetype.controller;

import com.shakemate.casetype.model.CaseType;
import com.shakemate.casetype.model.CaseTypeRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class WebPageController {

    private final CaseTypeRepository caseTypeRepo;

    public WebPageController(CaseTypeRepository caseTypeRepo) {
        this.caseTypeRepo = caseTypeRepo;
    }

    @GetMapping("/shakemate")
    public String index(Model model) {
        model.addAttribute("categories", caseTypeRepo.findAll());
        return "front-end/servicecase/index";
    }

    @GetMapping("/ask")
    public String askPage(Model model) {
        List<CaseType> categories = caseTypeRepo.findAll();
        model.addAttribute("categories", categories);
        return "ask";
    }
}
