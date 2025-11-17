package com.onbid.domain.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 공매 기본 정보 엔티티
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsBasicEntity {

    private Long id;
    private Long historyNo;
    private String goodsName;
    private String statusName;
    private String saleTypeName;
    private String categoryName;
    private String bidStartDate;
    private String bidCloseDate;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

