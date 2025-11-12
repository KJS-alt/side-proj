package com.onbid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 로그인 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    
    /**
     * JWT 토큰
     */
    private String token;
    
    /**
     * 토큰 타입 (Bearer)
     */
    private String type;
    
    /**
     * 사용자 ID
     */
    private Long userId;
    
    /**
     * 사용자명
     */
    private String username;
    
    /**
     * 이메일
     */
    private String email;
}

