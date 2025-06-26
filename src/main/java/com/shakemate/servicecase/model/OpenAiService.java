package com.shakemate.servicecase.model;

import java.util.Map;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class OpenAiService {

    private final WebClient client;

    public OpenAiService(@Value("${openai.api.key}") String apiKey) {
        this.client = WebClient.builder()
            .baseUrl("https://api.openai.com/v1")
            .defaultHeader("Authorization", "Bearer " + apiKey)
            .build();
    }

    public Mono<String> chatCompletion(String prompt) {
        return client.post()
            .uri("/chat/completions")
            .bodyValue(Map.of(
                "model", "gpt-3.5-turbo",
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
                    var message = (Map<?,?>) ((Map<?,?>) choices.get(0)).get("message");
                    return (String) message.get("content");
                }
                return "抱歉，AI 回覆錯誤。";
            });
    }
}
