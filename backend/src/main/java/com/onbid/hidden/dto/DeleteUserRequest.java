package com.onbid.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 회원 탈퇴 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteUserRequest {
    
    /**
     * 비밀번호 확인 (필수)
     */
    @NotBlank(message = "비밀번호를 입력해주세요")
    private String password;
}

