package com.shakemate.servicecase.mapper;

import com.shakemate.servicecase.dto.ServiceCaseDTO;
import com.shakemate.servicecase.model.ServiceCase;

public class ServiceCaseMapper {
    public static ServiceCaseDTO toDTO(ServiceCase sc) {
        if (sc == null) return null;
        ServiceCaseDTO dto = new ServiceCaseDTO();
        dto.setCaseId(sc.getCaseId());
        dto.setUserId(sc.getUserId());
        dto.setAdmId(sc.getAdmId());
        dto.setCaseTypeId(sc.getCaseTypeId());
        if (sc.getCaseType() != null) {
            dto.setCaseTypeName(sc.getCaseType().getTypeName());
        }
        dto.setCreateTime(sc.getCreateTime());
        dto.setUpdateTime(sc.getUpdateTime());
        dto.setTitle(sc.getTitle());
        dto.setContent(sc.getContent());
        dto.setCaseStatus(sc.getCaseStatus());
        dto.setEmail(sc.getEmail());
        return dto;
    }

    public static ServiceCase toEntity(ServiceCaseDTO dto) {
        if (dto == null) return null;
        ServiceCase sc = new ServiceCase();
        sc.setCaseId(dto.getCaseId());
        sc.setUserId(dto.getUserId());
        sc.setAdmId(dto.getAdmId());
        sc.setCaseTypeId(dto.getCaseTypeId());
        sc.setTitle(dto.getTitle());
        sc.setContent(dto.getContent());
        sc.setCaseStatus(dto.getCaseStatus());
        sc.setEmail(dto.getEmail());
        return sc;
    }
}