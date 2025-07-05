package com.shakemate.activity.dto;

import lombok.Data;

@Data
public class ActivityQuestionDTO {

    private Integer questionId;
    private Integer activityId;
    private String questionText;
    private Byte questionOrder;

}
