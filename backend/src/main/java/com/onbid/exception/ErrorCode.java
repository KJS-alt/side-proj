package com.onbid.exception;

import org.springframework.http.HttpStatus;

/**
 * 전역 에러 코드를 한 곳에서 관리하기 위한 열거형
 */
public enum ErrorCode {
    
    EXTERNAL_API_ERROR(HttpStatus.BAD_GATEWAY, "EXTERNAL_API_ERROR", "외부 온비드 API 호출 중 오류가 발생했습니다."),
    XML_PARSE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "XML_PARSE_ERROR", "온비드 XML 응답을 파싱하지 못했습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "요청 값이 올바르지 않습니다."),
    GOODS_NOT_FOUND(HttpStatus.NOT_FOUND, "GOODS_NOT_FOUND", "요청한 물건 정보를 찾을 수 없습니다."),
    DUPLICATED_PURCHASE(HttpStatus.CONFLICT, "DUPLICATED_PURCHASE", "이미 구매된 물건입니다."),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DATABASE_ERROR", "데이터베이스 처리 중 문제가 발생했습니다."),
    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "UNKNOWN_ERROR", "예상치 못한 서버 오류가 발생했습니다.");
    
    private final HttpStatus status;
    private final String code;
    private final String defaultMessage;
    
    ErrorCode(HttpStatus status, String code, String defaultMessage) {
        this.status = status;
        this.code = code;
        this.defaultMessage = defaultMessage;
    }
    
    public HttpStatus getStatus() {
        return status;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDefaultMessage() {
        return defaultMessage;
    }
}


