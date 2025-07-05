package com.shakemate.activity.mapper;

import com.shakemate.activity.dto.ActivityDTO;
import com.shakemate.activity.dto.request.ActivityCreateDTO;
import com.shakemate.activity.dto.request.ActivityUpdateDTO;
import com.shakemate.activity.dto.response.ActivityCardDTO;
import com.shakemate.activity.entity.Activity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ActivityMapper {

    @Mapping(source = "user.userId", target = "userId")
    ActivityDTO toDTO(Activity entity);

    @Mapping(target = "user", ignore = true) // user 實體要 Service 補充設定
    Activity toEntity(ActivityDTO dto);

    /**
     * 把 dto 裡有值的欄位更新到 entity，null 不會覆蓋
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "user", ignore = true) // user 也不更新，Service 可自行更新
    void updateEntityFromDto(ActivityDTO dto, @MappingTarget Activity entity);

    // 新增：CreateDTO 轉 Entity
    @Mapping(target = "user", ignore = true) // Service 補 user
    Activity toEntity(ActivityCreateDTO createDTO);

    // 新增：UpdateDTO 轉 Entity(更新實體用)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "user", ignore = true)
    void updateEntityFromDto(ActivityUpdateDTO updateDTO, @MappingTarget Activity entity);


    // CardDTO
    ActivityCardDTO toCardDTO(Activity activity);
}