package com.onbid.dto;

import com.onbid.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자 정보 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    
    /**
     * 사용자 ID
     */
    private Long id;
    
    /**
     * 이메일
     */
    private String email;
    
    /**
     * 사용자명
     */
    private String username;
    
    /**
     * 생성일시
     */
    private LocalDateTime createdAt;
    
    /**
     * User 엔티티로부터 UserResponse 생성
     * @param user User 엔티티
     * @return UserResponse
     */
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .createdAt(user.getCreatedAt())
                .build();
    }
}

