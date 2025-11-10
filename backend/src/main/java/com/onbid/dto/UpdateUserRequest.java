package com.onbid.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 사용자 정보 수정 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    
    /**
     * 사용자명 (선택)
     */
    @Size(min = 2, max = 50, message = "사용자명은 2자 이상 50자 이하여야 합니다")
    private String username;
    
    /**
     * 현재 비밀번호 (비밀번호 변경 시 필수)
     */
    private String currentPassword;
    
    /**
     * 새 비밀번호 (선택)
     */
    @Size(min = 6, message = "비밀번호는 6자 이상이어야 합니다")
    private String newPassword;
}

