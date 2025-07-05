package com.shakemate.activity.dto.response;

import com.shakemate.activity.dto.ActivityAnswerDTO;
import com.shakemate.activity.dto.ActivityQuestionDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicantsQAResponse {

    private List<ActivityQuestionDTO> questions;
    private List<ActivityAnswerDTO> answers;

}
