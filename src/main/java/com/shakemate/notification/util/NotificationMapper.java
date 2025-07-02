package com.shakemate.notification.util;

import com.shakemate.notification.dto.NotificationDto;
import com.shakemate.notification.dto.NotificationTemplateDto;
import com.shakemate.notification.entity.Notification;
import com.shakemate.notification.entity.NotificationTemplate;

public class NotificationMapper {

    // For Notification
    public static NotificationDto toDto(Notification notification) {
        if (notification == null) return null;
        NotificationDto dto = new NotificationDto();
        dto.setNotificationId(notification.getNotificationId());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setLink(notification.getLink());
        dto.setTargetType(notification.getTargetType());
        dto.setTargetIds(notification.getTargetIds());
        dto.setScheduledTime(notification.getScheduledTime());
        dto.setCreatedBy(notification.getCreatedBy());
        
        // 將 Integer status 轉換為 String
        Integer statusInt = notification.getStatus();
        String statusStr = "UNKNOWN";
        if (statusInt != null) {
            switch (statusInt) {
                case 0: statusStr = "DRAFT"; break;
                case 1: statusStr = "PENDING"; break;
                case 2: statusStr = "SENT"; break;
                case 3: statusStr = "FAILED"; break;
                default: statusStr = "UNKNOWN"; break;
            }
        }
        dto.setStatus(statusStr);
        
        dto.setRenderParams(notification.getRenderParams());
        dto.setCreatedTime(notification.getCreatedTime());
        dto.setUpdatedTime(notification.getUpdatedTime());
        dto.setSentTime(notification.getSentTime());
        dto.setIsRead(null); // This field belongs to MemberNotification
        dto.setReadTime(null); // This field belongs to MemberNotification
        return dto;
    }

    // For NotificationTemplate
    public static NotificationTemplateDto toDto(NotificationTemplate template) {
        if (template == null) return null;
        NotificationTemplateDto dto = new NotificationTemplateDto();
        dto.setTemplateId(template.getTemplateId());
        dto.setCreatedById(template.getCreatedById());
        dto.setTemplateCode(template.getTemplateCode());
        dto.setTemplateName(template.getTemplateName());
        dto.setTitleTemplate(template.getTitleTemplate());
        dto.setMessageTemplate(template.getMessageTemplate());
        dto.setTemplateType(template.getTemplateType());
        dto.setTemplateCategory(template.getTemplateCategory());
        dto.setDescription(template.getDescription());
        dto.setVariables(template.getVariables());
        dto.setPreviewImage(template.getPreviewImage());
        dto.setIsActive(template.getIsActive());
        dto.setIsSystem(template.getIsSystem());
        dto.setCreatedAt(template.getCreatedAt());
        dto.setUpdatedAt(template.getUpdatedAt());
        return dto;
    }

    public static NotificationTemplate toEntity(NotificationTemplateDto dto) {
        if (dto == null) return null;
        NotificationTemplate template = new NotificationTemplate();
        template.setTemplateId(dto.getTemplateId());
        // template.setCreatedBy(dto.getCreatedBy());  // This needs to be checked
        template.setCreatedById(dto.getCreatedById());
        template.setTemplateCode(dto.getTemplateCode());
        template.setTemplateName(dto.getTemplateName());
        template.setTitleTemplate(dto.getTitleTemplate());
        template.setMessageTemplate(dto.getMessageTemplate());
        template.setTemplateType(dto.getTemplateType());
        template.setTemplateCategory(dto.getTemplateCategory());
        template.setDescription(dto.getDescription());
        template.setVariables(dto.getVariables() != null ? dto.getVariables() : "{}");
        template.setPreviewImage(dto.getPreviewImage());
        template.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        template.setIsSystem(dto.getIsSystem() != null ? dto.getIsSystem() : false);
        template.setCreatedAt(dto.getCreatedAt());
        template.setUpdatedAt(dto.getUpdatedAt());
        
        // 設置hasContent欄位，如果有messageTemplate就設為true
        template.setHasContent(dto.getMessageTemplate() != null && !dto.getMessageTemplate().trim().isEmpty());
        
        return template;
    }
} 