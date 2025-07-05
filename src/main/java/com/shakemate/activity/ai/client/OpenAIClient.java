package com.shakemate.activity.ai.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OpenAIClient {

    private final String apiKey = ""; // ← 請改為從 yml 注入較安全
    private final String endpoint = "https://api.openai.com/v1/chat/completions";
    private final OkHttpClient client = new OkHttpClient();

    public String callChatGPT(String prompt) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        // 1. 準備 JSON 結構
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("temperature", 0.7);
        requestBody.put("messages", List.of(new Message("user", prompt)));

        String json = mapper.writeValueAsString(requestBody);

        // 2. 組送出請求
        Request request = new Request.Builder()
                .url(endpoint)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(json, MediaType.get("application/json")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("OpenAI 呼叫失敗: " + response.code() + " " + response.message());
            }
            return response.body().string();
        }
    }

    // ✅ ✅ ✅ 內部類：用來包裝 messages 陣列
    @Data
    @AllArgsConstructor
    private static class Message {
        private String role;
        private String content;
    }
}
