package com.shakemate.servicecase.controller;
//負責後台 HTML 頁面的 CRUD（Thymeleaf／JSP 等）。
import com.shakemate.casetype.model.CaseType;
import com.shakemate.casetype.model.CaseTypeRepository;
import com.shakemate.servicecase.model.ServiceCase;
import com.shakemate.servicecase.model.ServiceCaseService;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
	@GetMapping({"", "/list"})
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
	
	

	/**
     * 查詢案件：支援 userId, caseTypeId, caseStatus, fromDate, toDate
     * 前端 form 發 GET /servicecase/search?userId=...&caseTypeId=...&caseStatus=...&fromDate=yyyy-MM-dd&toDate=yyyy-MM-dd
     */
    @GetMapping("/search")
    public String search(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer caseTypeId,
            @RequestParam(required = false) Integer caseStatus,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            Model model
    ) {
        // 呼叫 Service 層的查詢方法，回傳符合條件的 List<ServiceCase>
        List<ServiceCase> results = service.search(userId, caseTypeId, caseStatus, fromDate, toDate);

        model.addAttribute("cases", results);
        // allTypes 已由 @ModelAttribute 提供
        return "back-end/servicecase/listone";
    }
}
