package com.onbid.domain.dto.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 온비드 물건 검색 조건 DTO (각 필드 설명은 한글 주석 필수)
 */
@Data
public class GoodsSearchRequest {

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

    @Schema(description = "시군구")
    private String sgk; // 시군구

    @Schema(description = "읍면동")
    private String emd; // 읍면동

    @Schema(description = "물건가격 시작")
    private String goodsPriceFrom; // 물건가격 시작

    @Schema(description = "물건가격 끝")
    private String goodsPriceTo; // 물건가격 끝

    @Schema(description = "개찰가격 시작")
    private String openPriceFrom; // 개찰가격 시작

    @Schema(description = "개찰가격 끝")
    private String openPriceTo; // 개찰가격 끝

    @Schema(description = "물건명")
    private String cltrNm; // 물건명

    @Schema(description = "공고시작일시")
    private String pbctBegnDtm; // 공고 시작일시

    @Schema(description = "공고종료일시")
    private String pbctClsDtm; // 공고 종료일시

    @Schema(description = "물건관리번호")
    private String cltrMnmtNo; // 물건 관리번호
}