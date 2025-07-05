package com.shakemate.activity.dto.response;

import com.shakemate.activity.dto.ActivityQuestionDTO;
import com.shakemate.activity.dto.request.ActivityCreateRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityCreateResponse {

    private Integer activityId;

    private ActivityCreateRequest createRequest;

    private List<ActivityQuestionDTO> questionList = Collections.emptyList();

}
