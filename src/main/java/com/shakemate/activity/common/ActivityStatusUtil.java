package com.shakemate.activity.common;

import com.shakemate.activity.entity.Activity;

import java.time.LocalDateTime;

public class ActivityStatusUtil {

    public static byte calculateStatus(Activity activity) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = activity.getActivStartTime().toLocalDateTime();
        LocalDateTime endTime = activity.getActivEndTime().toLocalDateTime();

        if (activity.getActivityStatus() == 3) {
            return 3; // 已取消或下架
        } else if (now.isBefore(startTime)) {
            return 0; // 尚未開始
        } else if (now.isAfter(endTime)) {
            return 2; // 已結束
        } else {
            return 1; // 進行中
        }
    }

}
