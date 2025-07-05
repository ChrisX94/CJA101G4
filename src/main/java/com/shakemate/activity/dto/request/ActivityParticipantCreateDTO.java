package com.shakemate.activity.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class ActivityParticipantCreateDTO {

    @NotNull(message = "參與者用戶編號不可為空")
    private Integer participantId;

    @NotNull(message = "活動編號不可為空")
    private Integer activityId;

    private Timestamp admReviewTime;

    @NotNull(message = "參與者狀態不可為空")
    private Byte parStatus;

    @NotNull(message = "申請時間不可為空")
    private Timestamp applyingDate;

    private Byte rating = 5;

    private String reviewContent;

    private Timestamp reviewTime;


}
