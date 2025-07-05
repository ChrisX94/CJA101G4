package com.shakemate.activity.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.sql.Timestamp;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActivityTrackingDTO {

    private Integer activityId;
    private Integer userId;
    private Timestamp trackingTime;
    private Byte trackingState; // 0: 正在追蹤, 1: 取消追蹤

}
