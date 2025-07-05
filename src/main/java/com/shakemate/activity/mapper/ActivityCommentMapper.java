package com.shakemate.activity.mapper;

import com.shakemate.activity.dto.ActivityCommentDTO;
import com.shakemate.activity.dto.request.ActivityCommentCreateDTO;
import com.shakemate.activity.dto.request.ActivityCommentUpdateDTO;
import com.shakemate.activity.entity.ActivityComment;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ActivityCommentMapper {

    @Mapping(source = "activity.activityId", target = "activityId")
    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "parentComment.commentId", target = "parentCommentId")
    ActivityCommentDTO toDto(ActivityComment entity);

    @Mapping(target = "activity", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "parentComment", ignore = true)
    ActivityComment toEntity(ActivityCommentDTO dto);


    @Mapping(target = "activity", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "parentComment", ignore = true)
    ActivityComment toEntity(ActivityCommentCreateDTO createDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "activity", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "parentComment", ignore = true)
    void updateEntityFromDto(ActivityCommentUpdateDTO updateDTO, @MappingTarget ActivityComment entity);

}
