package com.shakemate.activity.service;

import com.shakemate.activity.dto.ActivityParticipantDTO;
import com.shakemate.activity.dto.request.ActivityParticipantCreateDTO;
import com.shakemate.activity.dto.request.ActivityParticipantUpdateDTO;
import com.shakemate.activity.dto.request.RatingRequest;
import com.shakemate.activity.dto.response.*;
import com.shakemate.activity.entity.id.ActivityParticipantId;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ActivityParticipantService {

    ActivityParticipantDTO ActivityParticipantById(ActivityParticipantId id);
    List<ActivityParticipantDTO> getAllActivityParticipant();
    ActivityParticipantDTO createActivityParticipant(ActivityParticipantCreateDTO createDTO);
    ActivityParticipantDTO updateActivityParticipant(ActivityParticipantId id, ActivityParticipantUpdateDTO updateDTO);
    void deleteActivityParticipant(ActivityParticipantId id);


    IsApplyAvailableResponse isApplyAvailable(Integer activityId, Integer userId);
    ActivityParticipantResponse createApply(Integer activityId, Integer userId);
    List<ReviewListResponse> getActivityReviews(Integer activityId);




    Page<ActivityParticipantDTO> getApplicants(Integer activityId, int page, int size);
    Page<ActivityParticipantDTO> getAcceptedMembers(Integer activityId, int page, int size);
    Page<ActivityParticipantDTO> getActivityReviews(Integer activityId, int page, int size);
    Double getAverageRating(Integer activityId);
    List<ActivityApplicantResponse> getActivityApplicants(Integer activityId);
    List<ActivityMemberResponse> getActivityMember(Integer activityId);
    void approveApplicant(Integer userId, Integer activityId);
    void rejectApplicant(Integer userId, Integer activityId);
    void submitActivityRating(RatingRequest ratingRequest);
    ActivityApplicantResponseWithQuestions getActivityApplicantsWithQuestions(Integer activityId);
    List<ApplicantsListResponse> getApplicantsList(Integer activityId);
    ApplicantsQAResponse getApplicantsQA(Integer userId, Integer activityId);


    void unApply(Integer userId, Integer activityId);
    void unJoin(Integer userId, Integer activityId);


}
