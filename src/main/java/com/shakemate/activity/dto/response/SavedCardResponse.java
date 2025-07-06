package com.shakemate.activity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavedCardResponse {

    private Integer activityId;
    private String title;
    private String imageUrl;
    private String location;

    private Timestamp createdTime;

    private Timestamp regStartTime;
    private Timestamp regEndTime;

    private Timestamp activStartTime;
    private Timestamp activEndTime;

    private Integer minPeople;
    private Integer maxPeople;

    private Integer signupCount;

    private Integer remainingSlots;
    private Integer peopleToFormGroup;
    private Integer safePeopleToFormGroup;

    private ActivityStatusResponse status;

    private Timestamp trackingTime;


}
