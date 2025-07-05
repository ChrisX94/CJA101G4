package com.shakemate.activity.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class ActivityReportCreateDTO {

    @NotNull(message = "用戶編號不可為空")
    private Integer userId;

    @NotNull(message = "活動編號不可為空")
    private Integer activityId;

    @NotNull(message = "檢舉原因編號不可為空")
    private Byte rpReason; // 0~4 對應檢舉原因

    @NotBlank(message = "檢舉文字不可為空")
    private String rpContent;

    private String rpPic;

    @NotNull(message = "檢舉時間不可為空")
    private Timestamp rpTime;

    @NotNull(message = "管理員編號不可為空")
    private Integer admId;

    private Timestamp rpDoneTime;

    @NotNull(message = "處理狀態不可為空")
    private Byte rpStatus = 0; // 0: 未處理, 1: 通過, 2: 不通過

    private String rpNote;

}
