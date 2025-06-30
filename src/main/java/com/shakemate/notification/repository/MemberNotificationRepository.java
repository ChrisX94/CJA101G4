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

@Repository
public interface MemberNotificationRepository extends JpaRepository<MemberNotification, Integer> {
    /**
     * 分頁後的通知列表
     * @return 分頁後的通知列表
     */
    Page<MemberNotification> findByUser_UserIdOrderByNotification_CreatedTimeDesc(Integer userId, Pageable pageable);

    /**
     * 分頁後的未讀通知列表
     * @return 分頁後的未讀通知列表
     */
    Page<MemberNotification> findByUser_UserIdAndIsReadFalseOrderByNotification_CreatedTimeDesc(Integer userId, Pageable pageable);

    /**
     * 將指定使用者的所有未讀通知標記為已讀
     * @param userId 使用者 ID
     */
    @Modifying
    @Query("UPDATE MemberNotification mn SET mn.isRead = true, mn.readTime = CURRENT_TIMESTAMP WHERE mn.user.userId = :userId AND mn.notification.notificationId = :notificationId AND mn.isRead = false")
    void markAsRead(@Param("userId") Integer userId, @Param("notificationId") Integer notificationId);

    // 查詢特定使用者的特定通知
    Optional<MemberNotification> findByUser_UserIdAndNotification_NotificationId(Integer userId, Integer notificationId);

    // 將特定使用者的所有未讀通知標記為已讀
    @Modifying
    @Query("UPDATE MemberNotification mn SET mn.isRead = true, mn.readTime = CURRENT_TIMESTAMP WHERE mn.user.userId = :userId AND mn.isRead = false")
    void markAllAsReadByUserId(@Param("userId") Integer userId);

    // Add this method to count unread notifications for a user
    long countByUser_UserIdAndIsReadFalse(Integer userId);

    // 刪除指定通知ID的所有會員通知記錄
    @Modifying
    @Transactional
    @Query("DELETE FROM MemberNotification mn WHERE mn.notification.notificationId = :notificationId")
    void deleteByNotification_NotificationId(@Param("notificationId") Integer notificationId);

    // --- Methods for Statistics ---

    long countByNotification_NotificationId(Integer notificationId);

    long countByNotification_NotificationIdAndDeliveryStatus(Integer notificationId, com.shakemate.notification.enums.DeliveryStatus deliveryStatus);

    long countByNotification_NotificationIdAndIsReadTrue(Integer notificationId);

    long countByNotification_NotificationIdAndUserInteraction(Integer notificationId, Integer userInteraction);
} 