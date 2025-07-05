package com.shakemate.activity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReplyCommentResponse {

    private Integer commentId;
    private Integer parentCommentId;
    private Integer userId;
    private String userName;
    private String userAccName;
    private String userImgUrl;
    private String userIntro;


    private String content;
    private Timestamp commentTime;

}
