package com.onbid.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 온비드 공매물건 도메인 DTO (내부 로직에서만 사용하며 XML 의존성을 제거)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Goods {
    
    /**
     * 물건이력번호 (회차별 고유 식별자)
     */
    private Long historyNo;
    
    /**
     * 물건관리번호 (물건 식별자) - 예: 2025-09090-001
     */
    private String goodsNo;
    
    /**
     * 물건명 (전체 제목)
     */
    private String goodsName;
    
    /**
     * 물건상세명 (설명)
     */
    private String goodsDetail;
    
    /**
     * 감정가 (평가액)
     */
    private Long appraisalPrice;
    
    /**
     * 최저입찰가격 (입찰 시작가)
     */
    private Long minBidPrice;
    
    /**
     * 입찰시작일시 (YYYYMMDDHHmmss)
     */
    private String bidStartDate;
    
    /**
     * 입찰마감일시 (YYYYMMDDHHmmss)
     */
    private String bidCloseDate;
    
    /**
     * 공고번호
     */
    private String noticeNo;
    
    /**
     * 물건진행상태명 (예: 인터넷입찰진행중, 입찰준비중)
     */
    private String statusName;
    
    /**
     * 지번 주소
     */
    private String address;
    
    /**
     * 도로명 주소
     */
    private String roadAddress;
    
    /**
     * 매각구분코드 (0001: 매각, 0002: 임대)
     */
    private String saleTypeCode;
    
    /**
     * 매각구분명 (매각, 임대)
     */
    private String saleTypeName;
    
    /**
     * 입찰방법명 (예: 일반경쟁(최고가방식) / 총액)
     */
    private String bidMethodName;
    
    /**
     * 카테고리 전체명 (예: 주거용건물 / 다세대주택)
     */
    private String categoryName;
    
    /**
     * 조회수
     */
    private Integer inquiryCount;
    
    /**
     * 관심물건 등록 수
     */
    private Integer favoriteCount;
    
    /**
     * 비율 (예: (100%), (90%))
     */
    private String feeRate;
    
    /**
     * 물건 이미지 파일들
     */
    private String imageFiles;
}

