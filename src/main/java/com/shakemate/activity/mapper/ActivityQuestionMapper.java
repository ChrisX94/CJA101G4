package com.shakemate.activity.mapper;

import com.shakemate.activity.dto.ActivityQuestionDTO;
import com.shakemate.activity.dto.request.ActivityQuestionCreateDTO;
import com.shakemate.activity.dto.request.ActivityQuestionUpdateDTO;
import com.shakemate.activity.entity.ActivityQuestion;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ActivityQuestionMapper {

    @Mapping(source = "activity.activityId", target = "activityId")
    ActivityQuestionDTO toDto(ActivityQuestion entity);

    @Mapping(target = "activity", ignore = true)
    ActivityQuestion toEntity(ActivityQuestionDTO dto);

    @Mapping(target = "activity", ignore = true)
    ActivityQuestion toEntity(ActivityQuestionCreateDTO createDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "activity", ignore = true)
    void updateEntityFromDto(ActivityQuestionUpdateDTO updateDTO, @MappingTarget ActivityQuestion entity);
}
