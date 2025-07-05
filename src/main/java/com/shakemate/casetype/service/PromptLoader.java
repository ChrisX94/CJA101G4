package com.shakemate.casetype.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class PromptLoader {

    private static final String REG_PATH = "static/casetype/prompts/reg.txt";

    // 讀完之後，直接分好 system / user 兩段
    private final String systemPrompt;
    private final String regPrompt;

    public PromptLoader() {
        var messages = loadMessages(REG_PATH);
        this.systemPrompt = messages.getOrDefault("system", "");
        this.regPrompt = messages.getOrDefault("user", "");
    }

    private Map<String, String> loadMessages(String path) {
        Map<String, String> map = new HashMap<>();
        try {
            ClassPathResource resource = new ClassPathResource(path);
            try (InputStream is = resource.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line, currentKey = null;
                StringBuilder sb = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.matches("\\[.*\\]")) {
                        if (currentKey != null) {
                            map.put(currentKey, sb.toString().trim());
                        }
                        currentKey = line.replaceAll("[\\[\\]]", "").toLowerCase();
                        sb.setLength(0);
                    } else {
                        sb.append(line).append("\n");
                    }
                }
                if (currentKey != null) {
                    map.put(currentKey, sb.toString().trim());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("無法載入 prompt 檔案：" + path, e);
        }
        return map;
    }

    /** 取得 system prompt（對應 [system]…） */
    public String getSystemPrompt() {
        return systemPrompt;
    }

    /** 取得 reg prompt（對應 [user]…） */
    public String getRegPrompt() {
        return regPrompt;
    }
}
