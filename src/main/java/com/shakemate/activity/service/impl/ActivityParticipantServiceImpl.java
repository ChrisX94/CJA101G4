package com.shakemate.activity.service.impl;

import com.shakemate.activity.dto.ActivityAnswerDTO;
import com.shakemate.activity.dto.ActivityParticipantDTO;
import com.shakemate.activity.dto.ActivityQuestionDTO;
import com.shakemate.activity.dto.request.ActivityParticipantCreateDTO;
import com.shakemate.activity.dto.request.ActivityParticipantUpdateDTO;
import com.shakemate.activity.dto.request.RatingRequest;
import com.shakemate.activity.dto.response.*;
import com.shakemate.activity.entity.Activity;
import com.shakemate.activity.entity.ActivityAnswer;
import com.shakemate.activity.entity.ActivityParticipant;
import com.shakemate.activity.entity.id.ActivityParticipantId;
import com.shakemate.activity.mapper.ActivityAnswerMapper;
import com.shakemate.activity.mapper.ActivityMapper;
import com.shakemate.activity.mapper.ActivityParticipantMapper;
import com.shakemate.activity.repository.ActivityParticipantRepository;
import com.shakemate.activity.repository.ActivityRepository;
import com.shakemate.activity.service.ActivityParticipantService;
import com.shakemate.activity.service.ActivityQuestionService;
import com.shakemate.user.dao.UsersRepository;
import com.shakemate.user.model.Users;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ActivityParticipantServiceImpl implements ActivityParticipantService {

    private final ActivityParticipantRepository participantRepository;
    private final UsersRepository usersRepository;
    private final ActivityRepository activityRepository;
    private final ActivityParticipantMapper mapper;
    private final ActivityMapper activityMapper;
    private final ActivityAnswerMapper activityAnswerMapper;
    private final ActivityQuestionService activityQuestionService;


    @Override
    public ActivityParticipantDTO ActivityParticipantById(ActivityParticipantId id) {
        ActivityParticipant entity = participantRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("查無資料"));
        ActivityParticipantDTO dto = mapper.toDTO(entity);
        dto.setParticipantId(entity.getId().getParticipantId());
        dto.setActivityId(entity.getId().getActivityId());
        return dto;
    }

    @Override
    public List<ActivityParticipantDTO> getAllActivityParticipant() {
        return participantRepository.findAll()
                .stream()
                .map(entity -> {
                    ActivityParticipantDTO dto = mapper.toDTO(entity);
                    dto.setParticipantId(entity.getId().getParticipantId());
                    dto.setActivityId(entity.getId().getActivityId());
                    return dto;
                })
                .toList();
    }

    @Override
    public ActivityParticipantDTO createActivityParticipant(ActivityParticipantCreateDTO createDTO) {
        Users user = usersRepository.findById(createDTO.getParticipantId())
                .orElseThrow(() -> new EntityNotFoundException("找不到參與者"));
        Activity activity = activityRepository.findById(createDTO.getActivityId())
                .orElseThrow(() -> new EntityNotFoundException("找不到活動"));

        ActivityParticipant entity = mapper.toEntity(createDTO);
        entity.setParticipant(user);
        entity.setActivity(activity);
        entity.setId(new ActivityParticipantId(user.getUserId(), activity.getActivityId()));

        participantRepository.save(entity);
        return mapper.toDTO(entity);
    }

    @Override
    public ActivityParticipantDTO updateActivityParticipant(ActivityParticipantId id, ActivityParticipantUpdateDTO updateDTO) {
        ActivityParticipant entity = participantRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("查無資料"));

        // 利用 Mapper 更新 entity（忽略 null 欄位）
        mapper.updateEntityFromDto(updateDTO, entity);



        ActivityParticipant updated = participantRepository.save(entity);

        ActivityParticipantDTO dto = mapper.toDTO(updated);
        dto.setParticipantId(updated.getId().getParticipantId());
        dto.setActivityId(updated.getId().getActivityId());
        return dto;
    }

    @Override
    public void deleteActivityParticipant(ActivityParticipantId id) {
        boolean exists = participantRepository.existsById(id);
        if (!exists) {
            throw new EntityNotFoundException("查無資料");
        }
        participantRepository.deleteById(id);
    }

    @Override
    public Page<ActivityParticipantDTO> getApplicants(Integer activityId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ActivityParticipant> pageResult = participantRepository.findAllApplicantsByActivityId(activityId, pageable);
        return pageResult.map(mapper::toDTO);
    }

    @Override
    public Page<ActivityParticipantDTO> getAcceptedMembers(Integer activityId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ActivityParticipant> pageResult = participantRepository.findAllAcceptedByActivityId(activityId, pageable);
        return pageResult.map(mapper::toDTO);
    }

    @Override
    public Page<ActivityParticipantDTO> getActivityReviews(Integer activityId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ActivityParticipant> pageResult = participantRepository.findAllReviewsByActivityId(activityId, pageable);
        return pageResult.map(mapper::toDTO);
    }

    public Double getAverageRating(Integer activityId) {
        return participantRepository.findAverageRatingByActivityId(activityId);
    }


    // 使用者是否有資格參與活動
    public IsApplyAvailableResponse isApplyAvailable(Integer activityId, Integer userId) {

        Activity targetActivity = activityRepository.findById(activityId)
                .orElseThrow(() -> new EntityNotFoundException("查無活動"));

        // 0 - 活動已取消
        if (targetActivity.getActivityStatus() == 3) {
            return new IsApplyAvailableResponse(0, "活動已取消");
        }

        // 7 - 團主不能申請自己活動
        if (Objects.equals(userId, targetActivity.getUser().getUserId())) {
            return new IsApplyAvailableResponse(7, "團主不可申請自己的活動！");
        }


        // 1 - 報名截止
        Instant now = Instant.now();
        boolean isEnd = !now.isBefore(targetActivity.getRegEndTime().toInstant());
        if (isEnd) {
            return new IsApplyAvailableResponse(1, "報名已截止");
        }

        // 2 - 人數已滿
        boolean isFull = targetActivity.getSignupCount() >= targetActivity.getMaxPeople();
        if (isFull) {
            return new IsApplyAvailableResponse(2, "人數已滿");
        }

        // 取得申請人
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("查無使用者"));

        // 3 - 年齡不符
        if (user.getBirthday() == null) {
            throw new IllegalArgumentException("生日不能為 null");
        }
        LocalDate birth = user.getBirthday().toLocalDate();
        LocalDate today = now.atZone(ZoneId.of("Asia/Taipei")).toLocalDate();
        int userAge = Period.between(birth, today).getYears();

        int minAge = targetActivity.getMinAge();
        int maxAge = targetActivity.getMaxAge();
        if (userAge < minAge || userAge > maxAge) {
            return new IsApplyAvailableResponse(3, "年齡不符");
        }

        // 4 - 性別不符
        int userGender = user.getGender(); // 0:男, 1:女
        byte actGender = targetActivity.getGenderFilter(); // 0:不限, 1:男, 2:女
        boolean isGenderPass = switch (actGender) {
            case 0 -> true;
            case 1 -> userGender == 0;
            case 2 -> userGender == 1;
            default -> false;
        };
        if (!isGenderPass) {
            return new IsApplyAvailableResponse(4, "性別不符");
        }

        // 5 / 6 - 已申請 or 已加入
        Optional<ActivityParticipant> opt = participantRepository.findById(
                new ActivityParticipantId(userId, activityId));

        if (opt.isPresent()) {
            ActivityParticipant participant = opt.get();
            int status = participant.getParStatus();
            if (status == 0) {
                return new IsApplyAvailableResponse(5, "已經申請過！");
            }
            if (status == 2) {
                return new IsApplyAvailableResponse(6, "已經加入！");
            }
        }


        // -1 - 符合資格
        return new IsApplyAvailableResponse(-1, "符合申請資格");

