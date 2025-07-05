package com.shakemate.casetype.mapper;

import com.shakemate.casetype.model.CaseType;
import com.shakemate.casetype.dto.CaseTypeDTO;
import java.util.List;
import java.util.stream.Collectors;

public class CaseTypeMapper {

    /**
     * 將 Entity 轉成 DTO（不包含 selectedCategory）
     */
    public static CaseTypeDTO toDTO(CaseType ct) {
        if (ct == null) return null;
        CaseTypeDTO dto = new CaseTypeDTO();
        dto.setCaseTypeId(ct.getCaseTypeId());
        dto.setTypeName(ct.getTypeName());
        dto.setTypeDesc(ct.getTypeDesc());
        return dto;
    }

    /**
     * 將 Entity 轉成 DTO，並設定使用者選擇的分類
     */
    public static CaseTypeDTO toDTO(CaseType ct, String selectedCategory) {
        CaseTypeDTO dto = toDTO(ct);
        dto.setSelectedCategory(selectedCategory);
        return dto;
    }

    /**
     * 批量轉換 Entity 清單為 DTO 清單，且設定相同的 selectedCategory
     */
    public static List<CaseTypeDTO> toDTOList(List<CaseType> entities, String selectedCategory) {
        return entities.stream()
                       .map(ct -> toDTO(ct, selectedCategory))
                       .collect(Collectors.toList());
    }

    /**
     * 將 DTO 轉成 Entity
     */
    public static CaseType toEntity(CaseTypeDTO dto) {
        if (dto == null) return null;
        CaseType ct = new CaseType();
        ct.setCaseTypeId(dto.getCaseTypeId());
        ct.setTypeName(dto.getTypeName());
        ct.setTypeDesc(dto.getTypeDesc());
        return ct;
    }
}
