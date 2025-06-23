package com.shakemate.servicecase.controller;

//import java.util.Map;
//import java.util.List;
//
//import com.shakemate.ai.OpenAiService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.web.bind.annotation.*;
//import reactor.core.publisher.Mono;
//
//@RestController
//@RequestMapping("/api/ai")
//public class AiController {
//
//    @Autowired
//    private OpenAiService openAiService;
//
//    @PostMapping(value = "/ask", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public Mono<String> askAi(@RequestBody Map<String, Object> payload) {
//        Integer categoryId = (Integer) payload.get("categoryId");
//        String question = (String) payload.get("question");
//
//        // 你可以在這裡根據 categoryId 加入前置提示（prompt engineering）
//        String prompt = "Category " + categoryId + ": " + question;
//
//        return openAiService.chatCompletion(prompt);
//    }
//}
