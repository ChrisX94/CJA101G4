package com.shakemate.activity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityCalendarResponse {

    private Integer activityId;
    private String title;
    private Timestamp activStartTime;
    private Timestamp activEndTime;
    private Byte participantStatusCode;
    private String participantStatusLabel; // 1. 團主 2. 團員

}
