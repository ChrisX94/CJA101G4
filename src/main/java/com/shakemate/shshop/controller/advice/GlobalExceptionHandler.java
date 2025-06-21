package com.shakemate.shshop.controller.advice;

import com.shakemate.shshop.dto.ApiResponse;
import com.shakemate.shshop.dto.ApiResponseFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = "com.shakemate.shshop.controller")
public class GlobalExceptionHandler {

    // 處理一般錯誤
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception e) {
        return ResponseEntity
                .status(500)
                .body(ApiResponseFactory.error(500, "系統錯誤：" + e.getMessage()));
    }

    // 處理 @Valid 驗證錯誤
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(field ->
                errors.put(field.getField(), field.getDefaultMessage())
        );

        return ResponseEntity
                .badRequest()
                .body(ApiResponseFactory.error(400, "validation_failed", errors));
    }
    // 處理404路徑錯誤
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<String> handle404(NoHandlerFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("找不到你要的資源 😢：請確認路徑是否正確！");
    }
}
