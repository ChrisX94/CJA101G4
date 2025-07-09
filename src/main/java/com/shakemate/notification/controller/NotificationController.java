package com.shakemate.notification.controller;

import com.shakemate.notification.dto.NotificationPreferenceDto;
import com.shakemate.notification.dto.MemberNotificationDto;
import com.shakemate.notification.service.NotificationPreferenceService;
import com.shakemate.notification.service.NotificationService;
import com.shakemate.user.model.Users;
import com.shakemate.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;

/**
 * 通知控制器 - 處理用戶端通知相關請求
 */
@Controller
@RequestMapping("/notifications")
@Slf4j
public class NotificationController {

    @Autowired
    private NotificationPreferenceService preferenceService;

    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private UserService userService;

    // 簡單測試端點
    @GetMapping("/test")
    @ResponseBody
    public String testEndpoint(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("account");
        return "通知系統測試 - 用戶ID: " + (userId != null ? userId : "未登入");
    }

    // 會員通知列表頁面
    @GetMapping
    public String getNotificationsPage(HttpSession session, Model model) {
        System.out.println("=== DEBUG: 會員通知列表頁面被訪問 ===");
        
        Integer userId = (Integer) session.getAttribute("account");
        System.out.println("DEBUG: 當前用戶ID: " + userId);
        
        if (userId == null) {
            System.out.println("DEBUG: 用戶未登入，重導向到登入頁面");
            return "redirect:/login";
        }
        
        // 由於LoginFilter已經驗證過登入狀態，這裡直接載入頁面
        model.addAttribute("pageTitle", "通知列表");
        return "front-end/user/notifications";
    }

    // 會員通知列表API
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<Page<MemberNotificationDto>> getUserNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpSession session) {
        System.out.println("=== DEBUG: getUserNotifications API 被呼叫 ===");
        
        Integer userId = (Integer) session.getAttribute("account");
        if (userId == null) {
            System.out.println("DEBUG: 用戶未登入，返回 401");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<MemberNotificationDto> notifications = notificationService.getMemberNotifications(userId, pageable);
            System.out.println("DEBUG: 找到 " + notifications.getTotalElements() + " 個通知");
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            System.err.println("DEBUG: 獲取通知列表時發生錯誤: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 標記通知為已讀
    @PostMapping("/api/{id}/mark-read")
    @ResponseBody
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable Long id, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("account");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            notificationService.markAsRead(userId, id.intValue());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            // 查不到MemberNotification時返回404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/api/preferences")
    @ResponseBody
    public ResponseEntity<List<NotificationPreferenceDto>> getPreferences(HttpSession session) {
        System.out.println("=== DEBUG: getPreferences API 被呼叫 ===");
        
        Integer userId = (Integer) session.getAttribute("account");
        System.out.println("DEBUG: session 中的用戶ID: " + userId);
        
        if (userId == null) {
            System.out.println("DEBUG: 用戶未登入，返回 401");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        System.out.println("DEBUG: 用戶 ID: " + userId);
        
        try {
            // 獲取用戶的所有通知偏好設定
            List<NotificationPreferenceDto> preferences = preferenceService.getAllPreferences(userId);
            System.out.println("DEBUG: 找到 " + preferences.size() + " 個通知偏好設定");
            return ResponseEntity.ok(preferences);
        } catch (Exception e) {
            System.err.println("DEBUG: 獲取通知偏好設定時發生錯誤: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/api/preferences/create-defaults")
    @ResponseBody
    public ResponseEntity<List<NotificationPreferenceDto>> createDefaultPreferences(HttpSession session) {
        System.out.println("=== DEBUG: createDefaultPreferences API 被呼叫 ===");
        
        Integer userId = (Integer) session.getAttribute("account");
        if (userId == null) {
            System.out.println("DEBUG: 用戶未登入，返回 401");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            // 建立預設通知偏好設定
            List<NotificationPreferenceDto> preferences = preferenceService.createDefaultPreferences(userId);
            System.out.println("DEBUG: 建立了 " + preferences.size() + " 個預設通知偏好設定");
            return ResponseEntity.ok(preferences);
        } catch (Exception e) {
            System.err.println("DEBUG: 建立預設通知偏好設定時發生錯誤: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/api/preferences")
    @ResponseBody
    public ResponseEntity<?> updatePreferences(@RequestBody List<NotificationPreferenceDto> dtos, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("account");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("未登入，請重新登入");
        }
        try {
            List<NotificationPreferenceDto> result = new java.util.ArrayList<>();
            for (NotificationPreferenceDto dto : dtos) {
                try {
                    NotificationPreferenceDto updated = preferenceService.updatePreferencesWithResult(userId, dto);
                    System.out.println("[通知偏好設定] 儲存成功: preferenceId=" + dto.getPreferenceId());
                    result.add(updated);
                } catch (Exception e) {
                    System.err.println("[通知偏好設定] 儲存失敗: preferenceId=" + dto.getPreferenceId() + ", 錯誤: " + e.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("偏好設定儲存失敗: " + e.getMessage());
                }
            }
            return ResponseEntity.ok(result);
        } catch (Exception ex) {
            System.err.println("[通知偏好設定] 批次儲存失敗: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("批次儲存失敗: " + ex.getMessage());
        }
    }

    @GetMapping("/api/unread-count")
    @ResponseBody
    public ResponseEntity<Map<String, Long>> getUnreadCount(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("account");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(Collections.singletonMap("unreadCount", count));
    }

    @GetMapping("/api/recent")
    @ResponseBody
    public ResponseEntity<List<MemberNotificationDto>> getRecentNotifications(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("account");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            // 獲取最近的5條通知
            Pageable pageable = PageRequest.of(0, 5);
            Page<MemberNotificationDto> notifications = notificationService.getMemberNotifications(userId, pageable);
            return ResponseEntity.ok(notifications.getContent());
        } catch (Exception e) {
            System.err.println("獲取最近通知時發生錯誤: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/api/test-notification")
    @ResponseBody
    public ResponseEntity<?> sendTestNotification(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("account");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("未登入，請重新登入");
        }
        
        try {
            // 發送測試通知
            Map<String, Object> variables = new HashMap<>();
            variables.put("test_message", "這是一個測試通知，用於驗證數字更新功能");
            
            CompletableFuture<Boolean> result = notificationService.sendTemplateNotification(
                "TEST_NOTIFICATION", 
                userId, 
                variables
            );
            
            if (result.get()) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "測試通知已發送（如果您的站內通知偏好設定已啟用）。請檢查通知鈴鐺數字是否立即更新，或檢查偏好設定：/notifications/preferences"
                ));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("success", false, "message", "測試通知發送失敗")
                );
            }
        } catch (Exception e) {
            log.error("發送測試通知失敗: userId={}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                Map.of("success", false, "message", "測試通知發送失敗: " + e.getMessage())
            );
        }
    }

    /**
     * 顯示通知偏好設定頁面
     */
    @GetMapping("/preferences")
    public String showPreferences(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("account");
        System.out.println("DEBUG: 通知偏好設定 - 用戶ID: " + userId);
        
        if (userId == null) {
            return "redirect:/login";
        }
        
        // 由於LoginFilter已經驗證過登入狀態，這裡直接載入頁面
        model.addAttribute("pageTitle", "通知偏好設定");
        return "front-end/user/notification_preferences";
    }
}
