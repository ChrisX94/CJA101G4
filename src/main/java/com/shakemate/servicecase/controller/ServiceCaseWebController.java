package com.shakemate.servicecase.controller;

import com.shakemate.casetype.model.*;
import com.shakemate.servicecase.model.ServiceCase;
import com.shakemate.servicecase.model.ServiceCaseService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/servicecase")
public class ServiceCaseWebController {

	@Autowired
	private ServiceCaseService service;

	@Autowired
	private CaseTypeRepository caseTypeRepo;

	@ModelAttribute("allTypes")
	public List<CaseType> populateCaseTypes() {
		return caseTypeRepo.findAll();
	}

	// 顯示全部案件
	@GetMapping
	public String list(Model model) {
		model.addAttribute("cases", service.getAll());
		return "back-end/servicecase/list";
	}

	// 顯示新增表單
	@GetMapping("/add")
//    public String showAddForm(Model model) {
	public String showAddForm(Model model) {
        model.addAttribute("serviceCase", new ServiceCase());
		return "back-end/servicecase/add";
	}

	// 儲存新案件（或更新）
	@PostMapping("/save")
	public String save(@ModelAttribute ServiceCase serviceCase) {
		service.create(serviceCase); // JPA save() 可用於新增或更新
		return "redirect:/servicecase";
	}

	// 顯示修改頁面
	@GetMapping("/edit/{id}")
	public String showEditForm(@PathVariable Integer id, Model model) {
		ServiceCase serviceCase = service.findById(id);
		model.addAttribute("serviceCase", serviceCase);
		return "back-end/servicecase/edit";
	}

	// 刪除案件
	@GetMapping("/delete/{id}")
	public String delete(@PathVariable Integer id) {
		service.delete(id);
		return "redirect:/servicecase";
	}
}
