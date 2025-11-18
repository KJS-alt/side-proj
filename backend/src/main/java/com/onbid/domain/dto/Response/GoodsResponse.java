package com.onbid.domain.dto.Response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.onbid.domain.dto.Goods;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 온비드 API 응답을 내부 도메인 구조로 표현한 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoodsResponse {
    
    /**
     * 응답 헤더
     */
    private Header header;
    
    /**
     * 응답 바디
     */
    private Body body;
    
    /**
     * 응답 헤더 정보
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Header {
        /**
         * 결과 코드 (00: 정상)
         */
        private String resultCode;
        
        /**
         * 결과 메시지
         */
        private String resultMsg;
    }
    
    /**
     * 응답 바디 정보
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Body {
        /**
         * 물건 목록
         */
        private List<Goods> items;
        
        /**
         * 한 페이지에 표시할 결과 수
         */
        private Integer numOfRows;
        
        /**
         * 페이지 번호
         */
        private Integer pageNo;
        
        /**
         * 전체 결과 수
         */
        private Integer totalCount;
    }
}

