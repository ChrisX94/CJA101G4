package com.shakemate.activity.mapper;

import com.shakemate.activity.dto.ActivityParticipantDTO;
import com.shakemate.activity.dto.request.ActivityParticipantCreateDTO;
import com.shakemate.activity.dto.request.ActivityParticipantUpdateDTO;
import com.shakemate.activity.entity.ActivityParticipant;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ActivityParticipantMapper {

    @Mapping(target = "participantId", ignore = true)
    @Mapping(target = "activityId", ignore = true)
    ActivityParticipantDTO toDTO(ActivityParticipant entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "participant", ignore = true)
    @Mapping(target = "activity", ignore = true)
    ActivityParticipant toEntity(ActivityParticipantCreateDTO createDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "participant", ignore = true)
    @Mapping(target = "activity", ignore = true)
    void updateEntityFromDto(ActivityParticipantUpdateDTO updateDTO, @MappingTarget ActivityParticipant entity);





}
