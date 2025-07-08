package com.shakemate.notification.service;

import com.shakemate.notification.dto.NotificationPreferenceDto;
import com.shakemate.notification.entity.NotificationPreference;
import com.shakemate.notification.repository.NotificationPreferenceRepository;
import com.shakemate.user.dao.UsersRepository;
import com.shakemate.user.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.InstanceNotFoundException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
@Transactional
public class NotificationPreferenceServiceImpl implements NotificationPreferenceService {

    @Autowired
    private NotificationPreferenceRepository preferenceRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<NotificationPreferenceDto> getPreferences(Integer userId) {
        return preferenceRepository.findByUser_UserId(userId).stream()
                .findFirst()
                .map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationPreferenceDto> getAllPreferences(Integer userId) {
        return preferenceRepository.findByUser_UserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationPreferenceDto> createDefaultPreferences(Integer userId) {
        // 檢查用戶是否存在
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new SecurityException("找不到對應的用戶: " + userId));

        // 檢查是否已有通知偏好設定
        List<NotificationPreference> existingPreferences = preferenceRepository.findByUser_UserId(userId);
        if (!existingPreferences.isEmpty()) {
            // 如果已有設定，直接返回現有的
            return existingPreferences.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        }

        // 建立預設的通知類別設定
        String[] defaultCategories = {
            "系統通知", "訂單狀態", "促銷活動", "安全提醒", "更新公告"
        };

        List<NotificationPreference> defaultPreferences = new ArrayList<>();
        List<NotificationPreferenceDto> resultDtos = new ArrayList<>();

        for (String category : defaultCategories) {
            NotificationPreference preference = new NotificationPreference();
            preference.setUser(user);
            preference.setNotificationCategory(category);
            preference.setEmailEnabled(true);
            preference.setSmsEnabled(false);
            preference.setPushEnabled(true);
            preference.setInAppEnabled(true);
            preference.setQuietHoursEnabled(true);
            preference.setQuietHoursStart(LocalTime.of(22, 0)); // 晚上10點
            preference.setQuietHoursEnd(LocalTime.of(8, 0));    // 早上8點

            NotificationPreference savedPreference = preferenceRepository.save(preference);
            defaultPreferences.add(savedPreference);
            resultDtos.add(convertToDto(savedPreference));
        }

        return resultDtos;
    }

    @Override
    public void updatePreferences(Integer userId, NotificationPreferenceDto dto) {
        System.out.println("[Service] 開始更新通知偏好設定: preferenceId=" + dto.getPreferenceId() + ", userId=" + userId);
        NotificationPreference entity = preferenceRepository.findById(dto.getPreferenceId())
                .orElseThrow(() -> new SecurityException("找不到對應的偏好設定: " + dto.getPreferenceId()));

        if (!Objects.equals(entity.getUser().getUserId(), userId)) {
            throw new SecurityException("無權修改偏好設定: " + dto.getPreferenceId());
        }

        updateEntityFromDto(entity, dto);
        preferenceRepository.save(entity);
        preferenceRepository.flush(); // 強制寫入
        System.out.println("[Service] 儲存並flush完成: preferenceId=" + dto.getPreferenceId());
    }

    public NotificationPreferenceDto updatePreferencesWithResult(Integer userId, NotificationPreferenceDto dto) {
        System.out.println("[Service] 開始更新通知偏好設定: preferenceId=" + dto.getPreferenceId() + ", userId=" + userId);
        NotificationPreference entity = preferenceRepository.findById(dto.getPreferenceId())
                .orElseThrow(() -> new SecurityException("找不到對應的偏好設定: " + dto.getPreferenceId()));
        if (!Objects.equals(entity.getUser().getUserId(), userId)) {
            throw new SecurityException("無權修改偏好設定: " + dto.getPreferenceId());
        }
        updateEntityFromDto(entity, dto);
        preferenceRepository.save(entity);
        preferenceRepository.flush();
        System.out.println("[Service] 儲存並flush完成: preferenceId=" + dto.getPreferenceId());
        // 立即查詢驗證
        NotificationPreference updated = preferenceRepository.findById(dto.getPreferenceId())
                .orElseThrow(() -> new SecurityException("儲存後查無資料: " + dto.getPreferenceId()));
        // 比對關鍵欄位
        if (!Objects.equals(updated.getEmailEnabled(), dto.getEmailEnabled()) ||
            !Objects.equals(updated.getSmsEnabled(), dto.getSmsEnabled()) ||
            !Objects.equals(updated.getPushEnabled(), dto.getPushEnabled()) ||
            !Objects.equals(updated.getInAppEnabled(), dto.getInAppEnabled()) ||
            !Objects.equals(updated.getQuietHoursEnabled(), dto.getQuietHoursEnabled()) ||
            !Objects.equals(updated.getQuietHoursStart(), dto.getQuietHoursStart()) ||
            !Objects.equals(updated.getQuietHoursEnd(), dto.getQuietHoursEnd())) {
            throw new RuntimeException("資料庫寫入後資料不一致，請聯絡管理員");
        }
        System.out.println("[Service] 資料驗證通過: preferenceId=" + dto.getPreferenceId());
        return convertToDto(updated);
    }

    private NotificationPreferenceDto convertToDto(NotificationPreference entity) {
        NotificationPreferenceDto dto = new NotificationPreferenceDto();
        dto.setPreferenceId(entity.getPreferenceId());
        dto.setUserId(entity.getUser().getUserId());
        dto.setNotificationCategory(entity.getNotificationCategory());
        dto.setEmailEnabled(entity.getEmailEnabled());
        dto.setSmsEnabled(entity.getSmsEnabled());
        dto.setPushEnabled(entity.getPushEnabled());
        dto.setInAppEnabled(entity.getInAppEnabled());
        dto.setQuietHoursEnabled(entity.getQuietHoursEnabled());
        dto.setQuietHoursStart(entity.getQuietHoursStart());
        dto.setQuietHoursEnd(entity.getQuietHoursEnd());
        return dto;
    }

    private void updateEntityFromDto(NotificationPreference entity, NotificationPreferenceDto dto) {
        entity.setEmailEnabled(dto.getEmailEnabled());
        entity.setSmsEnabled(dto.getSmsEnabled());
        entity.setPushEnabled(dto.getPushEnabled());
        entity.setInAppEnabled(dto.getInAppEnabled());
        entity.setQuietHoursEnabled(dto.getQuietHoursEnabled());
        entity.setQuietHoursStart(dto.getQuietHoursStart());
        entity.setQuietHoursEnd(dto.getQuietHoursEnd());
    }
} 