package com.onbid.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자 엔티티
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    /**
     * 사용자 ID (PK)
     */
    private Long id;
    
    /**
     * 이메일 (로그인용, 유니크)
     */
    private String email;
    
    /**
     * 암호화된 비밀번호 (BCrypt)
     */
    private String password;
    
    /**
     * 사용자명
     */
    private String username;
    
    /**
     * 생성일시
     */
    private LocalDateTime createdAt;
    
    /**
     * 수정일시
     */
    private LocalDateTime updatedAt;
}

