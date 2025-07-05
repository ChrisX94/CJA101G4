package com.shakemate.activity.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityQuestionCreateRequest {

    @NotBlank(message = "questionText不能為空")
    @Size(max = 255)
    private String questionText;

    @Min(1)
    @Max(5)
    @NotNull(message = "問題排序號碼不可為空")
    private Byte questionOrder;

}
