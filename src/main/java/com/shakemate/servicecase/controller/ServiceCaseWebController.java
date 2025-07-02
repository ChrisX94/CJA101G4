package com.shakemate.servicecase.controller;
//負責後台 HTML 頁面的 CRUD（Thymeleaf／JSP 等）。
import com.shakemate.casetype.model.CaseType;
import com.shakemate.casetype.model.CaseTypeRepository;
import com.shakemate.servicecase.dto.ServiceCaseDTO;
import com.shakemate.servicecase.mapper.ServiceCaseMapper;
import com.shakemate.servicecase.model.ServiceCase;
import com.shakemate.servicecase.model.ServiceCaseService;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


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

	// 後端新增表單
	@GetMapping("/add")
	public String showAddForm(Model model) {
        model.addAttribute("serviceCase", new ServiceCase());
		return "back-end/servicecase/add";
	}

	// 儲存新案件（或更新）
	@PostMapping("/save")
	public String save(@ModelAttribute ServiceCaseDTO serviceCaseDTO) {
		ServiceCase sc = ServiceCaseMapper.toEntity(serviceCaseDTO);
		service.create(sc); // JPA save() 可用於新增或更新
		return "redirect:/servicecase";
	}
	
	// 前端新增表單
    @GetMapping("/sadd")
    public String showSaddForm(Model model) {
        ServiceCaseDTO dto = new ServiceCaseDTO();
        dto.setCaseTypeId(1);
        dto.setCaseStatus(0);
        dto.setAdmId(1);
        dto.setUserId(1);
        model.addAttribute("serviceCaseDTO", dto);
        return "front-end/servicecase/sadd";
    }
    
    @GetMapping("/single")
    public String findSingleCase(@RequestParam("caseId") Integer caseId, Model model) {
        ServiceCase serviceCase = service.findById(caseId);
        model.addAttribute("serviceCase", serviceCase);
        return "front-end/servicecase/slistone"; // 對應你的 slistone.html 路徑
    }
    
    @PostMapping("/sadd")
    public String saveUserCase(@ModelAttribute("serviceCaseDTO") ServiceCaseDTO dto, RedirectAttributes redirectAttrs) {
        ServiceCase entity = ServiceCaseMapper.toEntity(dto);
        service.create(entity);
        // 加入 flash 訊息
        redirectAttrs.addFlashAttribute("successMsg", "案件已成功提交，我們將盡快處理！");        
        // ➜ 導向單一查詢頁，帶入剛建立的 caseId
        return "redirect:/servicecase/single?caseId=" + entity.getCaseId();
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
