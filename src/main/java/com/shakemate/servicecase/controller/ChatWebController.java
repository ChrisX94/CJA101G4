package com.shakemate.servicecase.controller;

import com.shakemate.casetype.model.CaseType;
import com.shakemate.casetype.model.CaseTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ChatWebController {

	@Autowired
	private CaseTypeRepository caseTypeRepository;

	// 原本的 index mapping
	@GetMapping("/shakemate")
	public String index(Model model) {
		model.addAttribute("categories", caseTypeRepository.findAll());
		return "front-end/servicecase/index";
	}

	// 新增：提問頁面
	@GetMapping("/ask")
	public String askPage(Model model) {
		List<CaseType> categories = caseTypeRepository.findAll();
		model.addAttribute("categories", categories);
		return "ask";
	}
}
