package com.shakemate.casetype.service;

import com.shakemate.casetype.service.PromptLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;


@Service
public class OpenAiService {

    private final WebClient client;
    private final PromptLoader promptLoader;

    public OpenAiService(
            WebClient.Builder webClientBuilder,
            PromptLoader promptLoader,
            @Value("${openai.api.key}") String apiKey                  // 如果你喜歡用 @Value 注入也可以
    ) {
        this.client = webClientBuilder
            .baseUrl("https://api.openai.com/v1")
            .defaultHeader("Authorization", "Bearer " + apiKey)
            .defaultHeader("Content-Type", "application/json")
            .build();
        this.promptLoader = promptLoader;
    }

    /**
     * 使用者點擊分類後，呼叫這個方法
     * @param selectedCategory 前端傳進來的 category
     * @return AI 的純字串回答
     */
    public String chatWithCategory(String selectedCategory) {
        // 1. 準備 system + user messages
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of(
            "role",    "system",
            "content", promptLoader.getSystemPrompt()
        ));
        // 把點擊的 category 與 reg prompt 串在一起
        String userContent = selectedCategory + "\n\n" + promptLoader.getRegPrompt();
        messages.add(Map.of(
            "role",    "user",
            "content", userContent
        ));

        // 2. 呼叫 OpenAI API
        Map<String, Object> body = Map.of(
            "model",    "gpt-4o",
            "messages", messages
        );
        Map<?, ?> result = client.post()
            .uri("/chat/completions")
            .bodyValue(body)
            .retrieve()
            .bodyToMono(Map.class)
            .block();

        // 3. 處理回傳並回傳純字串
        if (result == null || !result.containsKey("choices")) {
            return "AI 回覆異常。";
        }
        var choices = (List<?>) result.get("choices");
        if (!choices.isEmpty()) {
            var msg = (Map<?, ?>) ((Map<?, ?>) choices.get(0)).get("message");
            return (String) msg.get("content");
        }
        return "AI 沒有任何回答。";
    }
}
