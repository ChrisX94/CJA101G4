package com.shakemate.shshop.controller.advice;

import com.shakemate.shshop.dto.ApiResponse;
import com.shakemate.shshop.dto.ApiResponseFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

import com.shakemate.notification.exception.ResourceNotFoundException;
@Component("shshopExceptionHandler")
@RestControllerAdvice(basePackages = {"com.shakemate.shshop.controller", "com.shakemate.notification.controller", "com.shakemate.ordermaster.controller"})
public class GlobalExceptionHandler {

    /**
     * 處理資源未找到異常
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseFactory.error(404, ex.getMessage()));
    }

    /**
     * 處理404錯誤
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseFactory.error(404, "請求的API路徑不存在: " + ex.getRequestURL()));
    }

    /**
     * 處理驗證異常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseFactory.error(400, "輸入驗證失敗", errors));
    }

    /**
     * 處理一般異常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseFactory.error(500, ex.getMessage()));
    }
}
