package com.shakemate.notification.util;

import com.shakemate.notification.dto.MemberNotificationDto;
import com.shakemate.notification.dto.NotificationCreateDto;
import com.shakemate.notification.dto.NotificationDto;
import com.shakemate.notification.dto.NotificationTemplateDto;
import com.shakemate.notification.entity.MemberNotification;
import com.shakemate.notification.entity.Notification;
import com.shakemate.notification.entity.NotificationTemplate;
import com.shakemate.notification.enums.NotificationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
public class NotificationMapper {

    @Autowired
    private MapToJsonConverter mapToJsonConverter;

    public NotificationDto toDto(Notification entity) {
        if (entity == null) {
            return null;
        }

        NotificationDto dto = new NotificationDto();
        dto.setNotificationId(entity.getNotificationId());
        dto.setTitle(entity.getTitle());
        dto.setMessage(entity.getMessage());
        dto.setTargetType(entity.getTargetType());
        
        // 設置狀態描述
        try {
            NotificationStatus status = NotificationStatus.fromCode(entity.getStatus());
            dto.setStatus(status.getDescription());
            dto.setStatusCode(entity.getStatus()); // 🔧 同時設置狀態碼
        } catch (IllegalArgumentException e) {
            dto.setStatus("未知狀態");
            dto.setStatusCode(entity.getStatus()); // 🔧 即使是未知狀態也設置原始碼
        }
        
        dto.setScheduledTime(entity.getScheduledTime());
        dto.setCreatedTime(entity.getCreatedTime());
        dto.setUpdatedTime(entity.getUpdatedTime());
        dto.setCreatedBy(entity.getCreatedBy());
        
        if (entity.getTargetCriteria() != null) {
            dto.setRenderParams(entity.getTargetCriteria());
        }
        
        // 新增：設置有效起始與結束時間
        dto.setValidFrom(entity.getValidFrom());
        dto.setValidUntil(entity.getValidUntil());
        
        dto.setNotificationType(entity.getNotificationType());
        dto.setNotificationCategory(entity.getNotificationCategory());
        dto.setNotificationLevel(entity.getNotificationLevel());
        dto.setIsBroadcast(entity.getIsBroadcast());
        dto.setTargetCriteria(entity.getTargetCriteria());
        
        return dto;
    }

    public Notification toEntity(NotificationCreateDto dto) {
        if (dto == null) {
            return null;
        }

        Notification entity = new Notification();
        entity.setTitle(dto.getTitle());
        entity.setMessage(dto.getContent());
        entity.setNotificationType(dto.getType());
        entity.setTargetType(dto.getTargetType());
        entity.setValidFrom(dto.getStartTime());
        entity.setValidUntil(dto.getEndTime());
        
        // 🔧 添加排程時間的映射
        if (dto.getScheduledTime() != null) {
            entity.setScheduledTime(dto.getScheduledTime());
        }
        
        // 設置目標條件
        if (dto.getTargetCriteria() != null) {
            entity.setTargetCriteria(dto.getTargetCriteria());
        }
        
        // 設置默認值
        entity.setNotificationCategory("SYSTEM"); // 默認類別
        entity.setNotificationLevel(1); // 默認優先級
        entity.setStatus(NotificationStatus.DRAFT.getCode());
        entity.setCreatedTime(LocalDateTime.now());
        
        return entity;
    }

    public MemberNotificationDto toMemberNotificationDto(MemberNotification entity) {
        if (entity == null) {
            return null;
        }

        MemberNotificationDto dto = new MemberNotificationDto();
        // 由於當前MemberNotification實體已被暫時註釋，這個方法暫時返回默認值
        dto.setNotificationId(0L);
        dto.setTitle("暫無");
        dto.setMessage("暫無");
        dto.setIsRead(false);
        dto.setReadTime(null);
        dto.setCreatedTime(LocalDateTime.now());
        
        return dto;
    }

    public NotificationTemplateDto toTemplateDto(NotificationTemplate entity) {
        if (entity == null) {
            return null;
        }

        NotificationTemplateDto dto = new NotificationTemplateDto();
        dto.setTemplateId(entity.getTemplateId());
        dto.setTemplateCode(entity.getTemplateCode());
        dto.setTemplateName(entity.getTemplateName());
        dto.setTemplateType(entity.getTemplateType());
        dto.setTemplateCategory(entity.getTemplateCategory());
        dto.setTitleTemplate(entity.getTitleTemplate());
        dto.setContentTemplate(entity.getContentTemplate());
        dto.setHtmlTemplate(entity.getHtmlTemplate());
        dto.setDescription(entity.getDescription());
        dto.setVariables(entity.getVariables());
        dto.setPreviewImage(entity.getPreviewImage());
        dto.setIsActive(entity.getIsActive());
        dto.setIsSystem(entity.getIsSystem());
        dto.setCreatedById(entity.getCreatedById());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        
        return dto;
    }

    public NotificationTemplate toTemplateEntity(NotificationTemplateDto dto) {
        if (dto == null) {
            return null;
        }

        NotificationTemplate entity = new NotificationTemplate();
        entity.setTemplateId(dto.getTemplateId());
        entity.setTemplateCode(dto.getTemplateCode());
        entity.setTemplateName(dto.getTemplateName());
        entity.setTemplateType(dto.getTemplateType());
        entity.setTemplateCategory(dto.getTemplateCategory());
        entity.setTitleTemplate(dto.getTitleTemplate());
        entity.setContentTemplate(dto.getContentTemplate());
        entity.setHtmlTemplate(dto.getHtmlTemplate());
        entity.setDescription(dto.getDescription());
        entity.setVariables(dto.getVariables());
        entity.setPreviewImage(dto.getPreviewImage());
        entity.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        entity.setIsSystem(dto.getIsSystem() != null ? dto.getIsSystem() : false);
        entity.setCreatedById(dto.getCreatedById());
        return entity;
    }
} 