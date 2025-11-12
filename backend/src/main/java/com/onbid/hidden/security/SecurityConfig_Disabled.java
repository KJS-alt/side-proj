package com.onbid.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Spring Security 설정 (비활성화 - 로그인 기능 미사용)
 * 메인 com.onbid.config.SecurityConfig를 사용
 */
//@Configuration
//@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig_Disabled {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    /**
     * 비밀번호 암호화 Bean
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * CORS 설정
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 프론트엔드 URL 허용
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        
        // 모든 HTTP 메서드 허용
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        
        // 모든 헤더 허용
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // 인증 정보(쿠키, Authorization 헤더 등) 포함 허용
        configuration.setAllowCredentials(true);
        
        // Preflight 요청 캐시 시간 (1시간)
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
    
    /**
     * Security Filter Chain 설정
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CORS 활성화
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                
                // CSRF 비활성화 (JWT 사용으로 불필요)
                .csrf(AbstractHttpConfigurer::disable)
                
                // 세션 사용 안함 (Stateless)
                .sessionManagement(session -> 
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                // 요청 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // ===== 인증 불필요 (공개 API) =====
                        // 회원가입, 로그인
                        .requestMatchers("/api/users/register", "/api/users/login").permitAll()
                        
                        // 공매물건 조회 (공개 API)
                        .requestMatchers("/api/goods/**").permitAll()
                        
                        // 구매 API (공개 API)
                        .requestMatchers("/api/purchases/**").permitAll()
                        
                        // Swagger UI 접근 허용
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        
                        // ===== 인증 필요 =====
                        // 나머지 /api/** (관심물건, 내 정보 등)
                        .requestMatchers("/api/**").authenticated()
                        
                        // 기타 모든 요청은 허용
                        .anyRequest().permitAll()
                )
                
                // JWT 인증 필터 추가
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}

