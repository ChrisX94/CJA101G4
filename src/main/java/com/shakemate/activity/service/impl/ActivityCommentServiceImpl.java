package com.shakemate.activity.service.impl;

import com.shakemate.activity.dto.ActivityCommentDTO;
import com.shakemate.activity.dto.request.ActivityCommentCreateDTO;
import com.shakemate.activity.dto.request.ActivityCommentUpdateDTO;
import com.shakemate.activity.dto.response.ActivityCommentResponse;
import com.shakemate.activity.dto.response.ReplyCommentResponse;
import com.shakemate.activity.entity.Activity;
import com.shakemate.activity.entity.ActivityComment;
import com.shakemate.activity.mapper.ActivityCommentMapper;
import com.shakemate.activity.repository.ActivityCommentRepository;
import com.shakemate.activity.repository.ActivityRepository;
import com.shakemate.activity.service.ActivityCommentService;
import com.shakemate.user.dao.UsersRepository;
import com.shakemate.user.model.Users;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ActivityCommentServiceImpl implements ActivityCommentService {

    private final ActivityCommentRepository activityCommentRepository;
    private final ActivityRepository activityRepository;
    private final UsersRepository userRepository;
    private final ActivityCommentMapper activityCommentMapper;

    @Override
    public ActivityCommentDTO getById(Integer id) {
        ActivityComment activityComment = activityCommentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("找不到該活動留言"));
        return activityCommentMapper.toDto(activityComment);
    }

    @Override
    public List<ActivityCommentDTO> getAll() {
        List<ActivityComment> list = activityCommentRepository.findAll();
        return list.stream()
                .map(activityCommentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ActivityCommentDTO create(ActivityCommentCreateDTO createDTO) {

        // createDTO -> Entity
        ActivityComment activityComment = activityCommentMapper.toEntity(createDTO);

        // activity
        Activity activity = activityRepository.findById(createDTO.getActivityId())
                .orElseThrow(() -> new EntityNotFoundException("找不到該活動"));
        activityComment.setActivity(activity);
        // user
        Users user = userRepository.findById(createDTO.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("找不到該使用者"));
        activityComment.setUser(user);

        ActivityComment parentComment = null;
        // parentComment
        if(createDTO.getParentCommentId() != null) {
            parentComment = activityCommentRepository.findById(createDTO.getParentCommentId())
                    .orElseThrow(() -> new EntityNotFoundException("找不到該父留言"));
            parentComment.setCommentCount(parentComment.getCommentCount() + 1);
            activityCommentRepository.save(parentComment);
        }
        activityComment.setParentComment(parentComment);
        activityComment.setCommentCount(0);
        activityComment.setCommentTime(Timestamp.from(Instant.now()));
        activity.setCommentCount(activity.getCommentCount() + 1);

        // save
        ActivityComment saved = activityCommentRepository.save(activityComment);
        activityRepository.save(activity);

        // Entity -> DTO
        return activityCommentMapper.toDto(saved);


    }

    @Override
    public ActivityCommentDTO update(Integer id, ActivityCommentUpdateDTO updateDTO) {
        ActivityComment entity = activityCommentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("查無資料"));

        if(updateDTO.getContent() != null && updateDTO.getContent().trim().isEmpty()){
            // throw...
            throw new IllegalArgumentException("留言內容不可為空或只包含空白字元");
        }

        activityCommentMapper.updateEntityFromDto(updateDTO, entity);

        ActivityComment parentComment = null;
        // parentComment
        if(updateDTO.getParentCommentId() != null) {
            parentComment = activityCommentRepository.findById(updateDTO.getParentCommentId())
                    .orElseThrow(() -> new EntityNotFoundException("找不到該父留言"));
        }
        entity.setParentComment(parentComment);


        ActivityComment updated = activityCommentRepository.save(entity);
        ActivityCommentDTO dto = activityCommentMapper.toDto(updated);

        return dto;

    }

    @Override
    public void delete(Integer id) {
        boolean exists = activityCommentRepository.existsById(id);
        if(!exists) {
            throw new EntityNotFoundException("查無資料");
        }
        activityCommentRepository.deleteById(id);
    }


    // 預設Preview兩筆，若有需求可再擴充增加參數previewNumber
    @Override
    public Page<ActivityCommentResponse> getParentCommentsWithPreviewReplies(Integer activityId, int page, int size) {

        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new EntityNotFoundException("查無活動"));

        // 找到該活動所有留言
        List<ActivityComment> activityCommentList = activity.getActivityComments();
        List<ActivityCommentResponse> resultList = new ArrayList<>();
        for(ActivityComment activityComment : activityCommentList) {

            // 或不存在?
            if(activityComment.getParentComment() != null) {
                continue;
            }

            ActivityCommentResponse activityCommentResponse = new ActivityCommentResponse();
            activityCommentResponse.setCommentId(activityComment.getCommentId());
            activityCommentResponse.setActivityId(activityId);

            Users user = activityComment.getUser();
            Integer userId = user.getUserId();
            String userName = user.getUsername();
            String userEmail = user.getEmail();
            String accName = "User";
            if (userEmail != null && userEmail.contains("@")) {
                accName =  userEmail.substring(0, userEmail.indexOf("@"));
            } else {
                accName = "User";
            }
            String imgUrl = user.getImg1();
            String intro = user.getIntro();

            activityCommentResponse.setUserId(userId);
            activityCommentResponse.setUserName(userName);
            activityCommentResponse.setUserAccName(accName);
            activityCommentResponse.setUserImgUrl(imgUrl);
            activityCommentResponse.setUserIntro(intro);

            activityCommentResponse.setContent(activityComment.getContent());
            activityCommentResponse.setCommentTime(activityComment.getCommentTime());

            activityCommentResponse.setCommentCount(activityComment.getCommentCount());

            // 獲取子留言
            List<ActivityComment> allReplies = activityComment.getActivityComments()
                    .stream()
                    .sorted(Comparator.comparing(ActivityComment::getCommentTime).reversed()) // 遞增（最舊在前）
                    .toList();

            List<ReplyCommentResponse>  previewReplies = new ArrayList<>();
            for(int i = 0; i < Math.min(2, allReplies.size()); i++) {
                ActivityComment subActivityComment = allReplies.get(i);
                ReplyCommentResponse replyCommentResponse = new ReplyCommentResponse();

                replyCommentResponse.setCommentId(subActivityComment.getCommentId());
                replyCommentResponse.setParentCommentId(subActivityComment.getParentComment().getCommentId());

                Users rUser = subActivityComment.getUser();

                String email = rUser.getEmail();
                String userAccName = "User";
                if (email != null && email.contains("@")) {
                    userAccName =  email.substring(0, email.indexOf("@"));
                } else {
                    userAccName = "User";
                }

                replyCommentResponse.setUserId(rUser.getUserId());
                replyCommentResponse.setUserName(rUser.getUsername());
                replyCommentResponse.setUserAccName(userAccName);
                replyCommentResponse.setUserImgUrl(rUser.getImg1());
                replyCommentResponse.setUserIntro(rUser.getIntro());

                replyCommentResponse.setContent(subActivityComment.getContent());
                replyCommentResponse.setCommentTime(subActivityComment.getCommentTime());

                previewReplies.add(replyCommentResponse);
            }

            activityCommentResponse.setReplies(previewReplies);

            resultList.add(activityCommentResponse);


        }

        // 按時間排序
        resultList.sort(Comparator.comparing(ActivityCommentResponse::getCommentTime).reversed()); // 遞增


        int start = page * size;
        int end = Math.min(start + size, resultList.size());
        Pageable pageable = PageRequest.of(page, size);

        if (start >= resultList.size()) {
            return new PageImpl<>(Collections.emptyList(), pageable, resultList.size());
        }


        List<ActivityCommentResponse> pageList = resultList.subList(start, end);



        return new PageImpl<>(pageList, pageable, resultList.size());


    }

    // 取得某留言的回覆
    @Override
    public Page<ReplyCommentResponse> getReply(Integer commentId, int page, int size) {

        ActivityComment activityComment = activityCommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("查無留言"));

        List<ActivityComment> replyList = activityComment.getActivityComments();
        List<ReplyCommentResponse> responseList = new ArrayList<>();
        for(ActivityComment reply : replyList) {

            ReplyCommentResponse response = new ReplyCommentResponse();
            response.setCommentId(reply.getCommentId());
            response.setParentCommentId(activityComment.getCommentId());


            Users user = reply.getUser();

            response.setUserId(user.getUserId());
            response.setUserName(user.getUsername());

            String userEmail = user.getEmail();
            String accName = "User";
            if (userEmail != null && userEmail.contains("@")) {
                accName =  userEmail.substring(0, userEmail.indexOf("@"));
            } else {
                accName = "User";
            }

            response.setUserAccName(accName);
            response.setUserImgUrl(user.getImg1());
            response.setUserIntro(user.getIntro());

            response.setContent(reply.getContent());
            response.setCommentTime(reply.getCommentTime());

            responseList.add(response);
        }

        // 按時間排序 （也可以更早排序）
        responseList.sort(Comparator.comparing(ReplyCommentResponse::getCommentTime).reversed()); // 遞增


        int start = page * size;
        int end = Math.min(start + size, responseList.size());
        Pageable pageable = PageRequest.of(page, size);

        if (start >= responseList.size()) {
            return new PageImpl<>(Collections.emptyList(), pageable, responseList.size());
        }


        List<ReplyCommentResponse> pageList = responseList.subList(start, end);



        return new PageImpl<>(pageList, pageable, responseList.size());


    }



}
