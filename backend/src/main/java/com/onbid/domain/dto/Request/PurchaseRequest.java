package com.onbid.domain.dto.Request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 구매 요청 DTO
 * 클라이언트에서 구매 요청 시 사용
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseRequest {
    
    /**
     * 물건이력번호
     */
    @NotNull(message = "물건이력번호는 필수입니다.")
    private Long historyNo;
    
    /**
     * 구매가격
     */
    @NotNull(message = "구매가격은 필수입니다.")
    private Long purchasePrice;
}

