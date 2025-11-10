package com.onbid.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger (SpringDoc OpenAPI) 설정
 */
@Configuration
public class SwaggerConfig {
    
    /**
     * OpenAPI 문서 설정
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("온비드 공매물건 조회 시스템 API")
                        .description("한국자산관리공사 온비드 OpenAPI를 활용한 공매물건 조회 REST API")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Side Project Team")
                                .url("https://github.com/kjs-alt/side-proj"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html")))
                .components(new Components()
                        .addSecuritySchemes("bearer-token",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .in(SecurityScheme.In.HEADER)
                                        .name("Authorization")
                                        .description("JWT 토큰을 입력하세요 (Bearer 접두사는 자동으로 추가됩니다)")));
    }
}

