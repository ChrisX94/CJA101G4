package com.shakemate.notification.aspect;

import com.shakemate.notification.service.NotificationService;
import com.shakemate.user.model.Users;
import com.shakemate.user.dao.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 配對通知AOP切面
 * 
 * 透過AOP機制攔截配對相關的業務操作，自動發送對應的通知。
 * 支援的通知場景：
 * - 收到新的按讚（NEW_MATCH_REQUEST）
 * - 配對成功（NEW_MATCH_SUCCESS）
 * - 聊天室建立（CHATROOM_CREATED）
 * 
 * @author ShakeMate團隊
 * @version 1.0
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class MatchNotificationAspect {

    private final NotificationService notificationService;
    private final UsersRepository usersRepository;

    /**
     * 定義切點：攔截配對服務的like操作
     */
    @Pointcut("execution(* com.shakemate.match.service.MatchService.insertLike(..))")
    public void likeOperationPointcut() {
        // 切點定義，無需實現
    }

    /**
     * 定義切點：攔截配對服務的配對記錄插入操作
     */
    @Pointcut("execution(* com.shakemate.match.service.MatchService.insertMatchRecord(..))")
    public void matchRecordPointcut() {
        // 切點定義，無需實現
    }

    /**
     * 定義切點：攔截聊天室創建操作
     */
    @Pointcut("execution(* com.shakemate.match.service.MatchService.createChatRoom(..))")
    public void chatRoomCreationPointcut() {
        // 切點定義，無需實現
    }

    /**
     * 處理按讚操作後的通知
     * 
     * 當用戶對目標用戶按讚時，向目標用戶發送按讚通知
     * 
     * @param userId 操作用戶ID
     * @param targetId 目標用戶ID
     */
    @AfterReturning("likeOperationPointcut() && args(userId, targetId)")
    public void handleLikeOperation(int userId, int targetId) {
        try {
            log.info("AOP攔截到按讚操作，用戶 {} 對用戶 {} 按讚", userId, targetId);

            // 查詢真實的用戶數據
            Users fromUser = usersRepository.findById(userId).orElse(null);
            
            if (fromUser == null) {
                log.warn("無法找到用戶數據: userId={}", userId);
                return;
            }

            // 準備通知模板變數 - 使用真實數據
            Map<String, Object> templateVariables = new HashMap<>();
            templateVariables.put("from_user_id", userId);
            templateVariables.put("from_user_name", fromUser.getUsername());
            templateVariables.put("from_user_age", calculateAge(fromUser.getBirthday()));
            templateVariables.put("from_user_location", fromUser.getLocation());

            // 發送按讚通知給目標用戶
            notificationService.sendTemplateNotification(
                "NEW_MATCH_REQUEST",
                targetId,
                templateVariables
            ).exceptionally(throwable -> {
                log.error("發送按讚通知失敗，操作用戶: {}, 目標用戶: {}", userId, targetId, throwable);
                return false;
            });

            log.info("按讚通知已發送，操作用戶: {}, 目標用戶: {}", userId, targetId);

        } catch (Exception e) {
            log.error("處理按讚通知時發生錯誤，操作用戶: {}, 目標用戶: {}", userId, targetId, e);
        }
    }

    /**
     * 處理配對成功後的通知
     * 
     * 當兩人互相按讚形成配對時，向雙方發送配對成功通知
     * 
     * @param result 配對記錄ID
     * @param userId 操作用戶ID
     * @param targetId 目標用戶ID
     */
    @AfterReturning(pointcut = "matchRecordPointcut() && args(userId, targetId)", returning = "result")
    public void handleMatchSuccess(Object result, int userId, int targetId) {
        try {
            if (result instanceof Integer) {
                int matchId = (Integer) result;
                
                log.info("AOP攔截到配對成功，用戶 {} 和用戶 {} 配對成功，配對ID: {}", 
                        userId, targetId, matchId);

                // 查詢真實的用戶數據
                Users user = usersRepository.findById(userId).orElse(null);
                Users targetUser = usersRepository.findById(targetId).orElse(null);
                
                if (user == null || targetUser == null) {
                    log.warn("無法找到用戶數據: userId={}, targetId={}", userId, targetId);
                    return;
                }

                // 準備通知模板變數給操作用戶 - 使用真實數據
                Map<String, Object> templateVariablesForUser = new HashMap<>();
                templateVariablesForUser.put("user_name", user.getUsername());
                templateVariablesForUser.put("matched_user_name", targetUser.getUsername());
                templateVariablesForUser.put("match_id", matchId);
                templateVariablesForUser.put("user_age", calculateAge(user.getBirthday()));
                templateVariablesForUser.put("matched_user_age", calculateAge(targetUser.getBirthday()));
                templateVariablesForUser.put("user_location", user.getLocation());
                templateVariablesForUser.put("matched_user_location", targetUser.getLocation());

                // 準備通知模板變數給目標用戶 - 使用真實數據
                Map<String, Object> templateVariablesForTarget = new HashMap<>();
                templateVariablesForTarget.put("user_name", targetUser.getUsername());
                templateVariablesForTarget.put("matched_user_name", user.getUsername());
                templateVariablesForTarget.put("match_id", matchId);
                templateVariablesForTarget.put("user_age", calculateAge(targetUser.getBirthday()));
                templateVariablesForTarget.put("matched_user_age", calculateAge(user.getBirthday()));
                templateVariablesForTarget.put("user_location", targetUser.getLocation());
                templateVariablesForTarget.put("matched_user_location", user.getLocation());

                // 發送配對成功通知給雙方
                notificationService.sendTemplateNotification(
                    "NEW_MATCH_SUCCESS",
                    userId,
                    templateVariablesForUser
                ).exceptionally(throwable -> {
                    log.error("發送配對成功通知失敗，用戶: {}", userId, throwable);
                    return false;
                });

                notificationService.sendTemplateNotification(
                    "NEW_MATCH_SUCCESS",
                    targetId,
                    templateVariablesForTarget
                ).exceptionally(throwable -> {
                    log.error("發送配對成功通知失敗，用戶: {}", targetId, throwable);
                    return false;
                });

                log.info("配對成功通知已發送給雙方，配對ID: {}", matchId);
            }

        } catch (Exception e) {
            log.error("處理配對成功通知時發生錯誤，用戶: {}, 目標用戶: {}", userId, targetId, e);
        }
    }

    /**
     * 處理聊天室創建後的通知
     * 
     * 當配對成功後自動創建聊天室時，向雙方發送聊天室建立通知
     * 
     * @param result 聊天室ID
     * @param userId 用戶ID
     * @param targetId 目標用戶ID
     * @param matchId 配對ID
     */
    @AfterReturning(pointcut = "chatRoomCreationPointcut() && args(userId, targetId, matchId)", returning = "result")
    public void handleChatRoomCreated(Object result, int userId, int targetId, int matchId) {
        try {
            if (result instanceof Integer) {
                int roomId = (Integer) result;
                
                log.info("AOP攔截到聊天室創建，用戶 {} 和用戶 {} 的聊天室已創建，房間ID: {}, 配對ID: {}", 
                        userId, targetId, roomId, matchId);

                // 查詢真實的用戶數據
                Users user = usersRepository.findById(userId).orElse(null);
                Users targetUser = usersRepository.findById(targetId).orElse(null);
                
                if (user == null || targetUser == null) {
                    log.warn("無法找到用戶數據: userId={}, targetId={}", userId, targetId);
                    return;
                }

                // 準備通知模板變數給操作用戶 - 使用真實數據
                Map<String, Object> templateVariablesForUser = new HashMap<>();
                templateVariablesForUser.put("matched_user_name", targetUser.getUsername());
                templateVariablesForUser.put("room_id", roomId);
                templateVariablesForUser.put("match_id", matchId);
                templateVariablesForUser.put("user_name", user.getUsername());

                // 準備通知模板變數給目標用戶 - 使用真實數據
                Map<String, Object> templateVariablesForTarget = new HashMap<>();
                templateVariablesForTarget.put("matched_user_name", user.getUsername());
                templateVariablesForTarget.put("room_id", roomId);
                templateVariablesForTarget.put("match_id", matchId);
                templateVariablesForTarget.put("user_name", targetUser.getUsername());

                // 發送聊天室建立通知給雙方
                notificationService.sendTemplateNotification(
                    "CHATROOM_CREATED",
                    userId,
                    templateVariablesForUser
                ).exceptionally(throwable -> {
                    log.error("發送聊天室建立通知失敗，用戶: {}", userId, throwable);
                    return false;
                });

                notificationService.sendTemplateNotification(
                    "CHATROOM_CREATED",
                    targetId,
                    templateVariablesForTarget
                ).exceptionally(throwable -> {
                    log.error("發送聊天室建立通知失敗，用戶: {}", targetId, throwable);
                    return false;
                });

                log.info("聊天室建立通知已發送給雙方，房間ID: {}", roomId);
            }

        } catch (Exception e) {
            log.error("處理聊天室建立通知時發生錯誤，用戶: {}, 目標用戶: {}, 配對ID: {}", 
                    userId, targetId, matchId, e);
        }
    }

    /**
     * 計算年齡的輔助方法
     */
    private int calculateAge(Date birthday) {
        if (birthday == null) return 0;
        
        LocalDate birthDate = birthday.toLocalDate();
        LocalDate currentDate = LocalDate.now();
        return Period.between(birthDate, currentDate).getYears();
    }
} 