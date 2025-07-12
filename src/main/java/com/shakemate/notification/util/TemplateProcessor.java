package com.shakemate.notification.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通知模板處理工具類
 * 負責處理模板中的變數替換
 */
@Slf4j
@Component
public class TemplateProcessor {

    // 支援 {{variable}} 格式的變數
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{([^}]+)\\}\\}");

    /**
     * 處理模板內容，替換變數
     * 
     * @param template 模板內容
     * @param variables 變數映射
     * @return 處理後的內容
     */
    public String processTemplate(String template, Map<String, Object> variables) {
        if (template == null || template.trim().isEmpty()) {
            return template;
        }
        
        if (variables == null || variables.isEmpty()) {
            log.warn("模板變數為空，返回原始模板: {}", template);
            return template;
        }
        
        try {
            Matcher matcher = VARIABLE_PATTERN.matcher(template);
            StringBuffer result = new StringBuffer();
            
            while (matcher.find()) {
                String variableName = matcher.group(1).trim();
                Object value = variables.get(variableName);
                
                String replacement;
                if (value != null) {
                    replacement = value.toString();
                } else {
                    // 如果變數不存在，保留原始格式或使用預設值
                    replacement = "{{" + variableName + "}}";
                    log.warn("模板變數 '{}' 未找到對應值，保留原始格式", variableName);
                }
                
                matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
            }
            
            matcher.appendTail(result);
            
            String processedTemplate = result.toString();
            log.debug("模板處理完成: {} -> {}", template, processedTemplate);
            return processedTemplate;
            
        } catch (Exception e) {
            log.error("模板處理失敗: {}", template, e);
            return template; // 失敗時返回原始模板
        }
    }

    /**
     * 驗證模板中的變數是否都有對應值
     * 
     * @param template 模板內容
     * @param variables 變數映射
     * @return 是否所有變數都有值
     */
    public boolean validateTemplateVariables(String template, Map<String, Object> variables) {
        if (template == null || variables == null) {
            return false;
        }
        
        Matcher matcher = VARIABLE_PATTERN.matcher(template);
        while (matcher.find()) {
            String variableName = matcher.group(1).trim();
            if (!variables.containsKey(variableName) || variables.get(variableName) == null) {
                log.warn("模板變數驗證失敗: 缺少變數 '{}'", variableName);
                return false;
            }
        }
        
        return true;
    }
} 