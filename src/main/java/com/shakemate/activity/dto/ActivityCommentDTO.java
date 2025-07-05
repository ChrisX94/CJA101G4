package com.shakemate.activity.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ActivityCommentDTO {

    private Integer commentId;             // 留言編號
    private Integer activityId;            // 活動編號
    private Integer userId;                // 使用者編號
    private String content;                // 留言內容
    private Integer parentCommentId;       // 父留言（可為 null）
    private Timestamp commentTime;         // 留言時間（可選）
    private Integer commentCount;          // 子留言數（通常新增時不填）
}