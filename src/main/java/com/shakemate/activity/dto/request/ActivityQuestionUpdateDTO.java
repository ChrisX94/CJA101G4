package com.shakemate.activity.dto.request;

import lombok.Data;

@Data
public class ActivityQuestionUpdateDTO {

    private String questionText;
    private Byte questionOrder;

}
