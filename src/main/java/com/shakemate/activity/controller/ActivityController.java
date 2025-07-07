package com.shakemate.activity.controller;

import com.shakemate.activity.common.ApiResponse;
import com.shakemate.activity.dto.ActivityDTO;
import com.shakemate.activity.dto.request.ActivityCreateDTO;
import com.shakemate.activity.dto.request.ActivityCreateRequest;
import com.shakemate.activity.dto.request.ActivityUpdateDTO;
import com.shakemate.activity.dto.response.*;
import com.shakemate.activity.entity.Activity;
import com.shakemate.activity.mapper.ActivityCardMapper;
import com.shakemate.activity.service.ActivityParticipantService;
import com.shakemate.activity.service.ActivityService;
import com.shakemate.user.dao.UsersRepository;
import com.shakemate.user.model.Users;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;
    private final UsersRepository userRepository;
    private final ActivityCardMapper activityCardMapper;
    private final ActivityParticipantService activityParticipantService;

    // 取得單一活動
    @GetMapping("/{id}")
    public ApiResponse<ActivityDTO> getActivity(@PathVariable Integer id) {
        ActivityDTO dto = activityService.getActivityById(id);
        return ApiResponse.success(dto);
    }

    // 取得所有活動
    @GetMapping
    public ApiResponse<List<ActivityDTO>> getAllActivities() {
        List<ActivityDTO> list = activityService.getAllActivities();
        return ApiResponse.success(list);
    }

    // 新增活動
    @PostMapping
    public ApiResponse<ActivityDTO> createActivity(@Valid @RequestBody ActivityCreateDTO createDTO) {
        ActivityDTO dto = activityService.createActivity(createDTO);
        return ApiResponse.success(dto);
    }

    // 新增活動＋包含問卷＋標籤
    @PostMapping("/create-all")
    public ApiResponse<ActivityCreateResponse> createActivityAllInOne(@Valid @RequestBody ActivityCreateRequest createRequest) {
        ActivityCreateResponse activityAllInOne = activityService.createActivityAllInOne(createRequest);
        return ApiResponse.success(activityAllInOne);
    }

    // 更新活動
    @PatchMapping("/{id}")
    public ApiResponse<ActivityDTO> updateActivity(@PathVariable Integer id,
                                                   @Valid @RequestBody ActivityUpdateDTO updateDTO) {
        ActivityDTO dto = activityService.updateActivity(id, updateDTO);
        return ApiResponse.success(dto);
    }

    // 刪除活動
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteActivity(@PathVariable Integer id) {
        activityService.deleteActivity(id);
        return ApiResponse.success(null);
    }

    // 到時候放在user controller
    @GetMapping("/current-user")
    public ApiResponse<UserInfoResponse> getCurrentUserInfo(
            @RequestParam Integer userId){
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("找不到該使用者"));
        UserInfoResponse userInfoResponse = new UserInfoResponse();
        userInfoResponse.setUserId(user.getUserId());
        userInfoResponse.setUserName(user.getUsername());

        String email = user.getEmail();
        String accName = "User";
        if (email != null && email.contains("@")) {
            accName =  email.substring(0, email.indexOf("@"));
        } else {
            accName = "User";
        }
        userInfoResponse.setUserAccName(accName);
        userInfoResponse.setUserImgUrl(user.getImg1());
        userInfoResponse.setUserIntro(user.getIntro());

        return ApiResponse.success(userInfoResponse);

    }

    @GetMapping("/post")
    public ApiResponse<PostResponse> getOnePost(
            @RequestParam Integer postId,
            @RequestParam Integer userId) {
        PostResponse response = activityService.getOnePost(postId, userId);
        return ApiResponse.success(response);
    }


    // 得到動態牆所需要資料
    @GetMapping("/wall-post")
    public ApiResponse<Page<PostResponse>> getWallPost(
            @RequestParam Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdTime") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Page<PostResponse> responses = activityService.getWallPost(userId, page, size, sortBy, sortDirection);

        return ApiResponse.success(responses);

    }


    @GetMapping("/cancel-activity")
    public ApiResponse<Void> cancelActivity(@RequestParam Integer activityId){
        activityService.cancelActivity(activityId);
        return ApiResponse.success(null);
    }






    // 測試用

    @GetMapping("/feed")
    public ApiResponse<Page<ActivityDTO>> getFeed(
            @RequestParam Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "latest") String sort) {

        // 取得會員年齡與性別 (假設已實作 UserService)
        Users user = userRepository.findById(userId).orElseThrow();
        int userAge = calculateAge(user.getBirthday());

        Page<Activity> pageVO = activityService.getFilteredActivities(userAge, user.getGender(), page, size, sort);

        Page<ActivityDTO> pageDTO = pageVO.map(this::toDTOWithStatus);

        return ApiResponse.success(pageDTO);
    }

    public int calculateAge(java.sql.Date birthday) {
        if (birthday == null) return 0;
        // 透過 getTime() 建立 Instant，再轉成 LocalDate
        LocalDate birthDate = birthday.toLocalDate(); // java.sql.Date 支援此方法

        return Period.between(birthDate, LocalDate.now()).getYears();
    }


    private ActivityDTO toDTOWithStatus(Activity activity) {
        ActivityDTO dto = new ActivityDTO();
        // 複製基本欄位
        BeanUtils.copyProperties(activity, dto);
        // 計算活動狀態（自己定義邏輯）
        dto.setActivityStatus(calculateStatus(activity));
        return dto;
    }

    private Byte calculateStatus(Activity activity) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = activity.getActivStartTime().toLocalDateTime();
        LocalDateTime endTime = activity.getActivEndTime().toLocalDateTime();

        if (activity.getActivityStatus() == 3) {
            return 3; // 已取消或下架
        } else if (now.isBefore(startTime)) {
            return 0; // 尚未開始
        } else if (now.isAfter(endTime)) {
            return 2; // 已結束
        } else {
            return 1; // 進行中
        }
    }

    @GetMapping("/owner")
    public ApiResponse<Page<ActivityCardDTO>> getOwnerActivities(
            @RequestParam Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "latest") String sort) {

        Page<ActivityCardDTO> pageDTO = activityService.getOwnerActivityCards(userId, page, size, sort);
        return ApiResponse.success(pageDTO);
    }

    // 使用者為團員

    @GetMapping("/member/all")
    public ApiResponse<Page<ActivityCardDTO>> getAllMemberActivities(
            @RequestParam Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "latest") String sort) {

        Page<ActivityCardDTO> pageDTO = activityService.getAllMemberActivityCards(userId, page, size, sort);


        return ApiResponse.success(pageDTO);
    }

    @GetMapping("/tracked-ongoing")
    public ApiResponse<Page<ActivityCardDTO>> getTrackedOngoingActivities(
            @RequestParam Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "latest") String sort) {

        Page<ActivityCardDTO> pageDTO = activityService.getTrackedOngoingActivityCards(userId, page, size, sort);

        return ApiResponse.success(pageDTO);
    }


    @GetMapping("/wall")
    public ApiResponse<Page<ActivityCardDTO>> getActivityWall(
            @RequestParam Integer userId,
            Pageable pageable) {

        Page<ActivityCardDTO> result = activityService.getVisibleActivitiesForUser(userId, pageable);
        return ApiResponse.success(result);
    }

    @GetMapping("/wall-tag")
    public ApiResponse<Page<PostResponse>> getTagWallPost(
            @RequestParam String tag,
            @RequestParam Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdTime") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Page<PostResponse> response = activityService.getTagWallPost(tag, userId, page, size, sortBy, sortDirection);
        return ApiResponse.success(response);


    }

    @GetMapping("/my-evt")
    public ApiResponse<Page<OwnerCardResponse>> getOwnerCards(
            @RequestParam Integer userId,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String sort) {

        Page<OwnerCardResponse> responses = activityService.getOwnerCards(userId, page, size, sort);
        return ApiResponse.success(responses);

    }

    @GetMapping("/my-join")
    public ApiResponse<Page<JoinedCardResponse>> getJoinedCard(
            @RequestParam Integer userId,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String sort) {

        Page<JoinedCardResponse> responses = activityService.getJoinedCard(userId, page, size, sort);
        return ApiResponse.success(responses);

    }

    @GetMapping("/my-save")
    public ApiResponse<Page<SavedCardResponse>> getSavedCard(
            @RequestParam Integer userId,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String sort){

        Page<SavedCardResponse> responses = activityService.getSavedCard(userId, page, size, sort);
        return ApiResponse.success(responses);

    }

    @GetMapping("/get-reviews")
    public ApiResponse<List<ReviewListResponse>> getActivityReviews(@RequestParam Integer activityId) {
        List<ReviewListResponse> activityReviews = activityParticipantService.getActivityReviews(activityId);
        return ApiResponse.success(activityReviews);
    }

    @GetMapping("/get-status")
    public ApiResponse<ActivityStatusResponse> getActivityStatus(@RequestParam Integer activityId) {
        ActivityStatusResponse activityStatus = activityService.getActivityStatus(activityId);
        return ApiResponse.success(activityStatus);
    }

    @GetMapping("/get-calendar")
    public ApiResponse<List<ActivityCalendarResponse>> getMyCalendar(@RequestParam Integer userId){
        List<ActivityCalendarResponse> responseList = activityService.getMyCalendar(userId);
        return ApiResponse.success(responseList);

    }












}
