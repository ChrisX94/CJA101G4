package com.shakemate.activity.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActivityDTO {


    private Integer activityId;

    private Integer userId;      // 只保留 userId，避免整個 User 物件嵌套

    private String title;

    private String content;

    private String imageUrl;

    private String location;

    private Byte activityStatus = 0;


    private Timestamp createdTime;


    private Timestamp updatedTime;


    private Timestamp regStartTime;


    private Timestamp regEndTime;


    private Timestamp activStartTime;


    private Timestamp activEndTime;


    private Byte genderFilter = 0;


    private Integer maxAge;


    private Integer minAge;


    private Timestamp expiredTime;


    private Integer maxPeople;


    private Integer minPeople;


    private Integer signupCount;

    private Integer ratingCount;

    private Integer rating;

    private Integer commentCount;

    private Integer reportCount;




}
