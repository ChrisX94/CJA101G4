// src/main/java/com/shakemate/servicecase/mapper/ServiceCaseMapper.java
package com.shakemate.servicecase.mapper;

import com.shakemate.servicecase.model.ServiceCase;
import com.shakemate.casetype.model.CaseType;
import com.shakemate.servicecase.dto.ServiceCaseDTO;

public class ServiceCaseMapper {

    public static ServiceCaseDTO toDTO(ServiceCase sc) {
        if (sc == null) return null;
        ServiceCaseDTO dto = new ServiceCaseDTO();
        dto.setCaseId(sc.getCaseId());
        dto.setUserId(sc.getUserId());
        dto.setAdmId(sc.getAdmId());
        dto.setCaseTypeId(sc.getCaseTypeId());
        // 如果 caseType fetch 到物件，就拷貝名稱
        if (sc.getCaseType() != null) {
            dto.setCaseTypeName(sc.getCaseType().getTypeName());
        }
        dto.setCreateTime(sc.getCreateTime());
        dto.setUpdateTime(sc.getUpdateTime());
        dto.setTitle(sc.getTitle());
        dto.setContent(sc.getContent());
        dto.setCaseStatus(sc.getCaseStatus());
        return dto;
    }

    public static ServiceCase toEntity(ServiceCaseDTO dto) {
        if (dto == null) return null;
        ServiceCase sc = new ServiceCase();
        sc.setCaseId(dto.getCaseId());
        sc.setUserId(dto.getUserId());
        sc.setAdmId(dto.getAdmId());
        // 僅設定 FK，實際關聯物件可在 Service 裡 fetch
        CaseType ct = new CaseType();
        ct.setCaseTypeId(dto.getCaseTypeId());  // 這個 setter 在 CaseType 是 public
        
        sc.setCaseType(ct);
        sc.setTitle(dto.getTitle());
        sc.setContent(dto.getContent());
        sc.setCaseStatus(dto.getCaseStatus());
        return sc;
    }
}
