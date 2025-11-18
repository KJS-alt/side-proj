package com.onbid.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 매매(구매) 엔티티
 * 물건에 대한 구매 정보를 저장
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseEntity {
    
    /**
     * 매매 ID (Primary Key)
     */
    private Long id;
    
    /**
     * 물건이력번호 (외래키)
     */
    private Long historyNo;
    
    /**
     * 구매가격
     */
    private Long purchasePrice;
    
    /**
     * 구매상태
     * PENDING: 대기중
     * COMPLETED: 완료
     * CANCELLED: 취소
     */
    private String purchaseStatus;
    
    /**
     * 구매일시
     */
    private LocalDateTime createdAt;
}



