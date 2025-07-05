package com.shakemate.casetype.controller;

import com.shakemate.casetype.dto.AiRequestDTO;
import com.shakemate.casetype.dto.CaseTypeDTO;
import com.shakemate.casetype.mapper.CaseTypeMapper;
import com.shakemate.casetype.model.CaseType;
import com.shakemate.casetype.service.CaseTypeService;
import com.shakemate.casetype.service.OpenAiService;
import com.shakemate.casetype.service.PromptLoader;
import com.shakemate.servicecase.service.ServiceCaseService;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiRestController {

    private final OpenAiService openAi;
    private final PromptLoader promptLoader;
    private final CaseTypeService caseTypeService;
    private final ServiceCaseService statusService;

    public AiRestController(
        OpenAiService openAi,
        PromptLoader promptLoader,
        CaseTypeService caseTypeService,
        ServiceCaseService statusService
    ) {
        this.openAi = openAi;
        this.promptLoader = promptLoader;
        this.caseTypeService = caseTypeService;
        this.statusService = statusService;
    }

    /**
     * 取得所有分類（純 DTO，前端下拉用）
     */
    @GetMapping("/categories")
    public List<CaseTypeDTO> listCategories() {
        return caseTypeService.listAll();   // ✅ 直接回傳 listAll() 已經是 DTO 清單
    }

    /**
     * AI 問答：根據 selectedCategory + prompt.txt
     */
    @PostMapping(  value = "/chat", produces = MediaType.TEXT_PLAIN_VALUE)
    public String chatWithCategory(@RequestBody AiRequestDTO req) {
        return openAi.chatWithCategory(req.getSelectedCategory());
    }

    /**
     * 進度查詢：若前端傳 { "caseId": 123 }
     */
    @GetMapping("/status/{caseId}")
    public Map<String, Object> checkStatus(@PathVariable int caseId) {
        return statusService.getStatusResponse(caseId);
    }
}
