package com.shakemate.activity.mapper;

import com.shakemate.activity.dto.ActivityTrackingDTO;
import com.shakemate.activity.dto.request.ActivityTrackingCreateDTO;
import com.shakemate.activity.dto.request.ActivityTrackingUpdateDTO;
import com.shakemate.activity.entity.ActivityTracking;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ActivityTrackingMapper {

    @Mapping(target = "activityId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    ActivityTrackingDTO toDTO(ActivityTracking entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activity", ignore = true)
    @Mapping(target = "user", ignore = true)
    ActivityTracking toEntity(ActivityTrackingCreateDTO createDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ActivityTrackingUpdateDTO updateDTO, @MappingTarget ActivityTracking entity);



}
