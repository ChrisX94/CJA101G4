package com.shakemate.activity.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingRequest {

    private Integer activityId;
    private Integer userId;
    private Byte rating;
    private String reviewContent;
//    private Timestamp reviewTime;
}
