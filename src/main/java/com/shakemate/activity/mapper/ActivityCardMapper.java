package com.shakemate.activity.mapper;

import com.shakemate.activity.dto.response.ActivityCardDTO;
import com.shakemate.activity.entity.Activity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ActivityCardMapper {

    @Mapping(target = "activityStatus", ignore = true)
    @Mapping(target = "participantStatus", ignore = true)
    ActivityCardDTO toCardDTO(Activity activity);
}
