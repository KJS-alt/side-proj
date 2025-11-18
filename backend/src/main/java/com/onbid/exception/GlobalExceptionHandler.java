package com.onbid.exception;

import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 모든 REST 예외를 한 곳에서 표준 응답으로 변환하는 전역 핸들러
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * 비즈니스 예외 처리
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusiness(BusinessException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        Map<String, Object> body = buildErrorBody(errorCode.getCode(), ex.getMessage());
        return ResponseEntity.status(errorCode.getStatus()).body(body);
    }
    
    /**
     * DTO 검증 오류 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fieldError -> fieldError.getDefaultMessage())
                .orElse(ErrorCode.INVALID_REQUEST.getDefaultMessage());
        
        ErrorCode errorCode = ErrorCode.INVALID_REQUEST;
        Map<String, Object> body = buildErrorBody(errorCode.getCode(), message);
        return ResponseEntity.status(errorCode.getStatus()).body(body);
    }
    
    /**
     * 경로 변수/쿼리 파라미터 검증 오류 처리
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex) {
        ErrorCode errorCode = ErrorCode.INVALID_REQUEST;
        Map<String, Object> body = buildErrorBody(errorCode.getCode(), ex.getMessage());
        return ResponseEntity.status(errorCode.getStatus()).body(body);
    }
    
    /**
     * 그 외 알 수 없는 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
        log.error("예상치 못한 서버 오류", ex);
        ErrorCode errorCode = ErrorCode.UNKNOWN_ERROR;
        Map<String, Object> body = buildErrorBody(errorCode.getCode(), errorCode.getDefaultMessage());
        return ResponseEntity.status(errorCode.getStatus()).body(body);
    }
    
    /**
     * 표준 실패 응답 맵을 생성 (success=false 포함)
     */
    private Map<String, Object> buildErrorBody(String errorCode, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("success", false); // 실패 여부를 명시
        body.put("errorCode", errorCode);
        body.put("message", message);
        return body;
    }
}


