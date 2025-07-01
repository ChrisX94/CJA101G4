package com.shakemate.notification.repository;

import com.shakemate.notification.entity.NotificationPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, Integer> {

    /**
     * 根據使用者 ID 查詢其所有的通知偏好設定
     * @param userId 使用者 ID
     * @return 偏好設定列表
     */
    List<NotificationPreference> findByUser_UserId(Integer userId);

    Optional<NotificationPreference> findByUser_UserIdAndNotificationCategory(Integer userId, String category);

    List<NotificationPreference> findByUser_UserIdIn(List<Integer> userIds);
} 