//        if(activityStatusCode == 3) {
//            applyResponse.setApplyStatusCode(0);
//            applyResponse.setApplyStatusLabel("活動已取消");
//        } else {
//
//            if(isEnd) {
//                applyResponse.setApplyStatusCode(1);
//                applyResponse.setApplyStatusLabel("報名已截止");
//            } else {
//                if(isFull) {
//                    applyResponse.setApplyStatusCode(2);
//                    applyResponse.setApplyStatusLabel("人數已滿");
//                } else {
//                    if(!isAgePass) {
//                        applyResponse.setApplyStatusCode(3);
//                        applyResponse.setApplyStatusLabel("年齡不符");
//                    } else {
//                        if(!isGenderPass) {
//                            applyResponse.setApplyStatusCode(4);
//                            applyResponse.setApplyStatusLabel("性別不符");
//                        } else {
//                            applyResponse.setApplyStatusCode(-1);
//                            applyResponse.setApplyStatusLabel("符合申請資格");
//                        }
//                    }
//                }
//            }
//
//        }


    }




    // 新增一筆活動參與紀錄（若有則修改狀態）-未檢查報名是否截止
    @Override
    @Transactional
    public ActivityParticipantResponse createApply(Integer activityId, Integer userId) {

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("找不到參與者"));
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new EntityNotFoundException("找不到活動"));

        // 先找是否存在
        Optional<ActivityParticipant> activityParticipant = participantRepository.findById(new ActivityParticipantId(userId, activityId));

        if (activityParticipant.isPresent()) {
            // 有找到資料
            ActivityParticipant participant = activityParticipant.get();

            if(participant.getParStatus() == 2) {
                throw new IllegalStateException("該活動已經加入過！");
            }

            // 申請中
            participant.setParStatus((byte) 0);

            // 申請時間 ＆ 審核時間
            Instant now = Instant.now();
            participant.setApplyingDate( Timestamp.from(now));
            participant.setAdmReviewTime(null);

            // 儲存
            participantRepository.save(participant);


            ActivityParticipantResponse response = new ActivityParticipantResponse();
            response.setPId(activityId);
            response.setUId(userId);
            response.setPStatusCode(0);
            response.setPStatusLabel("申請中");




            return response;
        }




        ActivityParticipant createParticipant = new ActivityParticipant();

        createParticipant.setParticipant(user);
        createParticipant.setActivity(activity);
        Byte b = 0; // 申請中
        createParticipant.setParStatus(b);

        // get now time
        Instant now = Instant.now();
        createParticipant.setApplyingDate( Timestamp.from(now));

        createParticipant.setId(new ActivityParticipantId(user.getUserId(), activity.getActivityId()));

        participantRepository.save(createParticipant);

        ActivityParticipantResponse response = new ActivityParticipantResponse();
        response.setPId(activityId);
        response.setUId(userId);
        response.setPStatusCode(0);
        response.setPStatusLabel("申請中");


        return response;


    }

    // 查看某個活動目前的團員 （無答卷）
    @Override
    public List<ActivityMemberResponse> getActivityMember(Integer activityId){
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new EntityNotFoundException("找不到活動"));
        List<ActivityParticipant> activityParticipantList = activity.getActivityParticipants();

        if(activityParticipantList.isEmpty()) { // 若沒有參與者
            return Collections.emptyList();
        }

        List<ActivityParticipant> applicantList = new ArrayList<>(); // 參與者狀態為 0 - 申請中

        // 獲取參與者狀態為 2 的活動參與資料
        for(ActivityParticipant activityParticipant : activityParticipantList) {

            if(activityParticipant.getParStatus() == 2) {
                applicantList.add(activityParticipant);
            }

        }

        if(applicantList.isEmpty()) { // 若沒有申請者
            return Collections.emptyList();
        }

        // 補充查詢更多資訊，放入Response中
        List<ActivityMemberResponse> responseList = new ArrayList<>();
        for(ActivityParticipant member : applicantList) {

//            Integer userId = applicant.getId().getParticipantId();

            ActivityMemberResponse response = new ActivityMemberResponse();

            // 團員資訊
            Users user = member.getParticipant();
            response.setUserId(user.getUserId());
            response.setUserName(user.getUsername());

            String applicantEmail = user.getEmail();
            String accName = "User";
            if (applicantEmail != null && applicantEmail.contains("@")) {
                accName =  applicantEmail.substring(0, applicantEmail.indexOf("@"));
            } else {
                accName = "User";
            }

            response.setUserAccName(accName);
            response.setUserImgUrl(user.getImg1());
            response.setUserIntro(user.getIntro());

            response.setActivityId(activityId);
            response.setParStatus(member.getParStatus());
            response.setAdmReviewTime(member.getAdmReviewTime());

            // - 可在加入團員當初的答卷

            responseList.add(response);
        }

        return responseList;


    }



    @Override
    public ActivityApplicantResponseWithQuestions getActivityApplicantsWithQuestions(Integer activityId) {

        List<ActivityQuestionDTO> activityQuestionnaire = activityQuestionService.getActivityQuestionnaire(activityId);
        List<ActivityApplicantResponse> activityApplicants = getActivityApplicants(activityId);

        ActivityApplicantResponseWithQuestions response = new ActivityApplicantResponseWithQuestions();
        response.setQuestionList(activityQuestionnaire);
        response.setResponsesList(activityApplicants);

        return response;

    }

    // 獲得某活動某使用者的回答和問題
    @Override
    public ApplicantsQAResponse getApplicantsQA(Integer userId, Integer activityId) {
        List<ActivityQuestionDTO> activityQuestionnaire = activityQuestionService.getActivityQuestionnaire(activityId);


        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("查無使用者"));

        //取得申請者的所有回答
        List<ActivityAnswer> activityAnswerList = user.getActivityAnswers();
        List<ActivityAnswerDTO> activityAnswerDTOList = activityAnswerList.stream()
                .map(activityAnswerMapper::toDto).collect(Collectors.toList());

        List<ActivityAnswerDTO> applicantAnswerList = new ArrayList<>();
        for(ActivityAnswerDTO activityAnswerDTO : activityAnswerDTOList) {
            // 篩選回答的活動id為當前活動id
            if(activityAnswerDTO.getActivityId().equals(activityId)) {
                applicantAnswerList.add(activityAnswerDTO);
            }
        }

        ApplicantsQAResponse response = new ApplicantsQAResponse();
        response.setQuestions(activityQuestionnaire);
        response.setAnswers(applicantAnswerList);


        return response;


    }

    // 只獲取申請者名單
    @Override
    public List<ApplicantsListResponse> getApplicantsList(Integer activityId) {

        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new EntityNotFoundException("找不到活動"));
        List<ActivityParticipant> activityParticipantList = activity.getActivityParticipants();

        if(activityParticipantList.isEmpty()) { // 若沒有參與者
            return Collections.emptyList();
        }

        List<ActivityParticipant> applicantList = new ArrayList<>(); // 參與者狀態為 0 - 申請中

        // 獲取參與者狀態為 0 的活動參與資料
        for(ActivityParticipant activityParticipant : activityParticipantList) {

            if(activityParticipant.getParStatus() == 0) {
                applicantList.add(activityParticipant);
            }

        }

        if(applicantList.isEmpty()) { // 若沒有申請者
            return Collections.emptyList();
        }

        // 補充查詢更多資訊，放入Response中
        List<ApplicantsListResponse> responseList = new ArrayList<>();
        for(ActivityParticipant applicant : applicantList) {

//            Integer userId = applicant.getId().getParticipantId();

            ApplicantsListResponse response = new ApplicantsListResponse();
            // 申請者資訊
            Users user = applicant.getParticipant();
            response.setUserId(user.getUserId());
            response.setUserName(user.getUsername());

            String applicantEmail = user.getEmail();
            String accName = "User";
            if (applicantEmail != null && applicantEmail.contains("@")) {
                accName =  applicantEmail.substring(0, applicantEmail.indexOf("@"));
            } else {
                accName = "User";
            }

            response.setUserAccName(accName);
            response.setUserImgUrl(user.getImg1());
            response.setUserIntro(user.getIntro());

            response.setActivityId(activityId);
            response.setParStatus(applicant.getParStatus());
            response.setApplyingDate(applicant.getApplyingDate());


            responseList.add(response);
        }



        return responseList;
    }

    @Override
    public List<ReviewListResponse> getActivityReviews(Integer activityId) {

        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new EntityNotFoundException("找不到活動"));
        List<ActivityParticipant> activityParticipantList = activity.getActivityParticipants();

        if(activityParticipantList.isEmpty()) { // 若沒有參與者
            return Collections.emptyList();
        }

        List<ActivityParticipant> applicantList = new ArrayList<>(); // 參與者狀態為 2 - 申請中

        // 獲取參與者狀態為 0 的活動參與資料
        for(ActivityParticipant activityParticipant : activityParticipantList) {

            if(activityParticipant.getParStatus() == 2 && activityParticipant.getReviewTime() != null) {
                applicantList.add(activityParticipant);
            }

        }

        if(applicantList.isEmpty()) { // 若沒有團員
            return Collections.emptyList();
        }

        List<ReviewListResponse> responseList = new ArrayList<>();
        for(ActivityParticipant member : applicantList) {

            ReviewListResponse response = new ReviewListResponse();

            // 申請者資訊
            Users user = member.getParticipant();
            response.setUserId(user.getUserId());
            response.setUserName(user.getUsername());

            String applicantEmail = user.getEmail();
            String accName = "User";
            if (applicantEmail != null && applicantEmail.contains("@")) {
                accName =  applicantEmail.substring(0, applicantEmail.indexOf("@"));
            } else {
                accName = "User";
            }

            response.setUserAccName(accName);
            response.setUserImgUrl(user.getImg1());
            response.setUserIntro(user.getIntro());

            response.setActivityId(activityId);
            response.setApplyingDate(member.getApplyingDate());

            response.setRating(member.getRating());
            response.setReviewContent(member.getReviewContent());
            response.setReviewTime(member.getReviewTime());

            responseList.add(response);
        }

        return responseList;
    }



    // 查看某個活動目前的申請者 ＆ 答卷
    @Override
    public List<ActivityApplicantResponse> getActivityApplicants(Integer activityId) {

        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new EntityNotFoundException("找不到活動"));
        List<ActivityParticipant> activityParticipantList = activity.getActivityParticipants();

        if(activityParticipantList.isEmpty()) { // 若沒有參與者
            return Collections.emptyList();
        }

        List<ActivityParticipant> applicantList = new ArrayList<>(); // 參與者狀態為 0 - 申請中

        // 獲取參與者狀態為 0 的活動參與資料
        for(ActivityParticipant activityParticipant : activityParticipantList) {

            if(activityParticipant.getParStatus() == 0) {
                applicantList.add(activityParticipant);
            }

        }

        if(applicantList.isEmpty()) { // 若沒有申請者
            return Collections.emptyList();
        }

        // 補充查詢更多資訊，放入Response中
        List<ActivityApplicantResponse> responseList = new ArrayList<>();
        for(ActivityParticipant applicant : applicantList) {

//            Integer userId = applicant.getId().getParticipantId();

            ActivityApplicantResponse response = new ActivityApplicantResponse();
            // 申請者資訊
            Users user = applicant.getParticipant();
            response.setUserId(user.getUserId());
            response.setUserName(user.getUsername());

            String applicantEmail = user.getEmail();
            String accName = "User";
            if (applicantEmail != null && applicantEmail.contains("@")) {
                accName =  applicantEmail.substring(0, applicantEmail.indexOf("@"));
            } else {
                accName = "User";
            }

            response.setUserAccName(accName);
            response.setUserImgUrl(user.getImg1());
            response.setUserIntro(user.getIntro());

            response.setActivityId(activityId);
            response.setParStatus(applicant.getParStatus());
            response.setApplyingDate(applicant.getApplyingDate());

            // - 該申請者對於該活動的問卷的答卷 ( userId == ... && activityId == ... )

            // 先取得該申請者的所有回答
            List<ActivityAnswer> activityAnswerList = user.getActivityAnswers();
            List<ActivityAnswerDTO> activityAnswerDTOList = activityAnswerList.stream()
                    .map(activityAnswerMapper::toDto).collect(Collectors.toList());

            List<ActivityAnswerDTO> applicantAnswerList = new ArrayList<>();
            for(ActivityAnswerDTO activityAnswerDTO : activityAnswerDTOList) {
                // 篩選回答的活動id為當前活動id
                if(activityAnswerDTO.getActivityId().equals(activityId)) {
                    applicantAnswerList.add(activityAnswerDTO);
                }
            }

            response.setAnswers(applicantAnswerList);




            responseList.add(response);
        }



        return responseList;
    }


    // 加上答卷


    // 批准申請者
    @Override
    @Transactional
    public void approveApplicant(Integer userId, Integer activityId) {

        // 1. 找出活動參與紀錄
        ActivityParticipant activityParticipant = participantRepository.findById(
                        new ActivityParticipantId(userId, activityId))
                .orElseThrow(() -> new EntityNotFoundException("查無紀錄"));

        // 2. 修改狀態與審核時間
        activityParticipant.setParStatus((byte) 2); // 2 - 已加入
        activityParticipant.setAdmReviewTime(Timestamp.from(Instant.now()));
        participantRepository.save(activityParticipant);

        // 3. 報名人數 +1（注意 null 安全）
        Activity activity = activityParticipant.getActivity();
        Integer originalCount = activity.getSignupCount();
        if (originalCount == null) {
            throw new IllegalStateException("活動報名人數為 null，請確認初始化邏輯");
        }
        activity.setSignupCount(originalCount + 1);
        activityRepository.save(activity);

    }

    // 拒絕申請者
    @Override
    @Transactional
    public void rejectApplicant(Integer userId, Integer activityId) {

        // 1. 找出活動參與紀錄
        ActivityParticipant activityParticipant = participantRepository.findById(
                        new ActivityParticipantId(userId, activityId))
                .orElseThrow(() -> new EntityNotFoundException("查無紀錄"));

        // 2. 修改狀態與審核時間
        activityParticipant.setParStatus((byte) 4); // 4 - 已被拒
        activityParticipant.setAdmReviewTime(Timestamp.from(Instant.now()));
        participantRepository.save(activityParticipant);

    }

    // 評價活動
    @Override
    @Transactional
    public void submitActivityRating(RatingRequest ratingRequest) {

        Integer activityId = ratingRequest.getActivityId();

        // 判斷活動使否結束
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new EntityNotFoundException("查無活動"));

        // 報名是否已截止 (now vs activEndTime)

        boolean isEnd = false;

        Instant now = Instant.now();
        Timestamp activEndTime = activity.getActivEndTime();
        if(!now.isBefore(activEndTime.toInstant())) {
            isEnd = true;
        }

        if(!isEnd) {
            throw new IllegalStateException("活動尚未結束！");
        }

        // 使用者是否是團員

        Integer userId = ratingRequest.getUserId();
        ActivityParticipant activityParticipant = participantRepository.findById(new ActivityParticipantId(userId, activityId))
                .orElseThrow(() -> new EntityNotFoundException("查無參與紀錄"));

        if(activityParticipant.getParStatus() != 2) { // 判斷是否加入 （2 - 已加入）
            throw new IllegalStateException("使用者未加入該活動！");
        }

        // 填入評價
        activityParticipant.setRating(ratingRequest.getRating());
        activityParticipant.setReviewContent(ratingRequest.getReviewContent());
        activityParticipant.setReviewTime(Timestamp.from(Instant.now())); // 由後端取得現在時間

        participantRepository.save(activityParticipant);

    }

    // 取消申請活動
    @Override
    @Transactional
    public void unApply(Integer userId, Integer activityId) {

        // 判斷是否有參與紀錄
        ActivityParticipant activityParticipant = participantRepository.findById(new ActivityParticipantId(userId, activityId))
                .orElseThrow(() -> new EntityNotFoundException("查無參與紀錄"));

        // 判斷是否為申請者
        if(activityParticipant.getParStatus() != 0) { // 0 - 申請中
            throw new IllegalStateException("該使用者並非該活動申請者！");
        }

        activityParticipant.setParStatus((byte) 1); // 1 - 已取消申請

        participantRepository.save(activityParticipant);

    }

    // 退出活動（已參加）
    @Override
    @Transactional
    public void unJoin(Integer userId, Integer activityId) {

        // 判斷是否有參與紀錄
        ActivityParticipant activityParticipant = participantRepository.findById(new ActivityParticipantId(userId, activityId))
                .orElseThrow(() -> new EntityNotFoundException("查無參與紀錄"));

        // 判斷是否為團員
        if(activityParticipant.getParStatus() != 2) { // 2 - 已加入
            throw new IllegalStateException("該使用者並非該活動團員（加入者）！");
        }

        activityParticipant.setParStatus((byte) 3); // 3 - 已退出

        // 活動報名人數 - 1
        Activity activity = activityParticipant.getActivity();
        Integer currentCount = activity.getSignupCount();
        if (currentCount == null || currentCount <= 0) {
            throw new IllegalStateException("活動報名人數異常，無法執行退出");
        }
        activity.setSignupCount(currentCount - 1);

        activityRepository.save(activity);

        participantRepository.save(activityParticipant);

    }




}
