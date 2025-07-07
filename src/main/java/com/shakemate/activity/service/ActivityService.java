package com.shakemate.activity.service;

import com.shakemate.activity.dto.ActivityDTO;
import com.shakemate.activity.dto.request.ActivityCreateDTO;
import com.shakemate.activity.dto.request.ActivityCreateRequest;
import com.shakemate.activity.dto.request.ActivityUpdateDTO;
import com.shakemate.activity.dto.response.*;
import com.shakemate.activity.entity.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ActivityService {

    ActivityDTO getActivityById(Integer id);
    List<ActivityDTO> getAllActivities();
    ActivityDTO createActivity(ActivityCreateDTO createDTO);
    ActivityDTO updateActivity(Integer id, ActivityUpdateDTO updateDTO);
    void deleteActivity(Integer id);

    PostResponse getOnePost(Integer postId, Integer userId);
    Page<PostResponse> getWallPost(Integer userId, int page, int size, String sortBy, String sortDirection);
    void cancelActivity(Integer activityId);
    Page<PostResponse> getTagWallPost(String tag, Integer userId, int page, int size, String sortBy, String sortDirection);
    ActivityCreateResponse createActivityAllInOne(ActivityCreateRequest createRequest);
    Page<OwnerCardResponse> getOwnerCards(Integer userId, int page, int size, String sort);
    Page<JoinedCardResponse> getJoinedCard(Integer userId, int page, int size, String sort);
    Page<SavedCardResponse> getSavedCard(Integer userId, int page, int size, String sort);
    ActivityStatusResponse getActivityStatus(Integer activityId);
    List<ActivityCalendarResponse> getMyCalendar(Integer userId);

    // 測試用
    Page<Activity> getFilteredActivities(int userAge, int userGender, int page, int size, String sort);
    Page<ActivityCardDTO> getOwnerActivityCards(Integer userId, int page, int size, String sort);
    Page<ActivityCardDTO> getAllMemberActivityCards(Integer userId, int page, int size, String sort);
    Page<ActivityCardDTO> getTrackedOngoingActivityCards(Integer userId, int page, int size, String sort);
    Page<ActivityCardDTO> getVisibleActivitiesForUser(Integer userId, Pageable pageable);
}
