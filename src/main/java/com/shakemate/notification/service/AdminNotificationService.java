package com.shakemate.notification.service;

import com.shakemate.notification.dto.NotificationTemplateDto;
import com.shakemate.notification.dto.NotificationCreateDto;
import com.shakemate.notification.dto.NotificationDto;
import com.shakemate.notification.dto.NotificationPreviewDto;
import com.shakemate.notification.dto.NotificationReportDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminNotificationService {

    /**
     * 獲取通知模板列表（分頁）
     * @param pageable 分頁參數
     * @return 分頁後的模板 DTO
     */
    Page<NotificationTemplateDto> getAllTemplates(Pageable pageable);

    /**
     * 建立一個新的通知模板
     * @param templateDto 包含模板資訊的 DTO
     * @return 建立後的模板 DTO
     */
    NotificationTemplateDto createTemplate(NotificationTemplateDto templateDto);

    /**
     * 創建一個新的通知。
     * @param createDto 通知創建的 DTO
     * @param adminId 創建該通知的管理員 ID
     * @return 創建的通知 DTO
     */
    NotificationDto createNotification(NotificationCreateDto createDto, Integer adminId);

    /**
     * 獲取通知列表（分頁）
     * @param pageable 分頁參數
     * @return 分頁後的通知 DTO
     */
    Page<NotificationDto> getAllNotifications(Pageable pageable);

    /**
     * 手動觸發一個通知的發送
     * @param notificationId 要發送的通知 ID
     */
    void sendNotification(Integer notificationId);

    /**
     * 更新一個現有的通知模板
     * @param templateId 要更新的模板 ID
     * @param templateDto 包含新模板資訊的 DTO
     * @return 更新後的模板 DTO
     */
    NotificationTemplateDto updateTemplate(Integer templateId, NotificationTemplateDto templateDto);

    /**
     * 刪除一個通知模板
     * @param templateId 要刪除的模板 ID
     */
    void deleteTemplate(Integer templateId);

    /**
     * 刪除一個通知
     * @param notificationId 要刪除的通知 ID
     */
    void deleteNotification(Integer notificationId);

    /**
     * 獲取指定通知的成效報告
     * @param notificationId 通知的 ID
     * @return 報告 DTO
     */
    NotificationReportDto getNotificationReport(Integer notificationId);
    
    /**
     * 獲取通知預覽信息，包括預估接收人數等
     * @param notificationId 通知ID
     * @return 通知預覽DTO
     */
    NotificationPreviewDto getNotificationPreview(Integer notificationId);

    /**
     * 查詢單筆通知詳情
     */
    NotificationDto getNotificationById(Integer notificationId);

} 