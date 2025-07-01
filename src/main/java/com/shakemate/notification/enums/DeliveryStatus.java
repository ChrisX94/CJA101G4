package com.shakemate.notification.enums;

import lombok.Getter;

@Getter
public enum DeliveryStatus {
    PENDING(0, "待處理"),
    SUCCESS(1, "成功"),
    FAILED(2, "失敗"),
    RETRYING(3, "重試中");

    private final int code;
    private final String description;

    DeliveryStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }
} 