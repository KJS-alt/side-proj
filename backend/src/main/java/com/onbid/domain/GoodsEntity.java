package com.onbid.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 물건 엔티티 (DB 저장용)
 * API에서 조회한 물건 정보를 DB에 저장하기 위한 엔티티
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsEntity {
    
    /**
     * 물건 ID (Primary Key)
     */
    private Long id;
    
    /**
     * 물건이력번호 (회차별 고유 식별자)
     */
    private Long historyNo;
    
    /**
     * 물건명
     */
    private String goodsName;
    
    /**
     * 최저입찰가
     */
    private Long minBidPrice;
    
    /**
     * 감정가 (평가액)
     */
    private Long appraisalPrice;
    
    /**
     * 입찰마감일시 (YYYYMMDDHHmmss)
     */
    private String bidCloseDate;
    
    /**
     * 물건소재지
     */
    private String address;
    
    /**
     * 생성일시
     */
    private LocalDateTime createdAt;
    
    /**
     * 수정일시
     */
    private LocalDateTime updatedAt;
}

