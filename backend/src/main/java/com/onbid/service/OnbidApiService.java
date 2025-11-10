package com.onbid.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@Slf4j
@Service
public class OnbidApiService {

    @Value("${onbid.api.key}")
    private String apiKey;

    @Value("${onbid.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public OnbidApiService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * 물건 목록 조회 - SIDO만으로 검색 (카테고리 제한 없음)
     */
    public String getGoodsList(
            int pageNo,
            int numOfRows,
            String sido,
            String sgk,
            String emd,
            String goodsPriceFrom,
            String goodsPriceTo,
            String openPriceFrom,
            String openPriceTo,
            String cltrNm,
            String pbctBegnDtm,
            String pbctClsDtm,
            String cltrMnmtNo) {

        try {
            StringBuilder urlBuilder = new StringBuilder(apiUrl);

            // 필수 파라미터
            urlBuilder.append("?serviceKey=").append(apiKey);
            urlBuilder.append("&numOfRows=").append(numOfRows);
            urlBuilder.append("&pageNo=").append(pageNo);

            // 매각만 (임대 제외)
            urlBuilder.append("&DPSL_MTD_CD=0001");

            // OnBid API는 카테고리 없이는 SIDO 검색이 안 되므로, SIDO가 있으면 토지 대분류(10000)를 기본값으로 추가
            if (isNotEmpty(sido)) {
                urlBuilder.append("&CTGR_HIRK_ID=10000");  // 토지 대분류
            }

            // 선택적 파라미터들
            if (isNotEmpty(sido)) {
                urlBuilder.append("&SIDO=").append(URLEncoder.encode(sido, StandardCharsets.UTF_8));
            }
            if (isNotEmpty(sgk)) {
                urlBuilder.append("&SGK=").append(URLEncoder.encode(sgk, StandardCharsets.UTF_8));
            }
            if (isNotEmpty(emd)) {
                urlBuilder.append("&EMD=").append(URLEncoder.encode(emd, StandardCharsets.UTF_8));
            }
            if (isNotEmpty(goodsPriceFrom)) {
                urlBuilder.append("&GOODS_PRICE_FROM=").append(goodsPriceFrom);
            }
            if (isNotEmpty(goodsPriceTo)) {
                urlBuilder.append("&GOODS_PRICE_TO=").append(goodsPriceTo);
            }
            if (isNotEmpty(openPriceFrom)) {
                urlBuilder.append("&OPEN_PRICE_FROM=").append(openPriceFrom);
            }
            if (isNotEmpty(openPriceTo)) {
                urlBuilder.append("&OPEN_PRICE_TO=").append(openPriceTo);
            }
            if (isNotEmpty(cltrNm)) {
                urlBuilder.append("&CLTR_NM=").append(URLEncoder.encode(cltrNm, StandardCharsets.UTF_8));
            }
            if (isNotEmpty(pbctBegnDtm)) {
                urlBuilder.append("&PBCT_BEGN_DTM=").append(pbctBegnDtm);
            }
            if (isNotEmpty(pbctClsDtm)) {
                urlBuilder.append("&PBCT_CLS_DTM=").append(pbctClsDtm);
            }
            if (isNotEmpty(cltrMnmtNo)) {
                urlBuilder.append("&CLTR_MNMT_NO=").append(URLEncoder.encode(cltrMnmtNo, StandardCharsets.UTF_8));
            }

            String url = urlBuilder.toString();

            log.info("=== OnBid API 호출 ===");
            log.info("검색 조건 - 페이지: {}, 개수: {}, 지역: {}",
                    pageNo, numOfRows, sido != null ? sido : "전체");
            log.info("전체 URL: {}", url);

            long startTime = System.currentTimeMillis();
            String response = restTemplate.getForObject(url, String.class);
            long endTime = System.currentTimeMillis();

            int responseLength = response != null ? response.length() : 0;
            log.info("API 호출 성공 - 소요시간: {}ms, 응답크기: {} bytes",
                    (endTime - startTime), responseLength);

            // 응답 미리보기
            if (response != null && response.length() > 0) {
                String preview = response.substring(0, Math.min(500, response.length()));
                log.debug("응답 미리보기:\n{}", preview);

                // totalCount 추출
                if (response.contains("<totalCount>")) {
                    int start = response.indexOf("<totalCount>") + 12;
                    int end = response.indexOf("</totalCount>");
                    String totalCount = response.substring(start, end);
                    log.info("조회된 총 건수: {} 건", totalCount);
                }
            }

            return response;

        } catch (Exception e) {
            log.error("=== OnBid API 호출 실패 ===");
            log.error("오류: {}", e.getMessage(), e);
            throw new RuntimeException("OnBid API 호출 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 간단한 물건 목록 조회
     */
    public String getGoodsList(int pageNo, int numOfRows, String sido) {
        return getGoodsList(pageNo, numOfRows, sido, null, null, null, null, null, null, null, null, null, null);
    }

    /**
     * API 연결 테스트
     */
    public String testConnection() {
        log.info("OnBid API 연결 테스트");
        return getGoodsList(1, 5, null);
    }

    /**
     * 문자열이 비어있지 않은지 확인
     */
    private boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
}