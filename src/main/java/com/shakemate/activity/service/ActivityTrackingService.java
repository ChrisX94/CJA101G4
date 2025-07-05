package com.shakemate.activity.service;

import com.shakemate.activity.dto.ActivityTrackingDTO;
import com.shakemate.activity.dto.request.ActivityTrackingCreateDTO;
import com.shakemate.activity.dto.request.ActivityTrackingUpdateDTO;
import com.shakemate.activity.entity.id.ActivityTrackingId;

import java.util.List;

public interface ActivityTrackingService {

    ActivityTrackingDTO getById(ActivityTrackingId id);
    List<ActivityTrackingDTO> getAll();
    ActivityTrackingDTO create(ActivityTrackingCreateDTO createDTO);
    ActivityTrackingDTO update(ActivityTrackingId id, ActivityTrackingUpdateDTO updateDTO);
    void delete(ActivityTrackingId id);

    void createTracking(ActivityTrackingCreateDTO createDTO);
    void unTracking(Integer userId, Integer activityId);

}
