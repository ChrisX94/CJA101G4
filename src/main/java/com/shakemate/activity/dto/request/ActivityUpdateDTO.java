package com.shakemate.activity.dto.request;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ActivityUpdateDTO {

    private String title;

    private String content;

    private String imageUrl;

    private String location;

    private Byte activityStatus;

    private Timestamp updatedTime;

    private Timestamp regStartTime;

    private Timestamp regEndTime;

    private Timestamp activStartTime;

    private Timestamp activEndTime;

    private Byte genderFilter;

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
