package com.shakemate.notification.repository;

import com.shakemate.notification.entity.MemberNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import com.shakemate.notification.entity.MemberNotificationId;

// @Repository
public interface MemberNotificationRepository extends JpaRepository<MemberNotification, MemberNotificationId> {
    /**
     * 分頁後的通知列表
     * @return 分頁後的通知列表
     */
    Page<MemberNotification> findByUserOrderByNotificationDesc(Integer user, Pageable pageable);

    /**
     * 分頁後的未讀通知列表
     * @return 分頁後的未讀通知列表
     */
    Page<MemberNotification> findByUserAndIsReadFalseOrderByNotificationDesc(Integer user, Pageable pageable);

    /**
     * 將指定使用者的所有未讀通知標記為已讀
     * @param userId 使用者 ID
     */
    @Modifying
    @Query("UPDATE MemberNotification mn SET mn.isRead = true, mn.readTime = CURRENT_TIMESTAMP WHERE mn.user = :userId AND mn.notification = :notificationId AND mn.isRead = false")
    void markAsRead(@Param("userId") Integer userId, @Param("notificationId") Integer notificationId);

    // 查詢特定使用者的特定通知
    Optional<MemberNotification> findByUserAndNotification(Integer user, Integer notification);

    // 將特定使用者的所有未讀通知標記為已讀
    @Modifying
    @Query("UPDATE MemberNotification mn SET mn.isRead = true, mn.readTime = CURRENT_TIMESTAMP WHERE mn.user = :userId AND mn.isRead = false")
    void markAllAsReadByUserId(@Param("userId") Integer userId);

    // Add this method to count unread notifications for a user
    long countByUserAndIsReadFalse(Integer user);

    // 查詢用戶的所有通知記錄
    List<MemberNotification> findByUser(Integer user);

    // 統計用戶的已讀/未讀通知數量
    long countByUserAndIsRead(Integer user, Boolean isRead);

    // 刪除指定通知ID的所有會員通知記錄
    @Modifying
    @Transactional
    @Query("DELETE FROM MemberNotification mn WHERE mn.notification = :notificationId")
    void deleteByNotification(@Param("notificationId") Integer notificationId);

    // --- Methods for Statistics ---

    long countByNotification(Integer notificationId);

    long countByNotificationAndDeliveryStatus(Integer notificationId, com.shakemate.notification.enums.DeliveryStatus deliveryStatus);

    long countByNotificationAndIsReadTrue(Integer notificationId);

    long countByNotificationAndUserInteraction(Integer notificationId, Integer userInteraction);

    // 按天统计指定通知的发送量（返回日期字符串和数量）
    @Query("SELECT DATE_FORMAT(mn.sentTime, '%Y-%m-%d') as day, COUNT(mn) FROM MemberNotification mn WHERE mn.notification = :notificationId GROUP BY day ORDER BY day")
    List<Object[]> findSendTrendByNotification(@Param("notificationId") Integer notificationId);
} 