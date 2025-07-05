package com.shakemate.activity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {

    private Integer activityId;

    private Integer hostId; // 主辦人id

    private String hostName; // 主辦人名稱

    private String hostAccName; // 主辦人帳號名稱

    private String hostImgUrl; // 主辦人頭貼url

    private String hostIntro; // 主辦人自我介紹

    private String title;

    private String imageUrl;

    private String location;

    private String content;


    private Timestamp createdTime;

    private Timestamp regStartTime;
    private Timestamp regEndTime;

    private Timestamp activStartTime;
    private Timestamp activEndTime;

    private Byte activityStatusCode; // 0, 報名尚未開始 1, 報名已開始 2. 報名已結束 3, 活動已開始 4, 活動已結束 5, 活動已取消/下架
    private String activityStatusLabel;


    private Integer signupCount; // 包含團主

    private Integer minPeople;
    private Integer maxPeople;

    private Integer commentCount;

    private Integer remainingSlots; // 剩餘名額

    private Integer peopleToFormGroup; // 距離成團人數 (可為負數）

    private Integer safePeopleToFormGroup; // 距離成團人數 (最小為0）



    private Byte participantStatusCode; // 0, 未參加 1, 申請中 2, 已參加
    private String participantStatusLabel;

}
