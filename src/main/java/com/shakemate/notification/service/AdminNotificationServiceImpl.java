package com.shakemate.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shakemate.adm.model.AdmVO;
import com.shakemate.adm.model.AdmRepository;
import com.shakemate.notification.dto.NotificationCreateDto;
import com.shakemate.notification.dto.NotificationDto;
import com.shakemate.notification.dto.NotificationPreviewDto;
import com.shakemate.notification.dto.NotificationReportDto;
import com.shakemate.notification.dto.NotificationTemplateDto;
import com.shakemate.notification.entity.Notification;
import com.shakemate.notification.entity.NotificationTemplate;
// import com.shakemate.notification.repository.MemberNotificationRepository;
import com.shakemate.notification.repository.NotificationRepository;
import com.shakemate.notification.repository.NotificationTemplateRepository;
import com.shakemate.notification.util.NotificationMapper;
import com.shakemate.notification.util.MapToJsonConverter;
import com.shakemate.notification.util.UserSpecificationBuilder;
import com.shakemate.user.dao.UsersRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.shakemate.notification.exception.ResourceNotFoundException;
import org.springframework.data.jpa.domain.Specification;
import com.shakemate.user.model.Users;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    // @Autowired
    // private MemberNotificationRepository memberNotificationRepository;

    @Autowired
    private AdmRepository admRepository; // 用於關聯創建者

    @Autowired
    private ObjectMapper objectMapper; // 用於JSON序列化

    @Autowired
    private NotificationDispatchService notificationDispatchService;

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private MapToJsonConverter mapToJsonConverter;

    @Autowired
    private UsersRepository userRepository;

    @Autowired
    private UserSpecificationBuilder userSpecificationBuilder;

    @Autowired
    private com.shakemate.notification.repository.MemberNotificationRepository memberNotificationRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationTemplateDto> getAllTemplates(Pageable pageable) {
        Page<NotificationTemplate> templates = templateRepository.findAll(pageable);
        return templates.map(notificationMapper::toTemplateDto);
    }

    @Override
    @Transactional
    public NotificationTemplateDto createTemplate(NotificationTemplateDto templateDto) {
        NotificationTemplate template = notificationMapper.toTemplateEntity(templateDto);
        template.setCreatedAt(LocalDateTime.now());
        NotificationTemplate savedTemplate = templateRepository.save(template);
        return notificationMapper.toTemplateDto(savedTemplate);
    }

    @Override
    @Transactional
    public NotificationTemplateDto updateTemplate(Integer templateId, NotificationTemplateDto templateDto) {
        NotificationTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new ResourceNotFoundException("模板不存在，ID: " + templateId));
        
        // 更新模板屬性
        template.setTemplateName(templateDto.getTemplateName());
        template.setTitleTemplate(templateDto.getTitleTemplate());
        template.setContentTemplate(templateDto.getContentTemplate());
        template.setDescription(templateDto.getDescription());
        template.setUpdatedAt(LocalDateTime.now());
        
        NotificationTemplate updatedTemplate = templateRepository.save(template);
        return notificationMapper.toTemplateDto(updatedTemplate);
    }

    @Override
    @Transactional
    public void deleteTemplate(Integer templateId) {
        if (!templateRepository.existsById(templateId)) {
            throw new ResourceNotFoundException("模板不存在，ID: " + templateId);
        }
        templateRepository.deleteById(templateId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDto> getAllNotifications(Pageable pageable) {
        Page<Notification> notifications = notificationRepository.findAll(pageable);
        return notifications.map(notificationMapper::toDto);
    }

    @Override
    @Transactional
    public NotificationDto createNotification(NotificationCreateDto createDto, Integer adminId) {
        Notification notification = notificationMapper.toEntity(createDto);
        
        if (createDto.getTargetCriteria() != null) {
            notification.setTargetCriteria(createDto.getTargetCriteria());
        }
        
        notification.setCreatedTime(LocalDateTime.now());
        notification.setCreatedBy(adminId);
        
        // 設置通知狀態為草稿
        notification.setStatus(com.shakemate.notification.enums.NotificationStatus.DRAFT.getCode());

        Notification savedNotification = notificationRepository.save(notification);

        return notificationMapper.toDto(savedNotification);
    }

    @Override
    @Transactional
    public void sendNotification(Integer notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("通知不存在，ID: " + notificationId));

        // 檢查通知狀態，避免重複發送
        Integer status = notification.getStatus();
        com.shakemate.notification.enums.NotificationStatus currentStatus = null;
        try {
            currentStatus = com.shakemate.notification.enums.NotificationStatus.fromCode(status);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("通知狀態無效: " + status);
        }
        
        if (currentStatus == com.shakemate.notification.enums.NotificationStatus.DRAFT) {
            notificationDispatchService.dispatchNotification(notification);
        } else {
            throw new IllegalStateException("只有在 PENDING 或 FAILED 狀態的通知才能被發送。目前狀態為：" + currentStatus.getDescription());
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
        // try {
        //     memberNotificationRepository.deleteByNotification_NotificationId(notificationId);
        //     memberNotificationRepository.flush(); // 強制執行刪除操作
        //     System.out.println("會員通知記錄刪除成功");
        // } catch (Exception e) {
        //     System.err.println("刪除會員通知記錄時發生錯誤: " + e.getMessage());
        //     throw e;
        // }
        
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
    public NotificationReportDto getNotificationReport(Integer notificationId) {
        // 取得通知主表資訊
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("通知不存在，ID: " + notificationId));
        // 僅統計已發布的通知
        com.shakemate.notification.enums.NotificationStatus status = com.shakemate.notification.enums.NotificationStatus.fromCode(notification.getStatus());
        if (status != com.shakemate.notification.enums.NotificationStatus.PUBLISHED) {
            throw new IllegalStateException("僅能查詢已發布的通知報表");
        }
        NotificationReportDto dto = new NotificationReportDto();
        dto.setNotificationId(notificationId);
        dto.setNotificationTitle(notification.getTitle());
        // 總發送數
        long totalSent = memberNotificationRepository.countByNotification(notificationId);
        dto.setTotalSent(totalSent);
        // 成功數
        long successCount = memberNotificationRepository.countByNotificationAndDeliveryStatus(notificationId, com.shakemate.notification.enums.DeliveryStatus.SUCCESS);
        dto.setSuccessCount(successCount);
        // 失敗數
        long failureCount = memberNotificationRepository.countByNotificationAndDeliveryStatus(notificationId, com.shakemate.notification.enums.DeliveryStatus.FAILED);
        dto.setFailureCount(failureCount);
        // 成功率
        double successRate = totalSent > 0 ? (double) successCount / totalSent : 0.0;
        dto.setSuccessRate(successRate);
        // 已讀數
        long readCount = memberNotificationRepository.countByNotificationAndIsReadTrue(notificationId);
        dto.setReadCount(readCount);
        // 已讀率
        double readRate = totalSent > 0 ? (double) readCount / totalSent : 0.0;
        dto.setReadRate(readRate);
        // 點擊數（userInteraction=1 代表點擊）
        long clickCount = memberNotificationRepository.countByNotificationAndUserInteraction(notificationId, 1);
        dto.setClickCount(clickCount);
        // 點擊率
        double clickRate = totalSent > 0 ? (double) clickCount / totalSent : 0.0;
        dto.setClickRate(clickRate);
        // --- 時間序列統計（發送量折線圖）---
        // 查詢該通知所有會員通知的發送時間，按天分組統計
        List<Object[]> trendList = memberNotificationRepository.findSendTrendByNotification(notificationId);
        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();
        for (Object[] row : trendList) {
            labels.add(String.valueOf(row[0])); // 日期字符串
            data.add((Long) row[1]); // 當天發送量
        }
        Map<String, Object> sendTrend = new HashMap<>();
        sendTrend.put("labels", labels);
        sendTrend.put("data", data);
        dto.setSendTrend(sendTrend);
        // 詳細台灣繁體中文註釋：
        // 1. 僅統計已發布通知，草稿/撤回/過期不統計
        // 2. 所有統計數據均來自會員通知表，確保數據準確
        // 3. 點擊數以 userInteraction=1 為依據，若有多種互動可擴充
        return dto;
    }

    @Override
    public NotificationPreviewDto getNotificationPreview(Integer notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("通知不存在，ID: " + notificationId));
        
        NotificationPreviewDto previewDto = new NotificationPreviewDto();
        previewDto.setNotificationId(notification.getNotificationId());
        previewDto.setTitle(notification.getTitle());
        previewDto.setContent(notification.getMessage());
        previewDto.setType(notification.getNotificationType());
        previewDto.setTargetType(notification.getTargetType());
        
        // 設置狀態描述
        try {
            com.shakemate.notification.enums.NotificationStatus status = com.shakemate.notification.enums.NotificationStatus.fromCode(notification.getStatus());
            previewDto.setStatusDescription(status.getDescription());
        } catch (IllegalArgumentException e) {
            previewDto.setStatusDescription("未知狀態");
        }
        
        // 設置排程發送時間
        previewDto.setScheduledTime(notification.getScheduledTime());
        
        // 解析目標條件
        if (notification.getTargetCriteria() != null && !notification.getTargetCriteria().isEmpty()) {
            Map<String, Object> targetCriteria = notification.getTargetCriteria();
            previewDto.setTargetCriteria(targetCriteria);
            
            // 計算預估接收人數
            if ("ALL".equals(notification.getTargetType())) {
                // 廣播模式，獲取所有用戶數量
                long count = userRepository.count();
                previewDto.setEstimatedRecipients((int) count);
            } else if ("SPECIFIC".equals(notification.getTargetType())) {
                // 特定用戶模式，從targetCriteria中獲取用戶ID列表
                if (targetCriteria.containsKey("userIds")) {
                    Object userIdsObj = targetCriteria.get("userIds");
                    if (userIdsObj instanceof Iterable) {
                        int count = 0;
                        for (Object ignored : (Iterable<?>) userIdsObj) {
                            count++;
                        }
                        previewDto.setEstimatedRecipients(count);
                    }
                }
            } else if ("TAG".equals(notification.getTargetType())) {
                // 標籤模式，根據條件查詢符合的用戶數量
                Specification<Users> spec = userSpecificationBuilder.buildSpecification(targetCriteria);
                long count = userRepository.count(spec);
                previewDto.setEstimatedRecipients((int) count);
            }
        } else {
            // 如果沒有指定條件，則設置為0
            previewDto.setEstimatedRecipients(0);
        }
        
        return previewDto;
    }

    /**
     * 智能選擇最適合的模板內容
     * 優先級規則：
     * 1. EMAIL 類型：優先 HTML，回退到純文字
     * 2. 其他類型：優先純文字，回退到 HTML（去除標籤）
     * 3. 如果都沒有，返回 null
     */
    private String selectBestTemplate(NotificationTemplate template) {
        String contentTemplate = template.getContentTemplate();
        String htmlTemplate = template.getHtmlTemplate();
        String templateType = template.getTemplateType();
        
        boolean hasContent = contentTemplate != null && !contentTemplate.trim().isEmpty();
        boolean hasHtml = htmlTemplate != null && !htmlTemplate.trim().isEmpty();
        
        if ("EMAIL".equals(templateType)) {
            // EMAIL 類型：優先使用 HTML，回退到純文字
            if (hasHtml) {
                return htmlTemplate;
            } else if (hasContent) {
                return contentTemplate;
            }
        } else {
            // 其他類型（PUSH、SMS、SYSTEM）：優先使用純文字，回退到 HTML
            if (hasContent) {
                return contentTemplate;
            } else if (hasHtml) {
                // 將 HTML 轉換為純文字（簡單的標籤移除）
                return stripHtmlTags(htmlTemplate);
            }
        }
        
        return null; // 兩個模板都為空
    }
    
    /**
     * 簡單的 HTML 標籤移除工具
     * 用於將 HTML 模板轉換為純文字版本
     */
    private String stripHtmlTags(String html) {
        if (html == null) return null;
        
        return html
            .replaceAll("<[^>]*>", "") // 移除所有 HTML 標籤
            .replaceAll("&nbsp;", " ") // 替換空格實體
            .replaceAll("&lt;", "<")   // 替換小於號實體
            .replaceAll("&gt;", ">")   // 替換大於號實體
            .replaceAll("&amp;", "&")  // 替換 & 實體
            .replaceAll("&quot;", "\"") // 替換引號實體
            .replaceAll("\\s+", " ")   // 合併多個空白字符
            .trim();                   // 移除首尾空白
    }

    @Override
    public NotificationDto getNotificationById(Integer notificationId) {
        return notificationRepository.findById(notificationId)
                .map(notificationMapper::toDto)
                .orElse(null);
    }
} 