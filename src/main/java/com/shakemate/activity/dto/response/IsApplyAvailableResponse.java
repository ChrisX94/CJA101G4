package com.shakemate.activity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IsApplyAvailableResponse {

    private Integer applyStatusCode;
    private String applyStatusLabel;

}
