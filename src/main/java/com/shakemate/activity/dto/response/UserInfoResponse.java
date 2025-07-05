package com.shakemate.activity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {

    private Integer userId;

    private String userName;

    private String userAccName;

    private String userImgUrl;

    private String userIntro;

}
