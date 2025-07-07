package com.shakemate.activity.controller;

import com.shakemate.activity.common.ApiResponse;
import com.shakemate.activity.dto.ActivityCommentDTO;
import com.shakemate.activity.dto.request.ActivityCommentCreateDTO;
import com.shakemate.activity.dto.request.ActivityCommentUpdateDTO;
import com.shakemate.activity.dto.response.ActivityCommentResponse;
import com.shakemate.activity.dto.response.ReplyCommentResponse;
import com.shakemate.activity.mapper.ActivityCommentMapper;
import com.shakemate.activity.service.ActivityCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity-comments")
@RequiredArgsConstructor
public class ActivityCommentController {

    private final ActivityCommentService activityCommentService;
    private final ActivityCommentMapper activityCommentMapper;

    @GetMapping("/{id}")
    public ApiResponse<ActivityCommentDTO> getActivityComment(@PathVariable Integer id) {
        ActivityCommentDTO dto = activityCommentService.getById(id);
        return ApiResponse.success(dto);
    }

    @GetMapping
    public ApiResponse<List<ActivityCommentDTO>> getAllActivityComments() {
        List<ActivityCommentDTO> list = activityCommentService.getAll();
        return ApiResponse.success(list);
    }

    @PostMapping
    public ApiResponse<ActivityCommentDTO> createActivityComment(@Valid @RequestBody ActivityCommentCreateDTO createDTO) {
        ActivityCommentDTO dto = activityCommentService.create(createDTO);
        return ApiResponse.success(dto);
    }

    @PatchMapping("/{id}")
    public ApiResponse<ActivityCommentDTO> updateActivityComment(@PathVariable Integer id,
                                                                 @Valid @RequestBody ActivityCommentUpdateDTO updateDTO) {
        ActivityCommentDTO dto = activityCommentService.update(id, updateDTO);
        return ApiResponse.success(dto);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteActivityComment(@PathVariable Integer id) {
        activityCommentService.delete(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/get-comments")
    public ApiResponse<Page<ActivityCommentResponse>> getParentCommentsWithPreviewReplies(
            @RequestParam Integer activityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ActivityCommentResponse> response = activityCommentService.getParentCommentsWithPreviewReplies(activityId, page, size);
        return ApiResponse.success(response);
    }

    @GetMapping("/get-reply")
    public ApiResponse<Page<ReplyCommentResponse>> getReply(
            @RequestParam Integer commentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size){
        Page<ReplyCommentResponse> response = activityCommentService.getReply(commentId, page, size);
        return ApiResponse.success(response);
    }




}