package com.shakemate.notification.controller;

import com.shakemate.adm.model.AdmVO;
import com.shakemate.notification.dto.NotificationCreateDto;
import com.shakemate.notification.dto.NotificationDto;
import com.shakemate.notification.dto.NotificationStatsDto;
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

    // Page navigation
    @GetMapping("/templates-page")
    public String templatesPage() {
        return "back-end/notification/templates";
    }

    @GetMapping("/manage-page")
    public String notificationsPage() {
        return "back-end/notification/notifications";
    }

    @GetMapping("/report/page")
    public String getReportPage() {
        return "back-end/notification/report";
    }

    // TODO:
    // POST /: 建立一則新的通知（廣播、精準、標籤）
    // GET /: 查詢通知列表
    // PUT /{id}: 更新處於草稿狀態的通知
    // DELETE /{id}: 刪除通知
    // POST /{id}/send: 手動觸發發送
    // GET /{id}/report: 查看通知成效報告

    @PostMapping("/")
    @ResponseBody
    public ResponseEntity<NotificationDto> createNotification(@Valid @RequestBody NotificationCreateDto createDto, HttpSession session) {
        System.out.println("=== createNotification called ===");
        System.out.println("CreateDto: " + createDto);
        
        AdmVO currentAdmin = (AdmVO) session.getAttribute("loggedInAdm");
        System.out.println("Current admin from session: " + currentAdmin);
        
        Integer adminId;
        if (currentAdmin == null) {
            System.out.println("No admin in session, using default adminId = 1");
            adminId = 1; // 暫時使用預設值
        } else {
            adminId = currentAdmin.getAdmId();
        }
        
        try {
            NotificationDto notification = adminNotificationService.createNotification(createDto, adminId);
            System.out.println("Notification created successfully: " + notification);
            return new ResponseEntity<>(notification, HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println("Error creating notification: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping("/")
    @ResponseBody
    public ResponseEntity<Page<NotificationDto>> getAllNotifications(Pageable pageable) {
        Page<NotificationDto> notifications = adminNotificationService.getAllNotifications(pageable);
        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/{id}/send")
    @ResponseBody
    public ResponseEntity<Void> sendNotification(@PathVariable Integer id) {
        adminNotificationService.sendNotification(id);
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteNotification(@PathVariable Integer id) {
        adminNotificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    // === Template Management ===

    @GetMapping("/templates")
    @ResponseBody
    public ResponseEntity<Page<NotificationTemplateDto>> getAllTemplates(Pageable pageable) {
        System.out.println("=== AdminNotificationController.getAllTemplates() called ===");
        System.out.println("Pageable: " + pageable);
        try {
            Page<NotificationTemplateDto> templates = adminNotificationService.getAllTemplates(pageable);
            System.out.println("Templates found: " + templates.getTotalElements());
            System.out.println("Templates content: " + templates.getContent());
            return ResponseEntity.ok(templates);
        } catch (Exception e) {
            System.err.println("Error in getAllTemplates: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @PostMapping("/templates")
    @ResponseBody
    public ResponseEntity<NotificationTemplateDto> createTemplate(@Valid @RequestBody NotificationTemplateDto templateDto, HttpSession session) {
        System.out.println("=== createTemplate called ===");
        System.out.println("TemplateDto: " + templateDto);
        
        AdmVO currentAdmin = (AdmVO) session.getAttribute("loggedInAdm");
        System.out.println("Current admin from session: " + currentAdmin);
        
        if (currentAdmin == null) {
            System.out.println("No admin in session, setting default createdById to 1");
            templateDto.setCreatedById(1); // 暫時使用預設值
        } else {
            templateDto.setCreatedById(currentAdmin.getAdmId());
        }
        
        try {
            NotificationTemplateDto createdTemplate = adminNotificationService.createTemplate(templateDto);
            System.out.println("Template created successfully: " + createdTemplate);
            return new ResponseEntity<>(createdTemplate, HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println("Error creating template: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @PutMapping("/templates/{id}")
    @ResponseBody
    public ResponseEntity<NotificationTemplateDto> updateTemplate(@PathVariable Integer id, @Valid @RequestBody NotificationTemplateDto templateDto) {
        System.out.println("=== updateTemplate called ===");
        System.out.println("ID: " + id);
        System.out.println("TemplateDto: " + templateDto);
        
        try {
            NotificationTemplateDto updatedTemplate = adminNotificationService.updateTemplate(id, templateDto);
            System.out.println("Template updated successfully: " + updatedTemplate);
            return ResponseEntity.ok(updatedTemplate);
        } catch (Exception e) {
            System.err.println("Error updating template: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @DeleteMapping("/templates/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteTemplate(@PathVariable Integer id) {
        adminNotificationService.deleteTemplate(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/report")
    public ResponseEntity<NotificationStatsDto> getReport(@PathVariable("id") Integer notificationId) {
        NotificationStatsDto stats = notificationReportService.getNotificationStats(notificationId);
        return ResponseEntity.ok(stats);
    }
} 