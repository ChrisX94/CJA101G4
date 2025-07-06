package com.shakemate.activity.mapper;

import com.shakemate.activity.dto.ActivityAnswerDTO;
import com.shakemate.activity.dto.request.ActivityAnswerCreateDTO;
import com.shakemate.activity.dto.request.ActivityAnswerUpdateDTO;
import com.shakemate.activity.entity.ActivityAnswer;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ActivityAnswerMapper {

    @Mapping(source = "activity.activityId", target = "activityId")
    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "activityQuestion.questionId", target = "questionId")
    ActivityAnswerDTO toDto(ActivityAnswer entity);

    @Mapping(target = "activity", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "activityQuestion", ignore = true)
    ActivityAnswer toEntity(ActivityAnswerDTO dto);

    @Mapping(target = "activity", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "activityQuestion", ignore = true)
    ActivityAnswer toEntity(ActivityAnswerCreateDTO createDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "activity", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "activityQuestion", ignore = true)
    void updateEntityFromDto(ActivityAnswerUpdateDTO updateDTO, @MappingTarget ActivityAnswer entity);

}
