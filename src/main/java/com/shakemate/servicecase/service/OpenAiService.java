package com.shakemate.servicecase.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class OpenAiService {

    private final WebClient client;

    public OpenAiService(@Value("${openai.api.key}") String apiKey) {
        this.client = WebClient.builder()
            .baseUrl("https://api.openai.com/v1")
            .defaultHeader("Authorization", "Bearer " + apiKey)
            .defaultHeader("Content-Type", "application/json")
            .build();
    }
    
    /**
     * 給完整 messages list，產生回覆（同步 blocking 版本）
     */
    public String chat(List<Map<String, String>> messages) {
        Map<String, Object> body = Map.of(
            "model", "gpt-4o",
            "messages", messages
        );

        Map<?, ?> result = client.post()
            .uri("/chat/completions")
            .bodyValue(body)
            .retrieve()
            .bodyToMono(Map.class)
            .block(); // Blocking

        if (result == null || !result.containsKey("choices")) {
            return "AI 回覆異常。";
        }

        var choices = (List<?>) result.get("choices");
        if (!choices.isEmpty()) {
            var message = (Map<?, ?>) ((Map<?, ?>) choices.get(0)).get("message");
            return (String) message.get("content");
        }

        return "AI 沒有任何回答。";
    }
    
    /**
     * 給一段文字 prompt，產生聊天回覆
     */
    public Mono<String> chatCompletion(String prompt) {
        return client.post()
            .uri("/chat/completions")
            .bodyValue(Map.of(
                "model", "gpt-4o",
                "messages", List.of(
                    Map.of("role", "system", "content", "你是一個友善的客服助手。"),
                    Map.of("role", "user", "content", prompt)
                )
            ))
            .retrieve()
            .bodyToMono(Map.class)
            .map(body -> {
                var choices = (List<?>) body.get("choices");
                if (!choices.isEmpty()) {
                    var message = (Map<?, ?>) ((Map<?, ?>) choices.get(0)).get("message");
                    return (String) message.get("content");
                }
                return "抱歉，AI 回覆錯誤。";
            });
    }

}
