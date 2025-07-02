// src/main/java/com/shakemate/casetype/mapper/CaseTypeMapper.java
package com.shakemate.casetype.mapper;

import com.shakemate.casetype.model.CaseType;
import com.shakemate.casetype.dto.CaseTypeDTO;

public class CaseTypeMapper {

    public static CaseTypeDTO toDTO(CaseType ct) {
        if (ct == null) return null;
        CaseTypeDTO dto = new CaseTypeDTO();
        dto.setCaseTypeId(ct.getCaseTypeId());
        dto.setTypeName(ct.getTypeName());
        dto.setTypeDesc(ct.getTypeDesc());
        return dto;
    }

    public static CaseType toEntity(CaseTypeDTO dto) {
        if (dto == null) return null;
        CaseType ct = new CaseType();
        ct.setCaseTypeId(dto.getCaseTypeId());
        ct.setTypeName(dto.getTypeName());
        ct.setTypeDesc(dto.getTypeDesc());
        return ct;
    }
}
