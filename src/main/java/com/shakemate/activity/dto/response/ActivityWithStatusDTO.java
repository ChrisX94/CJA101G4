package com.shakemate.activity.dto.response;

import com.shakemate.activity.dto.ActivityDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivityWithStatusDTO {
    private ActivityDTO activityDTO;
    private Integer parStatus;
}
