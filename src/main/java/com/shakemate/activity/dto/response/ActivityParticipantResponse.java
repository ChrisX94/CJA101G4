package com.shakemate.activity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityParticipantResponse {

    private Integer pId;
    private Integer uId;
    private Integer pStatusCode;
    private String pStatusLabel;


}
