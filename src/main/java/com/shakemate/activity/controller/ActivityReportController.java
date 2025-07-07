package com.shakemate.activity.controller;

import com.shakemate.activity.common.ApiResponse;
import com.shakemate.activity.dto.ActivityReportDTO;
import com.shakemate.activity.dto.request.ActivityReportCreateDTO;
import com.shakemate.activity.dto.request.ActivityReportUpdateDTO;
import com.shakemate.activity.service.ActivityReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity-reports")
@RequiredArgsConstructor
public class ActivityReportController {

    private final ActivityReportService activityReportService;

    @GetMapping("/{id}")
    public ApiResponse<ActivityReportDTO> getById(@PathVariable Integer id) {
        ActivityReportDTO dto = activityReportService.getById(id);
        return ApiResponse.success(dto);
    }

    @GetMapping
    public ApiResponse<List<ActivityReportDTO>> getAll(){
        List<ActivityReportDTO> list = activityReportService.getAll();
        return ApiResponse.success(list);
    }

    @PostMapping
    public ApiResponse<ActivityReportDTO> create(@Valid @RequestBody ActivityReportCreateDTO createDTO){
        ActivityReportDTO dto = activityReportService.create(createDTO);
        return ApiResponse.success(dto);
    }

    @PatchMapping("/{id}")
    public ApiResponse<ActivityReportDTO> update(@PathVariable Integer id,
                                                 @Valid @RequestBody ActivityReportUpdateDTO updateDTO) {
        ActivityReportDTO dto = activityReportService.update(id, updateDTO);
        return ApiResponse.success(dto);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id){
        activityReportService.delete(id);
        return ApiResponse.success(null);
    }

}
