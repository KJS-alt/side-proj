package com.onbid.domain.goods.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 공매 가격/통계 엔티티
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsPriceEntity {

    private Long id;
    private Long historyNo;
    private Long minBidPrice;
    private Long appraisalPrice;
    private String feeRate;
    private Integer inquiryCount;
    private Integer favoriteCount;
    private LocalDateTime updatedAt;
}

