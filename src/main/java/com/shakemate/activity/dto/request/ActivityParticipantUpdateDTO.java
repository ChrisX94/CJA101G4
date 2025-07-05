package com.shakemate.activity.dto.request;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ActivityParticipantUpdateDTO {

    private Timestamp admReviewTime;
    private Byte parStatus;
    private Timestamp applyingDate;
    private Byte rating;
    private String reviewContent;
    private Timestamp reviewTime;
}
