package com.onbid.domain.dto.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 간단 물건 목록 조회용 DTO (네 개 필드만 사용)
 */
@Data
public class GoodsItemsRequest {

    @Schema(description = "페이지 번호", defaultValue = "1")
    @Min(value = 1, message = "페이지 번호는 1 이상이어야 합니다.")
    private int pageNo = 1; // 페이지 번호

    @Schema(description = "페이지당 건수", defaultValue = "10")
    @Min(value = 1, message = "페이지당 건수는 1 이상이어야 합니다.")
    private int numOfRows = 10; // 페이지당 건수

    @Schema(description = "카테고리 ID")
    private String ctgrHirkId; // 카테고리 ID

    @Schema(description = "시도")
    private String sido; // 시도
}