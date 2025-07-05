package com.shakemate.activity.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class ActivityCommentCreateDTO {

    @NotNull(message = "活動編號不可為空")
    private Integer activityId;            // 活動編號

    @NotNull(message = "用戶編號不可為空")
    private Integer userId;                // 使用者編號

    @NotBlank(message = "留言內容不可為空")
    private String content;                // 留言內容

    private Integer parentCommentId;       // 父留言（可為 null）


    private Timestamp commentTime;         // 留言時間（可選）

    private Integer commentCount = 0;
}
