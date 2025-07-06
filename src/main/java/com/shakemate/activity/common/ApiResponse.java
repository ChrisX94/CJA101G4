package com.shakemate.activity.common;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private int code;         // 例如 200、400、500
    private String message;   // 錯誤或成功訊息
    private T data;           // 回傳資料，泛型

    // 建構子
    public ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // 成功時使用
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "success", data);
    }

    // 失敗時使用
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }



}
