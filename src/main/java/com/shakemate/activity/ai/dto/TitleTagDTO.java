package com.shakemate.activity.ai.dto;

import lombok.Data;

import java.util.List;

@Data
public class TitleTagDTO {
    private String title;
    private List<String> tags;
}
