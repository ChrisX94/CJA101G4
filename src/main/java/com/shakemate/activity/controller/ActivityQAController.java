package com.shakemate.activity.controller;

import com.shakemate.activity.common.ApiResponse;
import com.shakemate.activity.dto.ActivityAnswerDTO;
import com.shakemate.activity.dto.ActivityQuestionDTO;
import com.shakemate.activity.entity.ActivityAnswer;
import com.shakemate.activity.entity.ActivityQuestion;
import com.shakemate.activity.mapper.ActivityAnswerMapper;
import com.shakemate.activity.mapper.ActivityQuestionMapper;
import com.shakemate.activity.repository.ActivityAnswerRepository;
import com.shakemate.activity.repository.ActivityQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/activity-qa")
@RequiredArgsConstructor
public class ActivityQAController {

    private final ActivityQuestionRepository questionRepo;
    private final ActivityAnswerRepository answerRepo;
    private final ActivityQuestionMapper activityQuestionMapper;
    private final ActivityAnswerMapper activityAnswerMapper;

    // 取得某活動的所有問題
    @GetMapping("/{activityId}/questions")
    public ApiResponse<List<ActivityQuestionDTO>> getQuestions(@PathVariable Integer activityId) {
        List<ActivityQuestion> questions = questionRepo.findAllQuestionsByActivity(activityId);
        List<ActivityQuestionDTO> list = questions.stream()
                .map(activityQuestionMapper::toDto).collect(Collectors.toList());
        return ApiResponse.success(list);
    }

    // 取得某活動某使用者的所有回答
    @GetMapping("/{activityId}/answers/{userId}")
    public ApiResponse<List<ActivityAnswerDTO>> getAnswers(
            @PathVariable Integer activityId,
            @PathVariable Integer userId) {
        List<ActivityAnswer> answers = answerRepo.findAllAnswersByActivityAndUser(activityId, userId);
        List<ActivityAnswerDTO> list = answers.stream()
                .map(activityAnswerMapper::toDto).collect(Collectors.toList());

        return ApiResponse.success(list);
    }


}
