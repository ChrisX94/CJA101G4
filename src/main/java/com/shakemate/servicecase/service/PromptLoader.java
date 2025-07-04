package com.shakemate.servicecase.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

@Component
public class PromptLoader {

    private final String resourcePath;
    private final List<Map<String, String>> messages;
    

    public PromptLoader(@Value("${prompt.resource.path:static/servicecase/otherResource/reg.txt}") String resourcePath) throws IOException {
        this.resourcePath = resourcePath;
        this.messages = loadMessages();
    }

    private List<Map<String, String>> loadMessages() throws IOException {
        List<Map<String, String>> list = new ArrayList<>();
        InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (is == null) {
            throw new IOException("無法載入 prompt 檔案：" + resourcePath);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            String currentRole = null;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.matches("\\[.*\\]")) {
                    if (currentRole != null) {
                        list.add(createMessage(currentRole, sb.toString()));
                    }
                    currentRole = line.replaceAll("[\\[\\]]", "").toLowerCase();
                    sb.setLength(0);
                } else {
                    sb.append(line).append("\n");
                }
            }
            if (currentRole != null && sb.length() > 0) {
                list.add(createMessage(currentRole, sb.toString()));
            }
        }
       
        return list;
    }

    private Map<String, String> createMessage(String role, String content) {
        Map<String, String> msg = new HashMap<>();
        msg.put("role", role);
        msg.put("content", content.trim());
        return msg;
    }

    public List<Map<String, String>> getMessages() {
        return new ArrayList<>(messages);
    }
}