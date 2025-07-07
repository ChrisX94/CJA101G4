package com.shakemate.activity.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Data
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActivityParticipantDTO {
    private Integer participantId;
    private Integer activityId;
    private Timestamp admReviewTime;
    private Byte parStatus;
    private Timestamp applyingDate;
    private Byte rating;
    private String reviewContent;
    private Timestamp reviewTime;
}
