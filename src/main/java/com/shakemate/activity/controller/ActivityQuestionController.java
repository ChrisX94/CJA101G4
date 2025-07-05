package com.shakemate.activity.controller;

import com.shakemate.activity.common.ApiResponse;
import com.shakemate.activity.dto.ActivityQuestionDTO;
import com.shakemate.activity.dto.request.ActivityQuestionCreateDTO;
import com.shakemate.activity.dto.request.ActivityQuestionUpdateDTO;
import com.shakemate.activity.service.ActivityQuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/activity-questions")
public class ActivityQuestionController {

    private final ActivityQuestionService activityQuestionService;

    @GetMapping("/{id}")
    public ApiResponse<ActivityQuestionDTO> getById(@PathVariable Integer id) {
        ActivityQuestionDTO dto = activityQuestionService.getById(id);
        return ApiResponse.success(dto);
    }

    @GetMapping
    public ApiResponse<List<ActivityQuestionDTO>> getAll() {
        List<ActivityQuestionDTO> list = activityQuestionService.getAll();
        return ApiResponse.success(list);
    }

    @PostMapping
    public ApiResponse<ActivityQuestionDTO> create(@Valid @RequestBody ActivityQuestionCreateDTO createDTO) {
        ActivityQuestionDTO dto = activityQuestionService.create(createDTO);
        return ApiResponse.success(dto);
    }

    @PatchMapping("/{id}")
    public ApiResponse<ActivityQuestionDTO> update(@PathVariable Integer id,
                                                   @Valid @RequestBody ActivityQuestionUpdateDTO updateDTO) {
        ActivityQuestionDTO dto = activityQuestionService.update(id, updateDTO);
        return ApiResponse.success(dto);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        activityQuestionService.delete(id);
        return ApiResponse.success(null);
    }


    @PostMapping("/questionnaire")
    public ApiResponse<List<ActivityQuestionDTO>> createActivityQuestionnaire(@RequestBody List<@Valid ActivityQuestionCreateDTO> createDTOs) {
        List<ActivityQuestionDTO> dtoList = activityQuestionService.createActivityQuestionnaire(createDTOs);
        return ApiResponse.success(dtoList);
    }

    @GetMapping("/get-questionnaire")
    public ApiResponse<List<ActivityQuestionDTO>> getActivityQuestionnaire(@RequestParam Integer activityId) {
        List<ActivityQuestionDTO> activityQuestionDTOList = activityQuestionService.getActivityQuestionnaire(activityId);
        return ApiResponse.success(activityQuestionDTOList);
    }

    @GetMapping("/existing")
    public ApiResponse<Boolean> isQuestionExist(@RequestParam Integer activityId){
        Boolean state = activityQuestionService.isQuestionExist(activityId);
        return ApiResponse.success(state);
    }

}
