package com.shakemate.activity.dto.response;

import com.shakemate.activity.dto.ActivityQuestionDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityApplicantResponseWithQuestions {

    private List<ActivityApplicantResponse> responsesList;
    private List<ActivityQuestionDTO> questionList;


}
