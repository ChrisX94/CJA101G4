package com.shakemate.adm.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.shakemate.adm.model.AdmService;
import com.shakemate.adm.model.AdmVO;
import com.shakemate.adm.model.AuthFuncService;
import com.shakemate.adm.model.AuthFuncVO;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/adm")
public class AdmController {

	@Autowired
	private AdmService admSvc;

	@Autowired
	private AuthFuncService authFuncSvc;

	// 顯示新增管理員表單
	@GetMapping("addAdm")
	public String addAdm(ModelMap model) {
		AdmVO admVO = new AdmVO();
		model.addAttribute("admVO", admVO);
		return "back-end/adm/addAdm";
	}

	// 處理新增表單送出
	@PostMapping("insert")
	public String insert(@Valid AdmVO admVO, BindingResult result,
			@RequestParam(value = "authFuncIds", required = false) List<Integer> authFuncIds, ModelMap model) {

		result = removeFieldError(admVO, result, "authFuncIds");

		if (result.hasErrors()) {
			return "back-end/adm/addAdm";
		}

		admSvc.addAdmWithAuth(admVO, authFuncIds); // 這裡會呼叫 Service 去新增並綁定權限
		model.addAttribute("success", "- (新增成功)");
		return "redirect:/adm/listAllAdm";
	}

	// 顯示單筆管理員資訊並準備修改
	@PostMapping("getOne_For_Update")
	public String getOneForUpdate(@RequestParam("admId") Integer admId, ModelMap model) {
		AdmVO admVO = admSvc.getOneAdm(admId);
		model.addAttribute("admVO", admVO);
		return "back-end/adm/update_adm_input";
	}

	// 處理修改表單送出
	@PostMapping("update")
	public String update(@Valid AdmVO admVO, BindingResult result,
			@RequestParam(value = "authFuncIds", required = false) List<Integer> authFuncIds, ModelMap model) {

		result = removeFieldError(admVO, result, "authFuncIds");

		if (result.hasErrors()) {
			return "back-end/adm/update_adm_input";
		}

		admSvc.updateAdmWithAuth(admVO, authFuncIds);
		model.addAttribute("success", "- (修改成功)");
		return "redirect:/adm/listAllAdm";
	}

	// 刪除管理員
	@PostMapping("delete")
	public String delete(@RequestParam("admId") Integer admId, ModelMap model) {
		admSvc.deleteAdm(admId);
		model.addAttribute("success", "- (刪除成功)");
		return "redirect:/adm/listAllAdm";
	}

	// 權限下拉選單資料（checkbox 多選用）
	@ModelAttribute("authFuncListData")
	protected List<AuthFuncVO> authFuncListData() {
		return authFuncSvc.getAll();
	}

	// 去除 BindingResult 中某欄位錯誤
	public BindingResult removeFieldError(AdmVO admVO, BindingResult result, String removedFieldname) {
		List<FieldError> filtered = result.getFieldErrors().stream()
				.filter(field -> !field.getField().equals(removedFieldname)).collect(Collectors.toList());

		result = new BeanPropertyBindingResult(admVO, "admVO");
		for (FieldError fieldError : filtered) {
			result.addError(fieldError);
		}
		return result;
	}

	@GetMapping("/select_page")
	public String goSelectPage() {
		return "back-end/adm/select_page";
	}

	@GetMapping("/test")
	public String goTestPage() {
		return "test"; // templates/test.html
	}

	@GetMapping("/searchByName")
	public String searchByName(@RequestParam("admName") String name, Model model) {
		List<AdmVO> list = admSvc.findByName(name);
		model.addAttribute("admListData", list);
		return "back-end/adm/listAllAdm";
	}

	// 查詢所有管理員

	@GetMapping("listAllAdm")
	public String listAllAdm(HttpSession session, ModelMap model) {
		AdmVO adm = (AdmVO) session.getAttribute("loggedInAdmin");
//		if (adm == null || adm.getAuthFuncs().stream().noneMatch(a -> a.getAuthName().equals("最高管理員"))) {
//			return "redirect:/login";
//		}
		List<AdmVO> list = admSvc.getAll();
		model.addAttribute("admListData", list);
		return "back-end/adm/listAllAdm";
	}
	

	@PostMapping("/listAdms_ByCompositeQuery")
	public String listByCompositeQuery(@RequestParam(required = false) String admName,
	                                   @RequestParam(required = false) String admAccount,
	                                   @RequestParam(required = false) String authName,
	                                   Model model) {

	    // 你可以依條件去 service 查資料
	    List<AdmVO> list = admSvc.findByConditions(admName, admAccount, authName);

	    model.addAttribute("admList", list);
	    return "back-end/adm/listAdm"; // 你要顯示查詢結果的頁面
	}


}
