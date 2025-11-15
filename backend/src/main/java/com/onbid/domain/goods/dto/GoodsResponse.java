package com.onbid.domain.goods.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 온비드 API 응답 래퍼
 * XML 응답 전체 구조를 파싱하기 위한 클래스
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoodsResponse {
    
    /**
     * 응답 헤더
     */
    @XmlElement(name = "header")
    private Header header;
    
    /**
     * 응답 바디
     */
    @XmlElement(name = "body")
    private Body body;
    
    /**
     * 응답 헤더 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @XmlAccessorType(XmlAccessType.FIELD)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Header {
        /**
         * 결과 코드 (00: 정상)
         */
        @XmlElement(name = "resultCode")
        private String resultCode;
        
        /**
         * 결과 메시지
         */
        @XmlElement(name = "resultMsg")
        private String resultMsg;
    }
    
    /**
     * 응답 바디 정보
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @XmlAccessorType(XmlAccessType.FIELD)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Body {
        /**
         * 물건 목록
         */
        @XmlElementWrapper(name = "items")
        @XmlElement(name = "item")
        private List<Goods> items;
        
        /**
         * 한 페이지에 표시할 결과 수
         */
        @XmlElement(name = "numOfRows")
        private Integer numOfRows;
        
        /**
         * 페이지 번호
         */
        @XmlElement(name = "pageNo")
        private Integer pageNo;
        
        /**
         * 전체 결과 수
         */
        @XmlElement(name = "totalCount")
        private Integer totalCount;
    }
}

