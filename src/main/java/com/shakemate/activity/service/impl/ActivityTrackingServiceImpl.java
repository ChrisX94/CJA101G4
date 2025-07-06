package com.shakemate.activity.service.impl;

import com.shakemate.activity.dto.ActivityTrackingDTO;
import com.shakemate.activity.dto.request.ActivityTrackingCreateDTO;
import com.shakemate.activity.dto.request.ActivityTrackingUpdateDTO;
import com.shakemate.activity.entity.Activity;
import com.shakemate.activity.entity.ActivityTracking;
import com.shakemate.activity.entity.id.ActivityTrackingId;
import com.shakemate.activity.mapper.ActivityTrackingMapper;
import com.shakemate.activity.repository.ActivityRepository;
import com.shakemate.activity.repository.ActivityTrackingRepository;
import com.shakemate.activity.service.ActivityTrackingService;
import com.shakemate.user.dao.UsersRepository;
import com.shakemate.user.model.Users;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ActivityTrackingServiceImpl implements ActivityTrackingService {

    private final ActivityTrackingRepository activityTrackingRepository;
    private final UsersRepository usersRepository;
    private final ActivityRepository activityRepository;
    private final ActivityTrackingMapper mapper;

    @Override
    public ActivityTrackingDTO getById(ActivityTrackingId id) {
        ActivityTracking entity = activityTrackingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("查無資料"));
        ActivityTrackingDTO dto = mapper.toDTO(entity);
        dto.setUserId(entity.getId().getUserId());
        dto.setActivityId(entity.getId().getActivityId());
        return dto;
    }

    @Override
    public List<ActivityTrackingDTO> getAll() {
        return activityTrackingRepository.findAll()
                .stream()
                .map(entity -> {
                    ActivityTrackingDTO dto = mapper.toDTO(entity);
                    dto.setUserId(entity.getId().getUserId());
                    dto.setActivityId(entity.getId().getActivityId());
                    return dto;
                })
                .toList();
    }

    @Override
    public ActivityTrackingDTO create(ActivityTrackingCreateDTO createDTO) {
        Users user = usersRepository.findById(createDTO.getUserId())
                .orElseThrow(()-> new EntityNotFoundException("找不到使用者"));
        Activity activity = activityRepository.findById(createDTO.getActivityId())
                .orElseThrow(() -> new EntityNotFoundException("找不到活動"));

        // 由後端存取現在時間
        createDTO.setTrackingTime(Timestamp.from(Instant.now()));

        ActivityTracking entity = mapper.toEntity(createDTO);
        entity.setUser(user);
        entity.setActivity(activity);
        entity.setId(new ActivityTrackingId(activity.getActivityId(), user.getUserId()));

        activityTrackingRepository.save(entity);

        ActivityTrackingDTO dto = mapper.toDTO(entity);
        dto.setActivityId(entity.getId().getActivityId());
        dto.setUserId(entity.getId().getUserId());
        return dto;
    }

    @Override
    public ActivityTrackingDTO update(ActivityTrackingId id, ActivityTrackingUpdateDTO updateDTO) {
        ActivityTracking entity = activityTrackingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("查無資料"));

        mapper.updateEntityFromDto(updateDTO, entity);

        ActivityTracking updated = activityTrackingRepository.save(entity);

        ActivityTrackingDTO dto = mapper.toDTO(updated);
        dto.setUserId(updated.getId().getUserId());
        dto.setActivityId(updated.getId().getActivityId());
        return dto;
    }

    @Override
    public void delete(ActivityTrackingId id) {
        boolean exists = activityTrackingRepository.existsById(id);
        if(!exists) {
            throw new EntityNotFoundException("查無資料");
        }
        activityTrackingRepository.deleteById(id);
    }

    // 追蹤活動
    @Override
    @Transactional
    public void createTracking(ActivityTrackingCreateDTO createDTO) {

        Integer activityId = createDTO.getActivityId();
        Integer userId = createDTO.getUserId();

        // 判斷是否存在活動追蹤紀錄
        Optional<ActivityTracking> activityTrackingState = activityTrackingRepository.findById(new ActivityTrackingId(activityId, userId));


        if(activityTrackingState.isPresent()) {

            // 存在
            ActivityTracking activityTracking = activityTrackingState.get();
            activityTracking.setTrackingState((byte) 0); // 0 - 正在追蹤
            activityTracking.setTrackingTime(Timestamp.from(Instant.now()));

            activityTrackingRepository.save(activityTracking);
        } else {

            // 不存在
            create(createDTO);
        }
    }

    // 取消追蹤活動
    @Override
    @Transactional
    public void unTracking(Integer userId, Integer activityId) {

        ActivityTracking activityTracking = activityTrackingRepository.findById(new ActivityTrackingId(activityId, userId))
                .orElseThrow(() -> new EntityNotFoundException("查無追蹤紀錄"));

        activityTracking.setTrackingState((byte) 1); // 1 - 取消追蹤

        activityTrackingRepository.save(activityTracking);

    }

    // 使用者是否收藏了活動
    @Override
    public boolean isTracking(Integer userId, Integer activityId) {

        Optional<ActivityTracking> activityTracking = activityTrackingRepository.findById(new ActivityTrackingId(activityId, userId));
        if(activityTracking.isEmpty()) {
            return false;
        } else {
            ActivityTracking tracking = activityTracking.get();
            if(tracking.getTrackingState() == 1) {
                return false;
            }
            return true;
        }
    }







}
