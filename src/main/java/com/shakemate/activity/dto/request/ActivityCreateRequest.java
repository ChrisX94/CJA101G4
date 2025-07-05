package com.shakemate.activity.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityCreateRequest {

    @NotNull(message = "用戶編號不可為空")
    private Integer userId;      // 只保留 userId，避免整個 User 物件嵌套

    @NotBlank(message = "活動名稱不可為空")
    private String title;

    @NotBlank(message = "活動內文不可為空")
    private String content;

    private String imageUrl;


    private String location;

    @NotNull(message = "活動狀態不可為空")
    private Byte activityStatus = 0;

//    @NotNull(message = "創建時間不能為空")
    private Timestamp createdTime;

//    @NotNull(message = "更新時間不能為空")
    private Timestamp updatedTime;

    @NotNull(message = "報名開始時間不能為空")
    private Timestamp regStartTime;

    @NotNull(message = "報名截止時間不能為空")
    private Timestamp regEndTime;

    @NotNull(message = "活動開始時間不能為空")
    private Timestamp activStartTime;

    @NotNull(message = "活動結束時間不能為空")
    private Timestamp activEndTime;

    @NotNull(message = "性別篩選不能為空")
    private Byte genderFilter = 0;

    @NotNull(message = "年齡上限不能為空")
    private Integer maxAge;

    @NotNull(message = "年齡下限不能為空")
    private Integer minAge;

    @NotNull(message = "申請過期時間不能為空")
    private Timestamp expiredTime;

    @NotNull(message = "人數上限不能為空")
    private Integer maxPeople;

    @NotNull(message = "人數下限不能為空")
    private Integer minPeople;

    @NotNull(message = "已報名人數不能為空")
    private Integer signupCount = 1;

    private Integer ratingCount = 0;
    private Integer rating = 0;

    private Integer commentCount = 0;
    private Integer reportCount = 0;

    private List<String> tagList = Collections.emptyList();
    private List<ActivityQuestionCreateRequest> questionList = Collections.emptyList();
}
