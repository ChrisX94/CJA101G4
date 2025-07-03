package com.shakemate.casetype.service;

import com.shakemate.casetype.dto.CaseTypeDTO;
import com.shakemate.casetype.mapper.CaseTypeMapper;
import com.shakemate.casetype.model.CaseTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CaseTypeService {

    private final CaseTypeRepository repository;

    public CaseTypeService(CaseTypeRepository repository) {
        this.repository = repository;
    }

    public List<CaseTypeDTO> listAll() {
        return repository.findAll()
                .stream()
                .map(CaseTypeMapper::toDTO)
                .collect(Collectors.toList());
    }

    public String buildCategoryPrompt() {
        List<CaseTypeDTO> types = listAll();
        StringBuilder sb = new StringBuilder("可用問題類別如下：\n");
        for (CaseTypeDTO type : types) {
            sb.append("- ")
              .append(type.getTypeName())
              .append("：")
              .append(type.getTypeDesc())
              .append("\n");
        }
        return sb.toString();
    }
}
