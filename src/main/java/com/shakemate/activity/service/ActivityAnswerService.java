package com.shakemate.activity.service;

import com.shakemate.activity.dto.ActivityAnswerDTO;
import com.shakemate.activity.dto.request.ActivityAnswerCreateDTO;
import com.shakemate.activity.dto.request.ActivityAnswerUpdateDTO;

import java.util.List;

public interface ActivityAnswerService {

    ActivityAnswerDTO getById(Integer id);
    List<ActivityAnswerDTO> getAll();
    ActivityAnswerDTO create(ActivityAnswerCreateDTO createDTO);
    ActivityAnswerDTO update(Integer id, ActivityAnswerUpdateDTO updateDTO);
    void delete(Integer id);

    List<ActivityAnswerDTO> createSubmission(List<ActivityAnswerCreateDTO> createDTOList);
}
