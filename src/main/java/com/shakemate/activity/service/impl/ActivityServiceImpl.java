package com.shakemate.activity.service.impl;

import com.shakemate.activity.common.ActivityStatusUtil;
import com.shakemate.activity.dto.ActivityDTO;
import com.shakemate.activity.dto.ActivityQuestionDTO;
import com.shakemate.activity.dto.request.*;
import com.shakemate.activity.dto.response.*;
import com.shakemate.activity.entity.Activity;
import com.shakemate.activity.entity.ActivityParticipant;
import com.shakemate.activity.entity.ActivityTracking;
import com.shakemate.activity.entity.id.ActivityParticipantId;
import com.shakemate.activity.mapper.ActivityCardMapper;
import com.shakemate.activity.mapper.ActivityMapper;
import com.shakemate.activity.repository.ActivityParticipantRepository;
import com.shakemate.activity.repository.ActivityRepository;
import com.shakemate.activity.repository.ActivityTrackingRepository;
import com.shakemate.activity.service.*;
import com.shakemate.user.dao.UsersRepository;
import com.shakemate.user.model.Users;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;
    private final ActivityParticipantRepository activityParticipantRepository;
    private final UsersRepository userRepository;
    private final ActivityMapper activityMapper;
    private final ActivityCardMapper activityCardMapper;
    private final ActivityTagRedisService activityTagRedisService;
    private final ActivityQuestionService activityQuestionService;

    private final ActivityTrackingService activityTrackingService;
    private final ActivityTrackingRepository activityTrackingRepository;
    private final ActivityParticipantService activityParticipantService;

    // 新建活動
    // 若有問卷，則新增問卷
    // 若有標籤，則新增標籤
    @Override
    @Transactional
    public ActivityCreateResponse createActivityAllInOne(ActivityCreateRequest createRequest) {

        Activity activity = new Activity();
        Users user = userRepository.findById(createRequest.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("查無使用者"));

        activity.setUser(user);
        activity.setTitle(createRequest.getTitle());
        activity.setContent(createRequest.getContent());
        activity.setImageUrl(createRequest.getImageUrl());
        activity.setLocation(createRequest.getLocation());
        activity.setActivityStatus(createRequest.getActivityStatus());
        activity.setCreatedTime(Timestamp.from(Instant.now()));
        activity.setUpdatedTime(Timestamp.from(Instant.now()));
        activity.setRegStartTime(createRequest.getRegStartTime());
        activity.setRegEndTime(createRequest.getRegEndTime());
        activity.setActivStartTime(createRequest.getActivStartTime());
        activity.setActivEndTime(createRequest.getActivEndTime());
        activity.setGenderFilter(createRequest.getGenderFilter());
        activity.setMaxAge(createRequest.getMaxAge());
        activity.setMinAge(createRequest.getMinAge());
        activity.setExpiredTime(createRequest.getRegEndTime());
        activity.setMaxPeople(createRequest.getMaxPeople());
        activity.setMinPeople(createRequest.getMinPeople());
        activity.setSignupCount(1);
        activity.setRatingCount(0);
        activity.setRating(null);
        activity.setCommentCount(0);
        activity.setReportCount(0);

        Activity saved = activityRepository.save(activity);
        ActivityCreateResponse response = new ActivityCreateResponse();

        response.setCreateRequest(createRequest);

        Integer activityId = saved.getActivityId();
        response.setActivityId(activityId);

        // 若有標籤，新增標籤
        if(!createRequest.getTagList().isEmpty()) {
            activityTagRedisService.saveActivityTags(activityId, createRequest.getTagList());
        }

        // 若有問卷，新增問卷
        if(!createRequest.getQuestionList().isEmpty()) {

            List<ActivityQuestionCreateRequest> questionCreateRequestList = createRequest.getQuestionList();
            List<ActivityQuestionCreateDTO> questionCreateDTOS = new ArrayList<>();
            for (ActivityQuestionCreateRequest question :  questionCreateRequestList) {
                ActivityQuestionCreateDTO createDTO = new ActivityQuestionCreateDTO();
                createDTO.setActivityId(activityId);
                createDTO.setQuestionText(question.getQuestionText());
                createDTO.setQuestionOrder(question.getQuestionOrder());
                questionCreateDTOS.add(createDTO);
            }
            List<ActivityQuestionDTO> questionList = activityQuestionService.createActivityQuestionnaire(questionCreateDTOS);
            response.setQuestionList(questionList);

        }

        return response;


    }


    @Override
    @Transactional
    public ActivityDTO createActivity(ActivityCreateDTO createDTO) {

        // 轉換 createDTO → Entity
        Activity activity = activityMapper.toEntity(createDTO);

        // 設定關聯 user（createDTO 需帶 userId）
        Users user = userRepository.findById(createDTO.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("找不到該使用者"));
        activity.setUser(user);

        // 第一個報名者為團主
        activity.setSignupCount(1);

        // 儲存
        Activity saved = activityRepository.save(activity);

        // Entity → DTO 回傳
        return activityMapper.toDTO(saved);
    }

    @Override
    public ActivityDTO updateActivity(Integer id, ActivityUpdateDTO updateDTO) {
        // 找到舊資料
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("找不到該活動"));

        // 用 MapStruct 的 updateEntityFromDto 只更新有值的欄位，null 不會覆蓋
        activityMapper.updateEntityFromDto(updateDTO, activity);

        // 若 updateDTO 有 userId 欄位，代表要更新 user 關聯
