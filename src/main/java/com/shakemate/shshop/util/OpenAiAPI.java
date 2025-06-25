package com.shakemate.shshop.util;

import java.io.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.shakemate.shshop.dao.ShShopRepository;
import com.shakemate.shshop.dto.ShProdDto;
import com.shakemate.shshop.service.ShShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class OpenAiAPI {

    private final OpenAIConfig openAIConfig;

    @Autowired
    public OpenAiAPI(OpenAIConfig openAIConfig) {
        this.openAIConfig = openAIConfig;
    }



    public String getResult(String systemRole, String content) {

        try {
            String key = openAIConfig.getApiKey();
            String url = openAIConfig.getApiUrl();

            System.out.println(key);
            List<MessageForOpenAi> messages = new ArrayList<>();
            messages.add(new MessageForOpenAi("system", systemRole)); // 設定gpt角色
            messages.add(new MessageForOpenAi("user", content)); // 輸入要翻譯的內容
            Gson gson = new Gson();
            JsonObject json = new JsonObject();
            json.addProperty("model", "gpt-4o-mini"); // 選擇模型(demo時改成gpt-4o)
            json.add("messages", gson.toJsonTree(messages)); // 將List<Message> messages轉成json物件

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url)) //建立連線
                    .header("Content-Type", "application/json") //傳送資料形式
                    .header("Authorization", "Bearer " + key) // 傳送key
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(json)))
                    .build();
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString()); // 取的HttpResponse 的物件
            JsonObject jsonRes = gson.fromJson(res.body(), JsonObject.class); // 取得res.body()中的json物件，並轉成JsonObject.class
//            System.out.println(jsonRes.toString());
            String result = jsonRes.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString();
//            System.out.println(result);
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
    }

    public String getSystemSetting() {
        try {
            ClassPathResource resource = new ClassPathResource("static/shshop/otherResource/regulation.txt");
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "你是一位審核員，請根據規則審核內容。"; // fallback 預設字串
        }
    }

    public String buildUserPrompt(List<ShProdDto> dtoList) {
        StringBuilder prompt = new StringBuilder();

        for (ShProdDto dto : dtoList) {
            // 只處理「審核中」的商品（例如 prodStatus == 0）
            if (dto.getProdStatus() != 0) continue;

            prompt.append("prodId").append(dto.getProdId()).append("\n")
                    .append("prodName：").append(dto.getProdName()).append("\n")
                    .append("userName：").append(dto.getUserName()).append("\n")
                    .append("prodTypeName：").append(dto.getProdTypeName()).append("\n")
                    .append("prodBrand：").append(dto.getProdBrand()).append("\n")
                    .append("prodPrice：").append(dto.getProdPrice()).append("\n")
                    .append("prodCount：").append(dto.getProdCount()).append("\n")
                    .append("prodDesc：").append(dto.getProdDesc()).append("\n")
                    .append("prodContent：").append(dto.getProdContent()).append("\n")
                    .append("prodStatus :").append(dto.getProdStatus()).append("\n");

            List<String> picUrls = dto.getPicUrls();
            if (picUrls != null && !picUrls.isEmpty()) {
                prompt.append("圖片：").append(picUrls.get(0)).append("\n");
            }

        }
        return prompt.toString();
    }


}

@Component
class OpenAIConfig {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;


    public String getApiKey() {
        return apiKey;
    }
    public String getApiUrl() {
        return apiUrl;
    }

}