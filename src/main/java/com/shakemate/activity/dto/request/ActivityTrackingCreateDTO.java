package com.shakemate.activity.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class ActivityTrackingCreateDTO {

    @NotNull(message = "活動編號不可為空")
    private Integer activityId;
    @NotNull(message = "用戶編號不可為空")
    private Integer userId;

    private Timestamp trackingTime;

    private Byte trackingState = 0; // 0: 正在追蹤, 1: 取消追蹤

}