//        if (updateDTO.getUserId() != null) {
//            Users user = userRepository.findById(updateDTO.getUserId())
//                    .orElseThrow(() -> new EntityNotFoundException("找不到該使用者"));
//            activity.setUser(user);
//        }

        // 儲存更新後資料
        Activity updated = activityRepository.save(activity);

        // Entity → DTO 回傳
        return activityMapper.toDTO(updated);
    }

    @Override
    public ActivityDTO getActivityById(Integer id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("找不到該活動"));
        return activityMapper.toDTO(activity);
    }

    @Override
    public List<ActivityDTO> getAllActivities() {
        List<Activity> list = activityRepository.findAll();
        return list.stream()
                .map(activityMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteActivity(Integer id) {
        activityRepository.deleteById(id);
    }


    @Override
    public PostResponse getOnePost(Integer postId, Integer userId) {
        Activity activity = activityRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("找不到該活動"));

        PostResponse response = new PostResponse();


        Integer activityId = activity.getActivityId();
        response.setActivityId(activityId);

        // 主辦人資訊
        Users host = activity.getUser();
        response.setHostId(host.getUserId());
        response.setHostName(host.getUsername());

        String hostEmail = host.getEmail();
        String accName = "User";
        if (hostEmail != null && hostEmail.contains("@")) {
            accName =  hostEmail.substring(0, hostEmail.indexOf("@"));
        } else {
            accName = "User";
        }

        response.setHostAccName(accName);
        response.setHostImgUrl(host.getImg1());
        response.setHostIntro(host.getIntro());

        // 活動資訊
        response.setTitle(activity.getTitle());
        response.setImageUrl(activity.getImageUrl());
        response.setLocation(activity.getLocation());
        response.setCreatedTime(activity.getCreatedTime());
        response.setContent(activity.getContent());
        response.setCommentCount(activity.getCommentCount());

        Timestamp regStartTime = activity.getRegStartTime();
        Timestamp regEndTime = activity.getRegEndTime();
        Timestamp activStartTime = activity.getActivStartTime();
        Timestamp activEndTime = activity.getActivEndTime();

        // 活動當前狀態判斷
        Byte statusCode;

        if(activity.getActivityStatus() != 3) {
            Instant now = Instant.now();

            if (now.isBefore(regStartTime.toInstant())) {
                statusCode = 0; // 報名尚未開始
            } else if (!now.isBefore(regStartTime.toInstant()) && now.isBefore(regEndTime.toInstant())) {
                statusCode =  1; // 報名已開始
            } else if (!now.isBefore(regEndTime.toInstant()) && now.isBefore(activStartTime.toInstant())) {
                statusCode =  2; // 報名已結束
            } else if (!now.isBefore(activStartTime.toInstant()) && now.isBefore(activEndTime.toInstant())) {
                statusCode =  3; // 活動已開始
            } else {
                statusCode =  4; // 活動已結束
            }
        } else {
            statusCode = 5;
        }

        String statusLabel = switch (statusCode) {
            case 0 -> "報名未開始";
            case 1 -> "報名已開始";
            case 2 -> "報名已結束";
            case 3 -> "活動已開始";
            case 4 -> "活動已結束";
            case 5 -> "活動已取消/下架";
            default -> "未知狀態";
        };

        response.setActivityStatusCode(statusCode);
        response.setActivityStatusLabel(statusLabel);


        // 已報名人數
        Integer signupCount = activity.getSignupCount();
        response.setSignupCount(signupCount);

        Integer minPeople = activity.getMinPeople();
        Integer maxPeople = activity.getMaxPeople();

        response.setMinPeople(minPeople);
        response.setMaxPeople(maxPeople);

        Integer remainingSlots = maxPeople - signupCount;
        Integer peopleToFormGroup = minPeople - signupCount;
        Integer safePeopleToFormGroup = Math.max(0, peopleToFormGroup);

        response.setRemainingSlots(remainingSlots);
        response.setPeopleToFormGroup(peopleToFormGroup);
        response.setSafePeopleToFormGroup(safePeopleToFormGroup);


        // 使用者參與活動情況（0, 未參加 1, 申請中 2, 已參加）
        Byte pStatusCode;

        ActivityParticipantId findId = new ActivityParticipantId(userId, activityId);

        Optional<ActivityParticipant> participantEntity = activityParticipantRepository.findById(findId);
        if (participantEntity.isPresent()) {
            // 有值，可以用 entity.get() 拿到 ActivityParticipant
            ActivityParticipant ap = participantEntity.get();
            // 進一步操作
            Byte apCode = ap.getParStatus();
            //  0:申請中（預設）
            //  1:已取消申請
            //  2:已加入
            //  3:已退出
            if(apCode == 0) {
                pStatusCode = 1;
            } else if(apCode == 1) {
                pStatusCode = 0;
            } else if(apCode == 2) {
                pStatusCode = 2;
            } else if(apCode == 3) {
                pStatusCode = 0;
            } else {
                pStatusCode = 0;
            }

        } else {
            // Optional 是空的，沒找到資料
            pStatusCode = 0;
        }

        String pStatusLabel = switch (pStatusCode) {
            case 0 -> "未參加";
            case 1 -> "申請中";
            case 2 -> "已參加";
            default -> "未知狀態";
        };

        response.setParticipantStatusCode(pStatusCode);
        response.setParticipantStatusLabel(pStatusLabel);

        return response;


    }


    @Override
    public Page<PostResponse> getWallPost(Integer userId, int page, int size, String sortBy, String sortDirection) {

        List<Activity> list = activityRepository.findAll();
        List<PostResponse> responseList = new ArrayList<>();

        for (Activity activity : list) {

            PostResponse postResponse = getOnePost(activity.getActivityId(), userId);

            // 不顯示已取消的活動
            if(postResponse.getActivityStatusCode() != 5)  {
                responseList.add(postResponse);
            }


        }

        // 排序條件選擇
        Comparator<PostResponse> comparator = switch (sortBy) {
            case "createdTime" -> Comparator.comparing(PostResponse::getCreatedTime, Comparator.nullsLast(Comparator.naturalOrder()));
            // 可再加其他欄位
            default -> Comparator.comparing(PostResponse::getCreatedTime, Comparator.nullsLast(Comparator.naturalOrder()));
        };

        // 判斷是否為降冪
        if ("desc".equalsIgnoreCase(sortDirection)) {
            comparator = comparator.reversed();
        }

        // 排序後再分頁
        responseList.sort(comparator);
        int start = page * size;
        int end = Math.min(start + size, responseList.size());
        List<PostResponse> pagedList = responseList.subList(start, end);

        return new PageImpl<>(pagedList, PageRequest.of(page, size), responseList.size());

    }

    // 搜尋活動（依照標籤）
    @Override
    public Page<PostResponse> getTagWallPost(String tag, Integer userId, int page, int size, String sortBy, String sortDirection) {
        Set<String> resultSet = activityTagRedisService.searchActivityIdsByTagKeyword(tag);
        List<PostResponse> responseList = new ArrayList<>();

        for (String activityIdStr : resultSet) {
            Integer activityId;
            try {
                activityId = Integer.valueOf(activityIdStr);
            } catch (NumberFormatException e) {
                continue; // 忽略非數字字串
            }

            activityRepository.findById(activityId).ifPresent(activity -> {
                PostResponse postResponse = getOnePost(activity.getActivityId(), userId);
                responseList.add(postResponse);
            });
        }

        Comparator<PostResponse> comparator = switch (sortBy) {
            case "createdTime" -> Comparator.comparing(PostResponse::getCreatedTime, Comparator.nullsLast(Comparator.naturalOrder()));
            default -> Comparator.comparing(PostResponse::getCreatedTime, Comparator.nullsLast(Comparator.naturalOrder()));
        };

        if ("desc".equalsIgnoreCase(sortDirection)) {
            comparator = comparator.reversed();
        }

        responseList.sort(comparator);

        int start = page * size;
        if (start >= responseList.size()) {
            return new PageImpl<>(Collections.emptyList(), PageRequest.of(page, size), responseList.size());
        }
        int end = Math.min(start + size, responseList.size());
        List<PostResponse> pagedList = responseList.subList(start, end);

        return new PageImpl<>(pagedList, PageRequest.of(page, size), responseList.size());
    }

    // 儲存活動標籤
    public void saveActivityTags(Integer activityId, List<String> tags) {
        activityTagRedisService.saveActivityTags(activityId, tags);
    }


    // 取消活動
    @Override
    @Transactional
    public void cancelActivity(Integer activityId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new EntityNotFoundException("查無活動"));

        activity.setActivityStatus((byte) 3); // 3 - 活動已取消
        activityRepository.save(activity);
    }

    @Override
    public List<ActivityCalendarResponse> getMyCalendar(Integer userId) {

        // 找團員
        List<ActivityParticipant> all = activityParticipantRepository.findAll();
        List<ActivityCalendarResponse> responseList = new ArrayList<>();
        for(ActivityParticipant par : all) {
            // 2 - 已加入
            if(par.getId().getParticipantId() == userId && par.getParStatus() == 2) {
                ActivityCalendarResponse cal = new ActivityCalendarResponse();
                Activity activity = par.getActivity();
                cal.setActivityId(activity.getActivityId());
                cal.setTitle(activity.getTitle());
                cal.setActivStartTime(activity.getActivStartTime());
                cal.setActivEndTime(activity.getActivEndTime());
                cal.setParticipantStatusCode((byte) 2);
                cal.setParticipantStatusLabel("團員");

                responseList.add(cal);
            }
        }

        // 找團主
        List<Activity> actAll = activityRepository.findAll();
        for(Activity act : actAll) {
            Users user = act.getUser();
            if(user.getUserId() == userId && getActivityStatus(act.getActivityId()).getStatusCode() != 6 ) {
                ActivityCalendarResponse cal = new ActivityCalendarResponse();
                cal.setActivityId(act.getActivityId());
                cal.setTitle(act.getTitle());
                cal.setActivStartTime(act.getActivStartTime());
                cal.setActivEndTime(act.getActivEndTime());
                cal.setParticipantStatusCode((byte) 1);
                cal.setParticipantStatusLabel("團主");

                responseList.add(cal);
            }
        }

        return responseList;

    }


    // 獲得參加狀態
    public ParticipantStatusResponse getParticipantStatus(Integer userId, Integer activityId) {
        Optional<ActivityParticipant> participant = activityParticipantRepository.findById(new ActivityParticipantId(userId, activityId));

        if(participant.isEmpty()) {
            return new ParticipantStatusResponse(-1, "未申請");
        } else {
            ActivityParticipant activityParticipant = participant.get();
            Byte pStatus = activityParticipant.getParStatus();
            // 0 申請中 1 已取消申請 2 已加入 3 已退出 4. 已被拒
            if(pStatus == 0) {
                return new ParticipantStatusResponse(0, "申請中");
            } else if(pStatus == 1) {
                return new ParticipantStatusResponse(1, "已取消申請");
            } else if(pStatus == 2) {
                return new ParticipantStatusResponse(2, "已加入");
            } else if(pStatus == 3) {
                return new ParticipantStatusResponse(3, "已退出");
            } else if(pStatus == 4) {
                return new ParticipantStatusResponse(4, "已被拒");
            }


        }
        return new ParticipantStatusResponse(-2, "錯誤");

    }


    // 獲得活動狀態
    public ActivityStatusResponse getActivityStatus(Integer activityId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new EntityNotFoundException("查無活動"));

        // 若已取消，直接回傳狀態碼 6
        if (activity.getActivityStatus() == 3) {
            return new ActivityStatusResponse(6, "已取消");
        }


        Instant now = Instant.now();

        Instant regStart = activity.getRegStartTime().toInstant();
        Instant regEnd = activity.getRegEndTime().toInstant();
        Instant actStart = activity.getActivStartTime().toInstant();
        Instant actEnd = activity.getActivEndTime().toInstant();

        // 狀態判斷邏輯
        if (now.isBefore(regStart)) {
            return new ActivityStatusResponse(0, "尚未開放報名");
        }

        if (!now.isBefore(regStart) && now.isBefore(regEnd)) {
            return new ActivityStatusResponse(1, "報名中");
        }

        if (!now.isBefore(regEnd) && now.isBefore(actStart)) {
            return new ActivityStatusResponse(2, "即將開始");
        }

        if (!now.isBefore(actStart) && now.isBefore(actEnd)) {
            return new ActivityStatusResponse(4, "進行中");
        }

        if(now.isAfter(actEnd) && activity.getSignupCount() < activity.getMinPeople()) {
            return new ActivityStatusResponse(6, "已取消");
        }

        if (now.isAfter(actEnd)) {
            return new ActivityStatusResponse(5, "已結束");
        }

        // 理論上不會進入此處，保底
        return new ActivityStatusResponse(-1, "錯誤");
    }



    // 新版
    // 使用者為團主（活動狀態）
    public Page<OwnerCardResponse> getOwnerCards(Integer userId, int page, int size, String sort) {

        List<Activity> activityList = activityRepository.findAll();
        List<Activity> ownerList = new ArrayList<>();
        List<OwnerCardResponse> ownerResponseList = new ArrayList<>();
        for(Activity activity : activityList) {
            Users user = activity.getUser();
            Integer ownerId = user.getUserId();
            if(userId == ownerId) {
                ownerList.add(activity);

                OwnerCardResponse card = new OwnerCardResponse();

                card.setActivityId(activity.getActivityId());
                card.setTitle(activity.getTitle());
                card.setImageUrl(activity.getImageUrl());
                card.setLocation(activity.getLocation());

                card.setCreatedTime(activity.getCreatedTime());
                card.setRegStartTime(activity.getRegStartTime());
                card.setRegEndTime(activity.getRegEndTime());
                card.setActivStartTime(activity.getActivStartTime());
                card.setActivEndTime(activity.getActivEndTime());

                card.setMinPeople(activity.getMinPeople());
                card.setMaxPeople(activity.getMaxPeople());
                card.setSignupCount(activity.getSignupCount());

                Integer remainingSlots = activity.getMaxPeople() - activity.getSignupCount();
                Integer peopleToFormGroup = activity.getMinPeople() - activity.getSignupCount();
                Integer safePeopleToFormGroup = Math.max(0, peopleToFormGroup);

                card.setRemainingSlots(remainingSlots);
                card.setPeopleToFormGroup(peopleToFormGroup);
                card.setSafePeopleToFormGroup(safePeopleToFormGroup);

                card.setStatus(getActivityStatus(activity.getActivityId()));

                ownerResponseList.add(card);

            }
        }

        // 排序（根據 sort 參數）
        if (sort != null) {
            switch (sort) {
                case "createdTime":
                    ownerResponseList.sort(Comparator.comparing(OwnerCardResponse::getCreatedTime).reversed());
                    break;
                case "signupCount":
                    ownerResponseList.sort(Comparator.comparing(OwnerCardResponse::getSignupCount).reversed());
                    break;
                // 加其他排序條件
            }
        }

        // 手動分頁
        int start = page * size;
        int end = Math.min(start + size, ownerResponseList.size());

        List<OwnerCardResponse> pagedList = ownerResponseList.subList(start, end);
        Pageable pageable = PageRequest.of(page, size);

        return new PageImpl<>(pagedList, pageable, ownerResponseList.size());

    }


    // 使用者為團員（活動狀態、申請狀態）
    public Page<JoinedCardResponse> getJoinedCard(Integer userId, int page, int size, String sort) {
        List<ActivityParticipant> all = activityParticipantRepository.findAll();
        List<ActivityParticipant> mylist = new ArrayList<>();
        for(ActivityParticipant p : all) {
            if(p.getId().getParticipantId() == userId) {
                mylist.add(p);
            }
        }

        List<JoinedCardResponse> rList = new ArrayList<>();
        for(ActivityParticipant my : mylist) {
            JoinedCardResponse card = new JoinedCardResponse();

            Activity activity = my.getActivity();
            card.setActivityId(activity.getActivityId());
            card.setTitle(activity.getTitle());
            card.setImageUrl(activity.getImageUrl());
            card.setLocation(activity.getLocation());

            card.setCreatedTime(activity.getCreatedTime());
            card.setRegStartTime(activity.getRegStartTime());
            card.setRegEndTime(activity.getRegEndTime());
            card.setActivStartTime(activity.getActivStartTime());
            card.setActivEndTime(activity.getActivEndTime());

            card.setMinPeople(activity.getMinPeople());
            card.setMaxPeople(activity.getMaxPeople());

            card.setSignupCount(activity.getSignupCount());
            Integer remainingSlots = activity.getMaxPeople() - activity.getSignupCount();
            Integer peopleToFormGroup = activity.getMinPeople() - activity.getSignupCount();
            Integer safePeopleToFormGroup = Math.max(0, peopleToFormGroup);

            card.setRemainingSlots(remainingSlots);
            card.setPeopleToFormGroup(peopleToFormGroup);
            card.setSafePeopleToFormGroup(safePeopleToFormGroup);

            card.setAdmReviewTime(my.getAdmReviewTime());
            card.setApplyingDate(my.getApplyingDate());

            card.setStatus(getActivityStatus(activity.getActivityId()));
            card.setPStatus(getParticipantStatus(my.getId().getParticipantId(), activity.getActivityId()));

            if(my.getReviewTime() != null) {
                card.setReviewState(true);
            } else {
                card.setReviewState(false);
            }

            rList.add(card);

        }

        if (sort != null) {
            switch (sort) {
                case "createdTime":
                    rList.sort(Comparator.comparing(JoinedCardResponse::getCreatedTime).reversed());
                    break;
                case "signupCount":
                    rList.sort(Comparator.comparing(JoinedCardResponse::getSignupCount).reversed());
                    break;
                case "applyingDate":
                    rList.sort(Comparator.comparing(JoinedCardResponse::getApplyingDate).reversed());
                    break;
                // 你也可以加入其他排序邏輯
            }
        }

        // 分頁處理
        int start = page * size;
        int end = Math.min(start + size, rList.size());
        List<JoinedCardResponse> pagedList;

        if (start >= rList.size()) {
            pagedList = Collections.emptyList();
        } else {
            pagedList = rList.subList(start, end);
        }

        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<>(pagedList, pageable, rList.size());

    }


    // 追蹤的活動（活動狀態）
    public Page<SavedCardResponse> getSavedCard(Integer userId, int page, int size, String sort) {

        List<ActivityTracking> all = activityTrackingRepository.findAll();
        List<SavedCardResponse> rList = new ArrayList<>();
        for(ActivityTracking my : all) {
            if(my.getId().getUserId() == userId && my.getTrackingState() != 1) {
                Activity activity = my.getActivity();

                SavedCardResponse card = new SavedCardResponse();
                card.setActivityId(activity.getActivityId());
                card.setTitle(activity.getTitle());
                card.setImageUrl(activity.getImageUrl());
                card.setLocation(activity.getLocation());

                card.setCreatedTime(activity.getCreatedTime());

                card.setRegStartTime(activity.getRegStartTime());
                card.setRegEndTime(activity.getRegEndTime());
                card.setActivStartTime(activity.getActivStartTime());
                card.setActivEndTime(activity.getActivEndTime());

                card.setMinPeople(activity.getMinPeople());
                card.setMaxPeople(activity.getMaxPeople());

                card.setSignupCount(activity.getSignupCount());

                Integer remainingSlots = activity.getMaxPeople() - activity.getSignupCount();
                Integer peopleToFormGroup = activity.getMinPeople() - activity.getSignupCount();
                Integer safePeopleToFormGroup = Math.max(0, peopleToFormGroup);

                card.setRemainingSlots(remainingSlots);
                card.setPeopleToFormGroup(peopleToFormGroup);
                card.setSafePeopleToFormGroup(safePeopleToFormGroup);

                card.setStatus(getActivityStatus(activity.getActivityId()));

                card.setTrackingTime(my.getTrackingTime());

                rList.add(card);

            }
        }

        // 排序邏輯
        if (sort != null) {
            switch (sort) {
                case "createdTime":
                    rList.sort(Comparator.comparing(SavedCardResponse::getCreatedTime).reversed());
                    break;
                case "signupCount":
                    rList.sort(Comparator.comparing(SavedCardResponse::getSignupCount).reversed());
                    break;
                case "trackingTime":
                    rList.sort(Comparator.comparing(SavedCardResponse::getTrackingTime).reversed());
                    break;
                // 可擴充其他排序欄位
            }
        }

        // 分頁處理
        int start = page * size;
        int end = Math.min(start + size, rList.size());
        List<SavedCardResponse> pagedList;

        if (start >= rList.size()) {
            pagedList = Collections.emptyList();
        } else {
            pagedList = rList.subList(start, end);
        }

        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<>(pagedList, pageable, rList.size());


    }











    // 測試用 --------------------------------------------------------------
    @Override
    public Page<Activity> getFilteredActivities(int userAge, int userGender, int page, int size, String sort) {
        // 若是 spots 則用 custom query，不走 sortSpec
        if ("spots".equals(sort)) {
            Pageable pageable = PageRequest.of(page, size); // 不傳 Sort
            return activityRepository.findByUserAgeGenderOrderBySpotsLeft(userAge, userGender, pageable);
        }

        // 其餘照原有邏輯
        Sort sortSpec = getSortSpec(sort);
        Pageable pageable = PageRequest.of(page, size, sortSpec);
        return activityRepository.findByUserAgeGender(userAge, userGender, pageable);
    }


    private Sort getSortSpec(String sort) {
        return switch (sort) {
            case "earliest" -> Sort.by("createdTime").ascending();
            case "signup" -> Sort.by(Sort.Order.desc("signupCount"), Sort.Order.desc("createdTime"));
            case "deadline" -> Sort.by(Sort.Order.asc("regEndTime"), Sort.Order.desc("createdTime"));
            case "spots" -> Sort.unsorted(); // 交由 JPQL 決定排序
            default -> Sort.by(Sort.Order.desc("createdTime")); // latest
        };
    }

    // Card
    // 使用者為團主
    @Override
    public Page<ActivityCardDTO> getOwnerActivityCards(Integer userId, int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size, getSortSpec(sort));
        Page<Activity> activityPage = activityRepository.findByUserId(userId, pageable);

        return activityPage.map(activity -> {
            ActivityCardDTO dto = activityCardMapper.toCardDTO(activity);
            dto.setActivityStatus(ActivityStatusUtil.calculateStatus(activity));
            return dto;
        });
    }





    // 自訂排序條件
    private Sort getSortOwnerSpec(String sort) {
        return switch (sort) {
            case "earliest" -> Sort.by("createdTime").ascending();
            case "latest" -> Sort.by("createdTime").descending();
            default -> Sort.by("createdTime").descending();
        };
    }

    // 使用者為團員
    @Override
    public Page<ActivityCardDTO> getAllMemberActivityCards(Integer userId, int page, int size, String sort) {
        Sort sortSpec = getSortMemberSpec(sort);
        Pageable pageable = PageRequest.of(page, size, sortSpec);
        Page<Activity> activityPage = activityRepository.findAllMemberActivities(userId, pageable);

        return activityPage.map(activity -> {
            ActivityCardDTO dto = activityCardMapper.toCardDTO(activity);
            dto.setActivityStatus(ActivityStatusUtil.calculateStatus(activity));
            return dto;
        });

    }

    // 自訂排序條件
    private Sort getSortMemberSpec(String sort) {
        Sort baseSort = switch (sort) {
            case "earliest" -> Sort.by("createdTime").ascending();
            case "signup" -> Sort.by(Sort.Order.desc("signupCount"), Sort.Order.desc("createdTime"));
            case "deadline" -> Sort.by(Sort.Order.asc("regEndTime"), Sort.Order.desc("createdTime"));
            default -> Sort.by(Sort.Order.desc("createdTime"));
        };
        // 把排序欄位都加上 activity. 前綴
        return Sort.by(baseSort.stream()
                .map(order -> new Sort.Order(order.getDirection(), "activity." + order.getProperty()))
                .toList()
        );
    }

    @Override
    public Page<ActivityCardDTO> getTrackedOngoingActivityCards(Integer userId, int page, int size, String sort) {
        Sort sortSpec = getSortTrackingSpec(sort);
        Pageable pageable = PageRequest.of(page, size, sortSpec);
        Page<Activity> activityPage = activityRepository.findTrackedOngoingActivities(userId, pageable);

        return activityPage.map(activity -> {
            ActivityCardDTO dto = activityCardMapper.toCardDTO(activity);
            dto.setActivityStatus(ActivityStatusUtil.calculateStatus(activity));
            return dto;
        });

    }

    private Sort getSortTrackingSpec(String sort) {
        Sort baseSort = switch (sort) {
            case "earliest" -> Sort.by("createdTime").ascending();
            case "signup" -> Sort.by(Sort.Order.desc("signupCount"), Sort.Order.desc("createdTime"));
            case "deadline" -> Sort.by(Sort.Order.asc("regEndTime"), Sort.Order.desc("createdTime"));
            default -> Sort.by(Sort.Order.desc("createdTime"));
        };
        // 把排序欄位都加上 activity. 前綴
        return Sort.by(baseSort.stream()
                .map(order -> new Sort.Order(order.getDirection(), "activity." + order.getProperty()))
                .toList()
        );
    }

    public Page<ActivityCardDTO> getVisibleActivitiesForUser(Integer userId, Pageable pageable) {
        // 1. 查詢使用者
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("找不到該使用者"));
        // 2. 計算年齡（假設 birthday 是 java.util.Date）
        Date birthday = user.getBirthday();
        java.time.LocalDate birthDate;
        if (birthday instanceof java.sql.Date) {
            birthDate = ((java.sql.Date) birthday).toLocalDate();
        } else {
            birthDate = birthday.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();
        }
        int userAge = java.time.Period.between(birthDate, java.time.LocalDate.now()).getYears();
        // 3. 取得性別
        int userGender = user.getGender(); // 0=男, 1=女

        // 4. 查詢活動
        Page<Activity> activityPage = activityRepository.findByUserAgeGender(userAge, userGender, pageable);

        List<Integer> activityIds = activityPage.getContent().stream()
                .map(Activity::getActivityId)
                .toList();

        List<ActivityParticipant> participantList =
                activityParticipantRepository.findByParticipantUserIdAndActivityActivityIdIn(userId, activityIds);

        Map<Integer, Byte> statusMap = participantList.stream()
                .collect(Collectors.toMap(
                        ap -> ap.getActivity().getActivityId(),
                        ActivityParticipant::getParStatus
                ));

        List<ActivityCardDTO> dtoList = activityPage.getContent().stream()
                .map(a -> {
                    Byte parStatus = statusMap.get(a.getActivityId());
                    String joinStatus;

                    if (parStatus == null) {
                        joinStatus = "NOT_JOINED";
                    } else {
                        switch (parStatus) {
                            case 0: joinStatus = "PENDING"; break;
                            case 1: joinStatus = "CANCELED"; break;
                            case 2: joinStatus = "APPROVED"; break;
                            case 3: joinStatus = "LEFT"; break;
                            default: joinStatus = "UNKNOWN";
                        }
                    }

                    return ActivityCardDTO.builder()
                            .activityId(a.getActivityId())
                            .title(a.getTitle())
                            .createdTime(a.getCreatedTime())
                            .remainingQuota(a.getMaxPeople() - a.getSignupCount())
                            .activEndTime(a.getRegEndTime())
                            .joinStatus(joinStatus)
                            .build();
                })
                .toList();

        return new PageImpl<>(dtoList, pageable, activityPage.getTotalElements());
    }














}
