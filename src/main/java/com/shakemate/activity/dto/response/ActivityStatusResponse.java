package com.shakemate.activity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityStatusResponse {

    private Integer statusCode;
    private String statusLabel;

}
