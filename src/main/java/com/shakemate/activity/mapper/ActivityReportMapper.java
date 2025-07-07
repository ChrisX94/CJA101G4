package com.shakemate.activity.mapper;

import com.shakemate.activity.dto.ActivityReportDTO;
import com.shakemate.activity.dto.request.ActivityReportCreateDTO;
import com.shakemate.activity.dto.request.ActivityReportUpdateDTO;
import com.shakemate.activity.entity.ActivityReport;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ActivityReportMapper {

    @Mapping(source = "activity.activityId", target = "activityId")
    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "adm.admId", target = "admId")
    ActivityReportDTO toDto(ActivityReport entity);

    @Mapping(target = "activity", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "adm", ignore = true)
    ActivityReport toEntity(ActivityReportDTO dto);

    @Mapping(target = "activity", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "adm", ignore = true)
    ActivityReport toEntity(ActivityReportCreateDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "activity", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "adm", ignore = true)
    void updateEntityFromDto(ActivityReportUpdateDTO updateDTO, @MappingTarget ActivityReport entity);

}
