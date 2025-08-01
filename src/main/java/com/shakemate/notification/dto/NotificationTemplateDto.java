package com.shakemate.notification.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationTemplateDto {

    private Integer templateId;
    private Integer createdById;
    private String templateCode;

    @NotBlank(message = "模板名稱不可為空")
    private String templateName;

    @NotBlank(message = "標題模板不可為空")
    private String titleTemplate;

    private String contentTemplate;

    private String htmlTemplate;

    private String templateType;
    private String templateCategory;
    private String description;
    private String variables; // JSON String
    private String previewImage;
    private Boolean isActive;
    private Boolean isSystem;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

} 