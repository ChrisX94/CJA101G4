package com.shakemate.activity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityCommentResponse {

    private Integer commentId;
    private Integer activityId;
    private Integer userId;
    private String userName;
    private String userAccName;
    private String userImgUrl;
    private String userIntro;

    private String content;
    private Timestamp commentTime;

    private Integer commentCount; // 回覆總數
    private List<ReplyCommentResponse> replies; // ⬅ 僅用於「預覽回覆」2 則

}
