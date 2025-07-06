package com.shakemate.activity.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ActivityQuestionCreateDTO {

    @NotNull(message = "activityId不能為空")
    private Integer activityId;

    @NotBlank(message = "questionText不能為空")
    @Size(max = 255)
    private String questionText;

    @Min(1)
    @Max(5)
    @NotNull(message = "問題排序號碼不可為空")
    private Byte questionOrder;

}
