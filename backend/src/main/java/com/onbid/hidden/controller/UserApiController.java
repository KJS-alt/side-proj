package com.onbid.controller;

import com.onbid.dto.*;
import com.onbid.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 사용자 REST API Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "사용자", description = "사용자 관련 API")
public class UserApiController {
    
    private final UserService userService;
    
    /**
     * 회원가입
     */
    @PostMapping("/register")
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("회원가입 요청: email={}", request.getEmail());
        
        try {
            UserResponse user = userService.register(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "회원가입이 완료되었습니다");
            response.put("user", user);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("회원가입 실패", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 로그인
     */
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하여 JWT 토큰을 발급받습니다")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        log.info("로그인 요청: email={}", request.getEmail());
        
        try {
            LoginResponse loginResponse = userService.login(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "로그인 성공");
            response.put("data", loginResponse);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("로그인 실패", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
    
    /**
     * 내 정보 조회
     */
    @GetMapping("/me")
    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다",
            security = @SecurityRequirement(name = "bearer-token"))
    public ResponseEntity<Map<String, Object>> getMyInfo(Authentication authentication) {
        try {
            // Authentication에서 userId 추출 (JwtAuthenticationFilter에서 설정)
            Long userId = (Long) authentication.getPrincipal();
            
            UserResponse user = userService.getUserById(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", user);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("내 정보 조회 실패", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 내 정보 수정
     */
    @PutMapping("/me")
    @Operation(summary = "내 정보 수정", description = "현재 로그인한 사용자의 정보를 수정합니다",
            security = @SecurityRequirement(name = "bearer-token"))
    public ResponseEntity<Map<String, Object>> updateMyInfo(
            @Valid @RequestBody UpdateUserRequest request,
            Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            
            UserResponse user = userService.updateUser(userId, request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "정보가 수정되었습니다");
            response.put("data", user);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("내 정보 수정 실패", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 회원 탈퇴
     */
    @DeleteMapping("/me")
    @Operation(summary = "회원 탈퇴", description = "현재 로그인한 사용자의 계정을 삭제합니다",
            security = @SecurityRequirement(name = "bearer-token"))
    public ResponseEntity<Map<String, Object>> deleteMyAccount(
            @Valid @RequestBody DeleteUserRequest request,
            Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            
            userService.deleteUser(userId, request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "회원 탈퇴가 완료되었습니다");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("회원 탈퇴 실패", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
}

