package com.onbid.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

/**
 * SPA 라우팅을 지원하기 위한 설정
 */
@Configuration
public class SpaRedirectConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new SpaFallbackResourceResolver());
    }

    /**
     * 정적 파일이 존재하지 않으면 index.html로 포워딩해 리액트 라우팅을 살려준다.
     */
    private static class SpaFallbackResourceResolver extends PathResourceResolver {
        @Override
        protected Resource getResource(String resourcePath, Resource location) throws IOException {
            Resource requestedResource = location.createRelative(resourcePath);

            // 실제 파일이 존재하면 그대로 반환한다.
            if (requestedResource.exists() && requestedResource.isReadable()) {
                return requestedResource;
            }

            // API 혹은 Swagger와 같은 백엔드 전용 경로는 SPA 포워딩에서 제외한다.
            if (resourcePath.startsWith("api/") || resourcePath.startsWith("swagger-ui")
                    || resourcePath.startsWith("v3/api-docs") || resourcePath.startsWith("swagger-resources")
                    || resourcePath.startsWith("webjars")) {
                return null;
            }

            // 위 조건에 걸리지 않으면 SPA 진입점으로 포워딩한다.
            return location.createRelative("index.html");
        }
    }
}

