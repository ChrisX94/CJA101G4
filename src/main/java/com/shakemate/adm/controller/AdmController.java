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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shakemate.adm.model.AdmRepository;
import com.shakemate.adm.model.AdmService;
import com.shakemate.adm.model.AdmVO;
import com.shakemate.adm.model.AuthFuncService;
import com.shakemate.adm.model.AuthFuncVO;
import com.shakemate.adm.util.PasswordUtil;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/adm")
public class AdmController {

	@Autowired
	private AdmService admSvc;

	@Autowired
	private AuthFuncService authFuncSvc;

	@Autowired
	private AdmRepository admRepository;

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
			@RequestParam(value = "authFuncIds", required = false) List<Integer> authFuncIds,
			RedirectAttributes redirectAttributes) {

		result = removeFieldError(admVO, result, "authFuncIds");

		// 密碼必填驗證（新增）
		if (admVO.getInputPwd() == null || admVO.getInputPwd().isBlank()) {
			result.rejectValue("inputPwd", "error.inputPwd", "密碼不能空白");
		} else if (admVO.getInputPwd().length() < 6) {
			result.rejectValue("inputPwd", "error.inputPwd", "密碼至少要 6 碼");
		}

		// 若已有重複帳號
		if (admRepository.existsByAdmAcc(admVO.getAdmAcc())) {
			redirectAttributes.addFlashAttribute("error", "該帳號已存在！");
			return "redirect:/adm/addAdm";
		}

		// 加密明碼密碼
		if (!result.hasErrors()) {
			if (admVO.getInputPwd() != null && !admVO.getInputPwd().isBlank()) {
				String hashedPwd = PasswordUtil.hashPassword(admVO.getInputPwd());
				admVO.setAdmPwd(hashedPwd);
			}
		}

		// 驗證錯誤印出訊息 並返回原頁
		if (result.hasErrors()) {
			System.out.println("表單驗證失敗: ");
			result.getAllErrors().forEach(e -> System.out.println("Error" + e));
			return "back-end/adm/addAdm";
		}
		// 新增資料
		admSvc.addAdmWithAuth(admVO, authFuncIds); // 這裡會呼叫 Service 去新增並綁定權限
		redirectAttributes.addFlashAttribute("success", "新增成功！");
		return "redirect:/adm/listAllAdm";
	}

	// 顯示單筆管理員資訊並準備修改
	@PostMapping("getOne_For_Update")
	public String getOneForUpdate(@RequestParam("admId") Integer admId, ModelMap model) {
		AdmVO admVO = admSvc.getOneAdm(admId);
		// 確保 inputPwd 欄位是空的，避免顯示原密碼
		admVO.setInputPwd("");
		model.addAttribute("admVO", admVO);
		return "back-end/adm/update_adm_input";
	}

	// 處理修改表單送出
	@PostMapping("update")
	public String update(@Valid AdmVO admVO, BindingResult result,
			@RequestParam(value = "authFuncIds", required = false) List<Integer> authFuncIds,
			ModelMap model, RedirectAttributes redirectAttributes) {

		// 移除 inputPwd 的驗證錯誤，改由下方自訂驗證
		result = removeFieldError(admVO, result, "inputPwd");
		result = removeFieldError(admVO, result, "authFuncIds");

		// 密碼驗證（修改）
		if (admVO.getInputPwd() != null && !admVO.getInputPwd().isBlank() && admVO.getInputPwd().length() < 6) {
			result.rejectValue("inputPwd", "error.inputPwd", "密碼至少要 6 碼");
		}

		// 暫時跳過權限檢查，專注於基本資料更新
		// if (authFuncIds == null || authFuncIds.isEmpty()) {
		// model.addAttribute("error", "請至少選擇一個權限！");
		// return "back-end/adm/update_adm_input";
		// }

		// 檢查帳號是否重複（排除自己）
		AdmVO existingAdm = admSvc.getOneAdm(admVO.getAdmId());
		if (existingAdm == null) {
			model.addAttribute("error", "要修改的管理員不存在！");
			return "back-end/adm/update_adm_input";
		}

		// 檢查帳號是否被其他管理員使用
		AdmVO duplicateAdm = admRepository.findByAdmAcc(admVO.getAdmAcc());
		if (duplicateAdm != null && !duplicateAdm.getAdmId().equals(admVO.getAdmId())) {
			model.addAttribute("error", "該帳號已被其他管理員使用！");
			return "back-end/adm/update_adm_input";
		}

		// 處理密碼更新
		if (admVO.getInputPwd() != null && !admVO.getInputPwd().isBlank()) {
			// 如果有輸入新密碼，則加密並更新
			String hashedPwd = PasswordUtil.hashPassword(admVO.getInputPwd());
			admVO.setAdmPwd(hashedPwd);
		} else {
			// 如果沒有輸入新密碼，則保持原密碼不變
			admVO.setAdmPwd(existingAdm.getAdmPwd());
		}

		if (result.hasErrors()) {
			return "back-end/adm/update_adm_input";
		}

		try {
			System.out.println("=== 控制器開始處理更新請求 ===");
			System.out.println("接收到的管理員ID: " + admVO.getAdmId());
			System.out.println("接收到的姓名: " + admVO.getAdmName());
			System.out.println("接收到的帳號: " + admVO.getAdmAcc());
			System.out.println("接收到的密碼: " + (admVO.getInputPwd() != null ? "有輸入新密碼" : "沒有輸入新密碼"));

			// 先測試資料庫連接
			admSvc.testDatabaseConnection();

			// 使用Service的基本資料+權限更新方法
			admSvc.updateAdmBasicWithAuth(admVO, authFuncIds);

			// 清除快取
			admSvc.clearCache();

			// 再次測試資料庫連接，查看更新結果
			admSvc.testDatabaseConnection();

			System.out.println("=== 控制器更新處理完成 ===");
			redirectAttributes.addFlashAttribute("success", "管理員基本資料及權限修改成功！");
		} catch (Exception e) {
			System.err.println("修改管理員時發生錯誤: " + e.getMessage());
			System.err.println("錯誤類型: " + e.getClass().getSimpleName());
			e.printStackTrace();
			model.addAttribute("error", "修改失敗：" + e.getMessage());
			return "back-end/adm/update_adm_input";
		}

		return "redirect:/adm/listAllAdm";
	}

	// 刪除管理員
	@PostMapping("delete")
	public String delete(@RequestParam("admId") Integer admId,
			HttpSession session, ModelMap model, RedirectAttributes redirectAttributes) {

		// 檢查當前登入的管理員
		AdmVO currentAdm = (AdmVO) session.getAttribute("loggedInAdm");
		if (currentAdm == null) {
			return "redirect:/adm/admLogin";
		}

		// 防止刪除自己
		if (currentAdm.getAdmId().equals(admId)) {
			redirectAttributes.addFlashAttribute("error", "不能刪除自己的帳號！");
			return "redirect:/adm/listAllAdm";
		}

		// 檢查要刪除的管理員是否存在
		AdmVO admToDelete = admSvc.getOneAdm(admId);
		if (admToDelete == null) {
			redirectAttributes.addFlashAttribute("error", "要刪除的管理員不存在！");
			return "redirect:/adm/listAllAdm";
		}

		try {
			admSvc.deleteAdm(admId);
			redirectAttributes.addFlashAttribute("success", "管理員刪除成功！");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "刪除失敗：" + e.getMessage());
		}

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
	public String listAllAdm(HttpSession session, ModelMap model, HttpServletResponse response) {
		// 設置響應標頭防止快取
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Expires", "0");

		AdmVO adm = (AdmVO) session.getAttribute("loggedInAdm");

		// 沒有登入 → 踢回 login 頁面
		if (adm == null) {
			return "redirect:/adm/admLogin";
		}

		// 非最高管理員 → 踢回一般首頁
		boolean isSuperAdmin = adm.getAuthFuncs().stream().anyMatch(a -> a.getAuthId() == 1);
		if (!isSuperAdmin) {
			return "redirect:/adm/adminHome";
		}

		List<AdmVO> list = admSvc.getAll();
		model.addAttribute("admList", list);
		return "back-end/adm/listAllAdm";
	}

	@GetMapping("/adminHome")
	public String showAdminHome(HttpSession session, Model model, HttpServletResponse response) {
		// 設置響應標頭防止快取
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Expires", "0");

		AdmVO adm = (AdmVO) session.getAttribute("loggedInAdm");
		Boolean isSuperAdmin = (Boolean) session.getAttribute("isSuperAdmin");

		if (adm == null) {
			return "redirect:/adm/admLogin"; // 若沒登入直接踢回去
		}

		model.addAttribute("loggedInAdm", adm);
		model.addAttribute("isSuperAdmin", isSuperAdmin);

		return "back-end/adm/adminHome";
	}

	@PostMapping("/listAdms_ByCompositeQuery")
	public String listByCompositeQuery(@RequestParam(required = false) String admName,
			@RequestParam(required = false) String admAcc, @RequestParam(required = false) Integer authId,
			Model model) {
		if ((admName == null || admName.isBlank()) && (admAcc == null || admAcc.isBlank()) && (authId == null)) {
			model.addAttribute("error", "請至少輸入一個查詢條件");
			return "back-end/adm/select_page"; // 或原查詢頁面
		}

		System.out.println("admName " + admName + "   admAcc " + admAcc + "  authId " + authId);
		List<AdmVO> list = admSvc.findByConditions(admName, admAcc, authId);
		System.out.println(list);
		model.addAttribute("admList", list);
		return "back-end/adm/listAllAdm";
	}

}
