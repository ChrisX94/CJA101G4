package com.shakemate.activity.service;

import com.shakemate.activity.dto.ActivityQuestionDTO;
import com.shakemate.activity.dto.request.ActivityQuestionCreateDTO;
import com.shakemate.activity.dto.request.ActivityQuestionUpdateDTO;

import java.util.List;

public interface ActivityQuestionService {

    ActivityQuestionDTO getById(Integer id);
    List<ActivityQuestionDTO> getAll();
    ActivityQuestionDTO create(ActivityQuestionCreateDTO createDTO);
    ActivityQuestionDTO update(Integer id, ActivityQuestionUpdateDTO updateDTO);
    void delete(Integer id);

    // --
    List<ActivityQuestionDTO> createActivityQuestionnaire(List<ActivityQuestionCreateDTO> createDTOs);
    List<ActivityQuestionDTO> getActivityQuestionnaire(Integer activityId);
    Boolean isQuestionExist(Integer activityId);



}
