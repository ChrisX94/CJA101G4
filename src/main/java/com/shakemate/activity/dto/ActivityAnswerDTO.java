package com.shakemate.activity.dto;

import lombok.Data;

@Data
public class ActivityAnswerDTO {

    private Integer answerId;
    private Integer activityId;
    private Integer questionId;
    private Integer userId;
    private String answerText;

}
