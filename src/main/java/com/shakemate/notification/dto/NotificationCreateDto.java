package com.shakemate.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class NotificationCreateDto {

    @NotBlank(message = "通知標題不可為空 (若使用模板則此欄位會被覆蓋)")
    private String title;

    @NotBlank(message = "通知內容不可為空 (若使用模板則此欄位會被覆蓋)")
    private String message;

    private String link;

    @NotBlank(message = "目標類型不可為空")
    private String targetType; // e.g., "ALL", "SPECIFIC_USERS"

    // 可選，但若 targetType 為 SPECIFIC_USERS，應有值
    private List<Integer> targetIds;

    private LocalDateTime scheduledTime;
    private Integer templateId;
    private Map<String, Object> renderParams;
} 