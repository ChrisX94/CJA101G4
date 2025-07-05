package com.shakemate.activity.dto.response;

import com.shakemate.activity.dto.ActivityAnswerDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityApplicantResponse {

    private Integer userId;
    private String userName;
    private String userAccName;
    private String userImgUrl;
    private String userIntro;

    private Integer activityId;
    private Byte parStatus;
    private Timestamp applyingDate;

    private List<ActivityAnswerDTO> answers;



}
