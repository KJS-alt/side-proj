package com.onbid.service;

import com.onbid.domain.User;
import com.onbid.dto.*;
import com.onbid.mapper.UserMapper;
import com.onbid.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    
    /**
     * 회원가입
     * @param request 회원가입 요청 정보
     * @return 등록된 사용자 정보
     */
    @Transactional
    public UserResponse register(RegisterRequest request) {
        // 이메일 중복 체크
        if (userMapper.existsByEmail(request.getEmail()) > 0) {
            throw new RuntimeException("이미 사용중인 이메일입니다: " + request.getEmail());
        }
        
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        
        // User 엔티티 생성 및 저장
        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .username(request.getUsername())
                .build();
        
        userMapper.insert(user);
        
        log.info("회원가입 완료: userId={}, email={}", user.getId(), user.getEmail());
        
        return UserResponse.from(user);
    }
    
    /**
     * 로그인
     * @param request 로그인 요청 정보
     * @return JWT 토큰 및 사용자 정보
     */
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        // 이메일로 사용자 조회
        User user = userMapper.findByEmail(request.getEmail());
        if (user == null) {
            throw new RuntimeException("존재하지 않는 사용자입니다");
        }
        
        // 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다");
        }
        
        // JWT 토큰 생성
        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail());
        
        log.info("로그인 성공: userId={}, email={}", user.getId(), user.getEmail());
        
        return LoginResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
    
    /**
     * 사용자 정보 조회
     * @param userId 사용자 ID
     * @return 사용자 정보
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("존재하지 않는 사용자입니다: userId=" + userId);
        }
        return UserResponse.from(user);
    }
    
    /**
     * 이메일로 사용자 조회
     * @param email 이메일
     * @return 사용자 정보
     */
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userMapper.findByEmail(email);
    }
    
    /**
     * 사용자 정보 수정
     * @param userId 사용자 ID
     * @param request 수정 요청 정보
     * @return 수정된 사용자 정보
     */
    @Transactional
    public UserResponse updateUser(Long userId, UpdateUserRequest request) {
        // 사용자 조회
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("존재하지 않는 사용자입니다");
        }
        
        // 사용자명 수정
        if (request.getUsername() != null && !request.getUsername().trim().isEmpty()) {
            user.setUsername(request.getUsername());
        }
        
        // 비밀번호 수정
        if (request.getNewPassword() != null && !request.getNewPassword().trim().isEmpty()) {
            // 현재 비밀번호 확인
            if (request.getCurrentPassword() == null || request.getCurrentPassword().trim().isEmpty()) {
                throw new RuntimeException("현재 비밀번호를 입력해주세요");
            }
            
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new RuntimeException("현재 비밀번호가 일치하지 않습니다");
            }
            
            // 새 비밀번호 암호화 및 저장
            String encodedPassword = passwordEncoder.encode(request.getNewPassword());
            user.setPassword(encodedPassword);
        }
        
        // 업데이트
        userMapper.update(user);
        
        log.info("사용자 정보 수정 완료: userId={}", userId);
        
        return UserResponse.from(user);
    }
    
    /**
     * 회원 탈퇴
     * @param userId 사용자 ID
     * @param request 탈퇴 요청 정보 (비밀번호 확인)
     */
    @Transactional
    public void deleteUser(Long userId, DeleteUserRequest request) {
        // 사용자 조회
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("존재하지 않는 사용자입니다");
        }
        
        // 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다");
        }
        
        // 사용자 삭제 (관심물건도 CASCADE로 자동 삭제됨)
        userMapper.delete(userId);
        
        log.info("회원 탈퇴 완료: userId={}", userId);
    }
}

