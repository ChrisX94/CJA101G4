package com.shakemate.activity.service;

import com.shakemate.activity.dto.ActivityCommentDTO;
import com.shakemate.activity.dto.request.ActivityCommentCreateDTO;
import com.shakemate.activity.dto.request.ActivityCommentUpdateDTO;
import com.shakemate.activity.dto.response.ActivityCommentResponse;
import com.shakemate.activity.dto.response.ReplyCommentResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ActivityCommentService {

    ActivityCommentDTO getById(Integer id);
    List<ActivityCommentDTO> getAll();
    ActivityCommentDTO create(ActivityCommentCreateDTO createDTO);
    ActivityCommentDTO update(Integer id, ActivityCommentUpdateDTO updateDTO);
    void delete(Integer id);

    Page<ActivityCommentResponse> getParentCommentsWithPreviewReplies(Integer activityId, int page, int size);
    Page<ReplyCommentResponse> getReply(Integer commentId, int page, int size);

}
