package com.shakemate.activity.controller;

import com.shakemate.activity.common.ApiResponse;
import com.shakemate.activity.dto.ActivityTrackingDTO;
import com.shakemate.activity.dto.request.ActivityTrackingCreateDTO;
import com.shakemate.activity.dto.request.ActivityTrackingUpdateDTO;
import com.shakemate.activity.entity.id.ActivityTrackingId;
import com.shakemate.activity.service.ActivityTrackingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity-tracking")
@RequiredArgsConstructor
public class ActivityTrackingController {

    private final ActivityTrackingService activityTrackingService;

    // GetById
    @GetMapping("/{activityId}/{userId}")
    public ApiResponse<ActivityTrackingDTO> getById(
            @PathVariable Integer activityId,
            @PathVariable Integer userId) {

        ActivityTrackingId id = new ActivityTrackingId(activityId, userId);
        ActivityTrackingDTO dto = activityTrackingService.getById(id);
        return ApiResponse.success(dto);
    }

    // GetAll
    @GetMapping
    public ApiResponse<List<ActivityTrackingDTO>> getAll() {
        List<ActivityTrackingDTO> list = activityTrackingService.getAll();
        return ApiResponse.success(list);
    }

    // create
    @PostMapping
    public ApiResponse<ActivityTrackingDTO> create(@Valid @RequestBody ActivityTrackingCreateDTO createDTO) {
        ActivityTrackingDTO dto = activityTrackingService.create(createDTO);
        return ApiResponse.success(dto);
    }

    // update
    @PatchMapping("/{activityId}/{userId}")
    public ApiResponse<ActivityTrackingDTO> update(
            @PathVariable Integer activityId,
            @PathVariable Integer userId,
            @RequestBody ActivityTrackingUpdateDTO updateDTO) {

        ActivityTrackingId id = new ActivityTrackingId(activityId, userId);
        ActivityTrackingDTO updatedDTO = activityTrackingService.update(id, updateDTO);
        return ApiResponse.success(updatedDTO);

    }

    @DeleteMapping("/{activityId}/{userId}")
    public ApiResponse<Void> delete(
            @PathVariable Integer activityId,
            @PathVariable Integer userId) {

        ActivityTrackingId id = new ActivityTrackingId(activityId, userId);
        activityTrackingService.delete(id);
        return ApiResponse.success(null);

    }

    @PostMapping("/create-tracking")
    public ApiResponse<Void> createTracking(@Valid @RequestBody ActivityTrackingCreateDTO createDTO) {
        activityTrackingService.createTracking(createDTO);
        return ApiResponse.success(null);
    }

    @GetMapping("/un-tracking")
    public ApiResponse<Void> unTracking(@RequestParam Integer userId, @RequestParam Integer activityId) {
        activityTrackingService.unTracking(userId, activityId);
        return ApiResponse.success(null);

    }



}
