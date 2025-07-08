package com.shakemate.notification.controller;

import com.shakemate.adm.model.AdmVO;
import com.shakemate.notification.dto.NotificationCreateDto;
import com.shakemate.notification.dto.NotificationDto;
import com.shakemate.notification.dto.NotificationPreviewDto;
import com.shakemate.notification.dto.NotificationReportDto;
import com.shakemate.notification.dto.NotificationTemplateDto;
import com.shakemate.notification.service.AdminNotificationService;
import com.shakemate.notification.service.NotificationReportService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/admin/notifications")
public class AdminNotificationController {

    @Autowired
    private AdminNotificationService adminNotificationService;

    @Autowired
    private NotificationReportService notificationReportService;

    /**
     * 獲取通知列表（分頁）
     */
    @GetMapping("/")
    @ResponseBody
    public ResponseEntity<Page<NotificationDto>> getAllNotifications(Pageable pageable) {
        Page<NotificationDto> notifications = adminNotificationService.getAllNotifications(pageable);
        return ResponseEntity.ok(notifications);
    }

    /**
     * 建立新通知
     */
    @PostMapping("/")
    @ResponseBody
    public ResponseEntity<NotificationDto> createNotification(
            @Valid @RequestBody NotificationCreateDto createDto,
            HttpSession session) {
        
        // 從 session 中獲取管理員 ID
        AdmVO admin = (AdmVO) session.getAttribute("loggedInAdm");
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        NotificationDto createdNotification = adminNotificationService.createNotification(createDto, admin.getAdmId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNotification);
    }

    /**
     * 發送通知
     */
    @PostMapping("/{notificationId}/send")
    @ResponseBody
    public ResponseEntity<Void> sendNotification(@PathVariable Integer notificationId) {
        adminNotificationService.sendNotification(notificationId);
        return ResponseEntity.ok().build();
    }

    /**
     * 刪除通知
     */
    @DeleteMapping("/{notificationId}")
    @ResponseBody
    public ResponseEntity<Void> deleteNotification(@PathVariable Integer notificationId) {
        adminNotificationService.deleteNotification(notificationId);
        return ResponseEntity.ok().build();
    }

    /**
     * 獲取通知報告
     */
    @GetMapping("/{notificationId}/report")
    @ResponseBody
    public ResponseEntity<NotificationReportDto> getNotificationReport(@PathVariable Integer notificationId) {
        NotificationReportDto report = adminNotificationService.getNotificationReport(notificationId);
        return ResponseEntity.ok(report);
    }

    /**
     * 獲取通知預覽信息
     */
    @GetMapping("/{notificationId}/preview")
    @ResponseBody
    public ResponseEntity<NotificationPreviewDto> getNotificationPreview(@PathVariable Integer notificationId) {
        NotificationPreviewDto preview = adminNotificationService.getNotificationPreview(notificationId);
        return ResponseEntity.ok(preview);
    }

    /**
     * 通知管理頁面
     */
    @GetMapping("/manage-page")
    public String getNotificationManagePage() {
        return "back-end/notification/notifications";
    }

    /**
     * 通知模板管理頁面
     */
    @GetMapping("/templates-page")
    public String getTemplatesPage() {
        return "back-end/notification/templates";
    }

    /**
     * 通知報表頁面
     */
    @GetMapping("/report/page")
    public String getReportPage() {
        return "back-end/notification/report";
    }

    /**
     * 獲取通知模板列表（分頁）
     */
    @GetMapping("/templates")
    @ResponseBody
    public ResponseEntity<Page<NotificationTemplateDto>> getAllTemplates(Pageable pageable) {
        Page<NotificationTemplateDto> templates = adminNotificationService.getAllTemplates(pageable);
        return ResponseEntity.ok(templates);
    }

    /**
     * 建立新模板
     */
    @PostMapping("/templates")
    @ResponseBody
    public ResponseEntity<NotificationTemplateDto> createTemplate(
            @Valid @RequestBody NotificationTemplateDto templateDto,
            HttpSession session) {
        // 从 session 中获取管理员对象
        AdmVO admin = (AdmVO) session.getAttribute("loggedInAdm");
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        templateDto.setCreatedById(admin.getAdmId());
        if (templateDto.getIsActive() == null) {
            templateDto.setIsActive(true);
        }
        NotificationTemplateDto createdTemplate = adminNotificationService.createTemplate(templateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTemplate);
    }

    /**
     * 更新模板
     */
    @PutMapping("/templates/{templateId}")
    @ResponseBody
    public ResponseEntity<NotificationTemplateDto> updateTemplate(
            @PathVariable Integer templateId,
            @Valid @RequestBody NotificationTemplateDto templateDto) {
        NotificationTemplateDto updatedTemplate = adminNotificationService.updateTemplate(templateId, templateDto);
        return ResponseEntity.ok(updatedTemplate);
    }

    /**
     * 刪除模板
     */
    @DeleteMapping("/templates/{templateId}")
    @ResponseBody
    public ResponseEntity<Void> deleteTemplate(@PathVariable Integer templateId) {
        adminNotificationService.deleteTemplate(templateId);
        return ResponseEntity.ok().build();
    }

    /**
     * 檢查管理員登入狀態
     */
    @GetMapping("/check-auth")
    @ResponseBody
    public ResponseEntity<Void> checkAuth(HttpSession session) {
        AdmVO admin = (AdmVO) session.getAttribute("loggedInAdm");
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok().build();
    }

    /**
     * 查詢單筆通知詳情
     */
    @GetMapping("/{notificationId}")
    @ResponseBody
    public ResponseEntity<NotificationDto> getNotificationDetail(@PathVariable Integer notificationId) {
        NotificationDto dto = adminNotificationService.getNotificationById(notificationId);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }
} 