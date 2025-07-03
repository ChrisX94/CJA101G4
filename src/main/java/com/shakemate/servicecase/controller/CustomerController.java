package com.shakemate.servicecase.controller;

import com.shakemate.casetype.service.CaseTypeService;
import com.shakemate.servicecase.service.OpenAiService;
import com.shakemate.servicecase.service.PromptLoader;
import com.shakemate.servicecase.service.ServiceCaseService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class CustomerController {

    private final PromptLoader promptLoader;
    private final CaseTypeService caseTypeService;
    private final OpenAiService aiChatService;
    private final ServiceCaseService serviceCaseService;

    public CustomerController(
            PromptLoader promptLoader,
            CaseTypeService caseTypeService,
            OpenAiService aiChatService,
            ServiceCaseService serviceCaseService
    ) throws IOException {
        this.promptLoader = promptLoader;
        this.caseTypeService = caseTypeService;
        this.aiChatService = aiChatService;
        this.serviceCaseService = serviceCaseService;
    }

    @PostMapping("/api/support/ask")
    public Map<String, Object> ask(@RequestBody Map<String, String> payload) throws IOException {
        String userMsg = payload.get("message");
        // 1. 載入 system prompt
        List<Map<String, String>> msgs = promptLoader.getMessages();

        // 2. 加入動態類別說明
        String dynamic = caseTypeService.buildCategoryPrompt();
        msgs.add(Map.of("role", "system", "content", dynamic));

        // 3. 判斷進度查詢
        if (userMsg.contains("進度")) {
            int caseId = Integer.parseInt(payload.get("caseId"));
            return serviceCaseService.getStatusResponse(caseId);
        }

        // 4. 一般問題：呼叫 OpenAI
        msgs.add(Map.of("role", "user", "content", userMsg));
        
        // 確認ai是否接收到檔案
        System.out.println("🔍 傳送給 OpenAI 的 messages：");
        for (Map<String, String> msg : msgs) {
            System.out.println("[" + msg.get("role") + "] " + msg.get("content"));
        }
 
        String aiReply = aiChatService.chat(msgs);
        

        // 5. 回傳 AI 回覆
        Map<String, Object> response = new HashMap<>();
        response.put("response", aiReply);
        return response;
    }
}
