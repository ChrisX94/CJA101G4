package com.shakemate.activity.dto.response;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class ActivityCardDTO {

    private Integer activityId;
    private String title;
    private String imageUrl;
    private String location;
    private Byte activityStatus;
    private Timestamp createdTime;
    private Timestamp activStartTime;
    private Timestamp activEndTime;
    private Integer signupCount;
    private Integer maxPeople;
    private Integer remainingQuota;    // 剩餘名額


    private Byte participantStatus;      // 自己是團員時才有
    private String joinStatus; // "JOINABLE", "PENDING", "APPROVED", etc.


}
