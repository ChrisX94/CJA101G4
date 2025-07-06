package com.shakemate.activity.service.impl;

import com.shakemate.activity.dto.ActivityAnswerDTO;
import com.shakemate.activity.dto.request.ActivityAnswerCreateDTO;
import com.shakemate.activity.dto.request.ActivityAnswerUpdateDTO;
import com.shakemate.activity.entity.Activity;
import com.shakemate.activity.entity.ActivityAnswer;
import com.shakemate.activity.entity.ActivityQuestion;
import com.shakemate.activity.mapper.ActivityAnswerMapper;
import com.shakemate.activity.repository.ActivityAnswerRepository;
import com.shakemate.activity.repository.ActivityQuestionRepository;
import com.shakemate.activity.repository.ActivityRepository;
import com.shakemate.activity.service.ActivityAnswerService;
import com.shakemate.user.dao.UsersRepository;
import com.shakemate.user.model.Users;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ActivityAnswerServiceImpl implements ActivityAnswerService {

    private final ActivityAnswerRepository activityAnswerRepository;
    private final ActivityRepository activityRepository;
    private final UsersRepository userRepository;
    private final ActivityQuestionRepository activityQuestionRepository;
    private final ActivityAnswerMapper activityAnswerMapper;

    @Override
    public ActivityAnswerDTO getById(Integer id) {
        ActivityAnswer activityAnswer = activityAnswerRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("找不到該活動回答"));
        return activityAnswerMapper.toDto(activityAnswer);
    }

    @Override
    public List<ActivityAnswerDTO> getAll() {
        List<ActivityAnswer> list = activityAnswerRepository.findAll();
        return list.stream()
                .map(activityAnswerMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ActivityAnswerDTO create(ActivityAnswerCreateDTO createDTO) {

        // createDTO -> Entity
        ActivityAnswer activityAnswer = activityAnswerMapper.toEntity(createDTO);

        // activity
        Activity activity = activityRepository.findById(createDTO.getActivityId())
                .orElseThrow(() -> new EntityNotFoundException("找不到該活動"));
        activityAnswer.setActivity(activity);

        // user
        Users user = userRepository.findById(createDTO.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("找不到該使用者"));
        activityAnswer.setUser(user);

        // activity question
        ActivityQuestion activityQuestion = activityQuestionRepository.findById(createDTO.getQuestionId())
                .orElseThrow(() -> new EntityNotFoundException("找不到該活動問題"));
        activityAnswer.setActivityQuestion(activityQuestion);

        // save
        ActivityAnswer saved = activityAnswerRepository.save(activityAnswer);

        // Entity -> DTO
        return activityAnswerMapper.toDto(saved);






    }

    @Override
    public ActivityAnswerDTO update(Integer id, ActivityAnswerUpdateDTO updateDTO) {
        ActivityAnswer entity = activityAnswerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("查無資料"));

        if(updateDTO.getAnswerText() != null && updateDTO.getAnswerText().trim().isEmpty()) {
            // throw...
            throw new IllegalArgumentException("活動回答內容不可為空或只包含空白字元");
        }

        activityAnswerMapper.updateEntityFromDto(updateDTO, entity);
        ActivityAnswer updated = activityAnswerRepository.save(entity);
        ActivityAnswerDTO dto = activityAnswerMapper.toDto(updated);

        return dto;
    }

    @Override
    public void delete(Integer id) {
        boolean exists = activityAnswerRepository.existsById(id);
        if(!exists) {
            throw new EntityNotFoundException("查無資料");
        }

        activityAnswerRepository.deleteById(id);
    }


    // 新增答卷
    @Override
    @Transactional
    public List<ActivityAnswerDTO> createSubmission(List<ActivityAnswerCreateDTO> createDTOList) {

        List<ActivityAnswerDTO> activityAnswerDTOList = new ArrayList<>();
        for(ActivityAnswerCreateDTO createDTO : createDTOList) {
            activityAnswerDTOList.add(create(createDTO));
        }

        return activityAnswerDTOList;
    }
}
