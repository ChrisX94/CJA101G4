package com.shakemate.servicecase.controller;
//負責後台 HTML 頁面的 CRUD（Thymeleaf／JSP 等）。
import com.shakemate.casetype.model.CaseType;
import com.shakemate.casetype.model.CaseTypeRepository;
import com.shakemate.adm.model.AdmRepository;
import com.shakemate.adm.model.AdmVO; // 根據你的專案結構調整
import com.shakemate.servicecase.dto.ServiceCaseDTO;
import com.shakemate.servicecase.mapper.ServiceCaseMapper;
import com.shakemate.servicecase.model.ServiceCase;
import com.shakemate.servicecase.service.ServiceCaseService;
import com.shakemate.user.dao.UsersRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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
	
	// 1. 注入使用者、管理員 Repository
    @Autowired
    private UsersRepository userRepository;     // 你的 User JPA repo

    @Autowired
    private AdmRepository adminRepository;   // 你的 AdmVO JPA repo

	// 顯示全部案件
	@GetMapping({"", "/list"})
	public String list(Model model) {
		model.addAttribute("cases", service.getAll());
		return "back-end/servicecase/list";
	}

	// 後端新增表單
	@GetMapping("/add")
	public String showAddForm(Model model, HttpSession session) {
		ServiceCaseDTO dto = new ServiceCaseDTO();

	    // 從 session 取得管理員 ID
		AdmVO  admin = (AdmVO) session.getAttribute("loggedInAdmin");
	    if (admin != null) {
	        dto.setAdmId(admin.getAdmId()); // 自動帶入
	    }
		
        model.addAttribute("serviceCaseDTO", dto);
		return "back-end/servicecase/add";
	}

    // --- 處理「新增」提交，啟用驗證 ---
    @PostMapping("/save")
    public String saveNew(
            @Valid @ModelAttribute("serviceCaseDTO") ServiceCaseDTO dto,
            BindingResult errors,
            Model model,
            RedirectAttributes ra) {
    	
    	// 2. 手動檢查 userId
        if (dto.getUserId() != null && !userRepository.existsById(dto.getUserId())) {
            errors.rejectValue(
                "userId",            // 欄位名
                "NotFound.userId",   // code (對應 messages.properties)
                "使用者 ID 不存在"   // defaultMessage
            );
        }

        // 2. 手動檢查 admId
        if (dto.getAdmId() != null && !adminRepository.existsById(dto.getAdmId())) {
            errors.rejectValue(
                "admId",
                "NotFound.admId",
                "管理員 ID 不存在"
            );
        }

        if (errors.hasErrors()) {
            // 驗證失敗 → 留在 add 頁面，Thymeleaf 會顯示錯誤
            return "back-end/servicecase/add";
        }
        ServiceCase entity = ServiceCaseMapper.toEntity(dto);
        service.create(entity);
        ra.addFlashAttribute("successMsg", "新增成功!");
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
    
    @PostMapping("/sadd")
    public String saveUserCase(
            @Valid @ModelAttribute("serviceCaseDTO") ServiceCaseDTO dto,
            BindingResult errors,
            RedirectAttributes redirectAttrs,
            Model model) {

        // 如果有驗證錯誤，回到前端表單
        if (errors.hasErrors()) {
            // allTypes 也要再塞一次
            model.addAttribute("allTypes", caseTypeRepo.findAll());
            return "front-end/servicecase/sadd";
        }

        ServiceCase entity = ServiceCaseMapper.toEntity(dto);
        service.create(entity);

        redirectAttrs.addFlashAttribute("successMsg", "案件已成功提交，我們將盡快處理！");
        return "redirect:/servicecase/single?caseId=" + entity.getCaseId();
    }
    
    @GetMapping("/single")
    public String findSingleCase(@RequestParam("caseId") Integer caseId, Model model) {
        ServiceCase serviceCase = service.findById(caseId);
        model.addAttribute("serviceCase", serviceCase);
        return "front-end/servicecase/slistone"; // 對應你的 slistone.html 路徑
    }

    // --- 顯示「編輯」表單，先 load DTO ---
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        ServiceCase sc = service.findById(id);
        ServiceCaseDTO dto = ServiceCaseMapper.toDTO(sc);
        model.addAttribute("serviceCaseDTO", dto);
        return "back-end/servicecase/edit";
    }

    // --- 處理「編輯」提交，啟用驗證 ---
    @PostMapping("/saveEdit")
    public String saveEdit(
            @Valid @ModelAttribute("serviceCaseDTO") ServiceCaseDTO dto,
            BindingResult errors,
            Model model,
            RedirectAttributes ra) {
    	
        // 使用者 ID 存在性檢查
        if (dto.getUserId() != null && !userRepository.existsById(dto.getUserId())) {
            errors.rejectValue(
                "userId",
                "NotFound.userId",
                "使用者 ID 不存在"
            );
        }

        // 管理員 ID 存在性檢查
        if (dto.getAdmId() != null && !adminRepository.existsById(dto.getAdmId())) {
            errors.rejectValue(
                "admId",
                "NotFound.admId",
                "管理員 ID 不存在"
            );
        }

        if (errors.hasErrors()) {
            // 千萬別忘記把下拉選單資料再塞一次
            model.addAttribute("allTypes", caseTypeRepo.findAll());
            return "back-end/servicecase/edit";
        }
        ServiceCase entity = ServiceCaseMapper.toEntity(dto);
        service.create(entity);
        ra.addFlashAttribute("successMsg", "更新成功!");
        return "redirect:/servicecase";
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
