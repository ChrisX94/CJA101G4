package com.shakemate.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shakemate.adm.model.AdmVO;
import com.shakemate.adm.model.AdmRepository;
import com.shakemate.notification.dto.NotificationCreateDto;
import com.shakemate.notification.dto.NotificationDto;
import com.shakemate.notification.dto.NotificationTemplateDto;
import com.shakemate.notification.entity.Notification;
import com.shakemate.notification.entity.NotificationTemplate;
import com.shakemate.notification.repository.MemberNotificationRepository;
import com.shakemate.notification.repository.NotificationRepository;
import com.shakemate.notification.repository.NotificationTemplateRepository;
import com.shakemate.notification.util.NotificationMapper;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.shakemate.notification.exception.ResourceNotFoundException;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class AdminNotificationServiceImpl implements AdminNotificationService {

    @Autowired
    private NotificationService notificationService; // 注入使用者端的Service

    @Autowired
    private NotificationTemplateRepository templateRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private MemberNotificationRepository memberNotificationRepository;

    @Autowired
    private AdmRepository admRepository; // 用於關聯創建者

    @Autowired
    private ObjectMapper objectMapper; // 用於JSON序列化

    @Autowired
    private NotificationDispatchService notificationDispatchService;

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationTemplateDto> getAllTemplates(Pageable pageable) {
        return templateRepository.findAll(pageable).map(NotificationMapper::toDto);
    }

    @Override
    public NotificationTemplateDto createTemplate(NotificationTemplateDto templateDto) {
        NotificationTemplate template = NotificationMapper.toEntity(templateDto);
        NotificationTemplate savedTemplate = templateRepository.save(template);
        return NotificationMapper.toDto(savedTemplate);
    }

    @Override
    @Transactional
    public NotificationTemplateDto updateTemplate(Integer id, NotificationTemplateDto templateDto) {
        System.out.println("=== 開始更新模板 ID: " + id + " ===");
        System.out.println("接收到的 DTO templateName: " + templateDto.getTemplateName());
        
        NotificationTemplate existingTemplate = templateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("找不到ID為 " + id + " 的模板"));
        
        System.out.println("更新前的模板名稱: " + existingTemplate.getTemplateName());
        
        // 更新所有可編輯的欄位
        existingTemplate.setTemplateCode(templateDto.getTemplateCode());
        existingTemplate.setTemplateName(templateDto.getTemplateName());
        existingTemplate.setTemplateType(templateDto.getTemplateType());
        existingTemplate.setTemplateCategory(templateDto.getTemplateCategory());
        existingTemplate.setTitleTemplate(templateDto.getTitleTemplate());
        existingTemplate.setMessageTemplate(templateDto.getMessageTemplate());
        existingTemplate.setVariables(templateDto.getVariables() != null ? templateDto.getVariables() : "{}");
        existingTemplate.setDescription(templateDto.getDescription());
        existingTemplate.setIsActive(templateDto.getIsActive() != null ? templateDto.getIsActive() : true);
        existingTemplate.setIsSystem(templateDto.getIsSystem() != null ? templateDto.getIsSystem() : false);
        
        System.out.println("設定後的模板名稱: " + existingTemplate.getTemplateName());
        
        // 強制刷新並保存
        NotificationTemplate updatedTemplate = templateRepository.saveAndFlush(existingTemplate);
        
        System.out.println("保存後的模板名稱: " + updatedTemplate.getTemplateName());
        
        // 重新從資料庫查詢以確保數據正確
        NotificationTemplate reloadedTemplate = templateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("重新載入模板失敗"));
        
        System.out.println("重新載入後的模板名稱: " + reloadedTemplate.getTemplateName());
        System.out.println("=== 模板更新完成 ===");
        
        return NotificationMapper.toDto(reloadedTemplate);
    }

    @Override
    public void deleteTemplate(Integer templateId) {
        if (!templateRepository.existsById(templateId)) {
            throw new ResourceNotFoundException("找不到要刪除的模板: " + templateId);
        }
        templateRepository.deleteById(templateId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDto> getAllNotifications(Pageable pageable) {
        return notificationRepository.findAll(pageable).map(NotificationMapper::toDto);
    }

    @Override
    @Transactional
    public NotificationDto createNotification(NotificationCreateDto createDto, Integer adminId) {
        Notification notification = new Notification();
        Map<String, Object> renderParams = createDto.getRenderParams() != null ? createDto.getRenderParams() : new HashMap<>();

        // 如果使用模板
        if (createDto.getTemplateId() != null) {
            NotificationTemplate template = templateRepository.findById(createDto.getTemplateId())
                .orElseThrow(() -> new ResourceNotFoundException("找不到ID為 " + createDto.getTemplateId() + " 的模板"));

            StringSubstitutor sub = new StringSubstitutor(renderParams);
            notification.setTitle(sub.replace(template.getTitleTemplate()));
            notification.setMessage(sub.replace(template.getMessageTemplate()));
        } else {
            notification.setTitle(createDto.getTitle());
            notification.setMessage(createDto.getMessage());
            // link 欄位不存在於新的 Entity 結構中
        }

        // 根據新的 Entity 結構設置欄位
        notification.setNotificationType("SYSTEM"); // 預設類型
        notification.setNotificationCategory("GENERAL"); // 預設分類
        notification.setNotificationLevel(1); // 預設級別
        
        // 根據 targetType 設置 isBroadcast
        boolean isBroadcast = "BROADCAST".equals(createDto.getTargetType()) || "ALL".equals(createDto.getTargetType());
        notification.setIsBroadcast(isBroadcast);
        
        // 設置目標條件
        Map<String, Object> targetCriteria = new HashMap<>();
        if (createDto.getTargetIds() != null && !createDto.getTargetIds().isEmpty()) {
            targetCriteria.put("userIds", createDto.getTargetIds());
        }
        targetCriteria.put("type", createDto.getTargetType());
        notification.setTargetCriteria(targetCriteria);
        
        notification.setValidFrom(createDto.getScheduledTime());
        notification.setCreatedBy(adminId);
        
        // 將 String status 轉換為 Integer
        notification.setStatus(1); // 1 = PENDING

        Notification savedNotification = notificationRepository.save(notification);

        if (savedNotification.getScheduledTime() == null) {
            notificationDispatchService.dispatchNotification(savedNotification);
        }

        return NotificationMapper.toDto(savedNotification);
    }

    @Override
    @Transactional
    public void sendNotification(Integer notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到ID為 " + notificationId + " 的通知"));

        // 檢查通知狀態，避免重複發送
        Integer status = notification.getStatus();
        if (status != null && (status == 1 || status == 3)) { // 1=PENDING, 3=FAILED
            notificationDispatchService.dispatchNotification(notification);
        } else {
            throw new IllegalStateException("只有在 PENDING 或 FAILED 狀態的通知才能被發送。目前狀態為：" + status);
        }
    }

    @Override
    @Transactional
    public void deleteNotification(Integer notificationId) {
        System.out.println("=== 開始刪除通知 ID: " + notificationId + " ===");
        
        if (!notificationRepository.existsById(notificationId)) {
            throw new ResourceNotFoundException("找不到ID為 " + notificationId + " 的通知，無法刪除");
        }
        
        System.out.println("通知存在，開始刪除相關的會員通知記錄...");
        
        // 先刪除相關的會員通知記錄，避免外鍵約束錯誤
        try {
            memberNotificationRepository.deleteByNotification_NotificationId(notificationId);
            memberNotificationRepository.flush(); // 強制執行刪除操作
            System.out.println("會員通知記錄刪除成功");
        } catch (Exception e) {
            System.err.println("刪除會員通知記錄時發生錯誤: " + e.getMessage());
            throw e;
        }
        
        System.out.println("開始刪除通知本身...");
        
        // 再刪除通知本身
        try {
            notificationRepository.deleteById(notificationId);
            System.out.println("通知刪除成功");
        } catch (Exception e) {
            System.err.println("刪除通知時發生錯誤: " + e.getMessage());
            throw e;
        }
        
        System.out.println("=== 通知刪除完成 ===");
    }

    @Override
    public com.shakemate.notification.dto.NotificationReportDto getNotificationReport(Integer notificationId) {
        // TODO: 實作報告生成邏輯
        return new com.shakemate.notification.dto.NotificationReportDto();
    }
} 