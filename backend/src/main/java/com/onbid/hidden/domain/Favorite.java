package com.onbid.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 관심물건 엔티티
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Favorite {
    
    /**
     * 관심물건 ID (PK)
     */
    private Long id;
    
    /**
     * 사용자 ID (FK)
     */
    private Long userId;
    
    /**
     * 물건이력번호 (회차별 고유 식별자)
     */
    private Long historyNo;
    
    /**
     * 물건관리번호 (온비드 API의 물건 고유번호)
     */
    private String goodsNo;
    
    /**
     * 물건명
     */
    private String goodsName;
    
    /**
     * 최저입찰가
     */
    private Long minBidPrice;
    
    /**
     * 입찰마감일시 (YYYYMMDDHHmmss 형식)
     */
    private String bidCloseDate;
    
    /**
     * 등록일시
     */
    private LocalDateTime createdAt;
}

