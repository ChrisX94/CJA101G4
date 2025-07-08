package com.shakemate.notification.service;

import com.shakemate.notification.dto.NotificationPreferenceDto;

import java.util.List;
import java.util.Optional;

public interface NotificationPreferenceService {

    /**
     * 根據使用者ID獲取通知偏好設定。
     *
     * @param userId 使用者ID
     * @return 返回包含通知偏好設定的Optional，如果找不到則為空
     */
    Optional<NotificationPreferenceDto> getPreferences(Integer userId);

    /**
     * 根據使用者ID獲取所有通知偏好設定。
     *
     * @param userId 使用者ID
     * @return 返回所有通知偏好設定的列表
     */
    List<NotificationPreferenceDto> getAllPreferences(Integer userId);

    /**
     * 為使用者建立預設的通知偏好設定。
     *
     * @param userId 使用者ID
     * @return 返回建立的預設通知偏好設定列表
     */
    List<NotificationPreferenceDto> createDefaultPreferences(Integer userId);

    /**
     * 更新或創建使用者的通知偏好設定。
     *
     * @param userId 使用者ID
     * @param preferenceDto 包含更新資訊的DTO
     */
    void updatePreferences(Integer userId, NotificationPreferenceDto preferenceDto);

    /**
     * 更新或創建使用者的通知偏好設定，並回傳最新結果。
     *
     * @param userId 使用者ID
     * @param preferenceDto 包含更新資訊的DTO
     * @return 更新後的DTO
     */
    NotificationPreferenceDto updatePreferencesWithResult(Integer userId, NotificationPreferenceDto preferenceDto);
} 