package com.shakemate.activity.dto.request;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ActivityReportUpdateDTO {

    private Byte rpReason; // 0~4 對應檢舉原因
    private String rpContent;
    private String rpPic;
    private Timestamp rpTime;
    private Integer admId;
    private Timestamp rpDoneTime;
    private Byte rpStatus; // 0: 未處理, 1: 通過, 2: 不通過
    private String rpNote;

}
