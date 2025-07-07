package com.shakemate.activity.service.impl;

import com.shakemate.activity.dto.ActivityReportDTO;
import com.shakemate.activity.dto.request.ActivityReportCreateDTO;
import com.shakemate.activity.dto.request.ActivityReportUpdateDTO;
import com.shakemate.activity.entity.Activity;
import com.shakemate.activity.entity.ActivityReport;
import com.shakemate.activity.mapper.ActivityReportMapper;
import com.shakemate.activity.repository.ActivityReportRepository;
import com.shakemate.activity.repository.ActivityRepository;
import com.shakemate.activity.service.ActivityReportService;
import com.shakemate.adm.model.AdmRepository;
import com.shakemate.adm.model.AdmVO;
import com.shakemate.user.dao.UsersRepository;
import com.shakemate.user.model.Users;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ActivityReportServiceImpl implements ActivityReportService {

    private final ActivityReportRepository activityReportRepository;
    private final ActivityRepository activityRepository;
    private final UsersRepository userRepository;
    private final AdmRepository admRepository;
    private final ActivityReportMapper activityReportMapper;

    @Override
    public ActivityReportDTO getById(Integer id) {
        ActivityReport activityReport = activityReportRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException("找不到該活動檢舉"));
        return activityReportMapper.toDto(activityReport);
    }

    @Override
    public List<ActivityReportDTO> getAll() {
        List<ActivityReport> list = activityReportRepository.findAll();
        return list.stream()
                .map(activityReportMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ActivityReportDTO create(ActivityReportCreateDTO createDTO) {

        // craeteDTO -> Entity
        ActivityReport activityReport = activityReportMapper.toEntity(createDTO);

        // activity
        Activity activity = activityRepository.findById(createDTO.getActivityId())
                .orElseThrow(() -> new EntityNotFoundException("找不到該活動"));
        activityReport.setActivity(activity);

        // user
        Users user = userRepository.findById(createDTO.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("找不到該使用者"));
        activityReport.setUser(user);

        // adm
        AdmVO adm = admRepository.findById(createDTO.getAdmId())
                .orElseThrow(() -> new EntityNotFoundException("找不到該管理員"));
        activityReport.setAdm(adm);

        // save
        ActivityReport saved = activityReportRepository.save(activityReport);

        // Entity -> DTO
        return activityReportMapper.toDto(saved);
    }

    @Override
    public ActivityReportDTO update(Integer id, ActivityReportUpdateDTO updateDTO) {
        ActivityReport entity = activityReportRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("查無資料"));

        // RP_CONTENT
        if(updateDTO.getRpContent() != null && updateDTO.getRpContent().trim().isEmpty()){
            throw new IllegalArgumentException("檢舉文字內容不可為空或只包含空白字元");
        }
        // ADM_ID
        if(updateDTO.getAdmId() != null) {
            if(!admRepository.existsById(updateDTO.getAdmId())){
                throw new IllegalArgumentException("修改格式錯誤-查無此管理員");
            }
        }

        activityReportMapper.updateEntityFromDto(updateDTO, entity);
        ActivityReport updated = activityReportRepository.save(entity);
        ActivityReportDTO dto = activityReportMapper.toDto(updated);

        return dto;

    }

    @Override
    public void delete(Integer id) {
        boolean exists = activityReportRepository.existsById(id);
        if(!exists) {
            throw new EntityNotFoundException("查無資料");
        }
        activityReportRepository.deleteById(id);
    }
}
