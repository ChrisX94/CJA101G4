package com.shakemate.activity.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shakemate.activity.ai.client.OpenAIClient;
import com.shakemate.activity.ai.dto.TitleTagDTO;
import com.shakemate.activity.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AIAssistService {

    private final OpenAIClient openAIClient;

    public ApiResponse<TitleTagDTO> generateWrappedResponse(String content) {
        if (content == null || content.trim().isEmpty()) {
            return ApiResponse.error(400, "內容不得為空");
        }

        String prompt = """
            根據以下活動文章內容，請產生一個準確的中文標題與 1~3 個關鍵標籤，回傳 JSON 格式：
            「文章內容」：
            %s

            回傳格式：
            {
              "title": "文章標題",
              "tags": ["標籤1", "標籤2", "標籤3"]
            }
        """.formatted(content);

        try {
            String response = openAIClient.callChatGPT(prompt);
            String contentOnly = new ObjectMapper()
                    .readTree(response)
                    .get("choices")
                    .get(0)
                    .get("message")
                    .get("content")
                    .asText();

            TitleTagDTO result = new ObjectMapper().readValue(contentOnly, TitleTagDTO.class);
            return ApiResponse.success(result);

        } catch (Exception e) {
            // 錯誤回傳 HTTP 500 語意的錯誤
            return ApiResponse.error(500, "OpenAI 呼叫失敗: " + e.getMessage());
        }
    }
}
