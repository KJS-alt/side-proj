package com.onbid.service;

import com.onbid.domain.Goods;
import com.onbid.domain.GoodsResponse;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.StringReader;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;


@Slf4j
@Service
public class OnbidApiService {

    @Value("${onbid.api.key}")
    private String apiKey;

    @Value("${onbid.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;
    private final JAXBContext jaxbContext;

    /**
     * 생성자 - RestTemplate 및 JAXB Context 초기화 (타임아웃 설정 포함)
     */
    public OnbidApiService() {
        // RestTemplate에 타임아웃 설정 추가 (15초)
        this.restTemplate = new RestTemplate();
//        this.restTemplate.setRequestFactory(new org.springframework.http.client.SimpleClientHttpRequestFactory() {{
//            setConnectTimeout(1500000);  // 연결 타임아웃: 15초
//            setReadTimeout(3000000);      // 읽기 타임아웃: 30초
//        }});
        
        try {
            // GoodsResponse를 위한 JAXB Context 생성
            this.jaxbContext = JAXBContext.newInstance(GoodsResponse.class);
        } catch (JAXBException e) {
            log.error("JAXB Context 초기화 실패", e);
            throw new RuntimeException("JAXB 초기화 오류", e);
        }
    }

    /**
     * 물건 목록 조회
     * 카테고리를 지정하면 해당 카테고리의 물건만 조회
     */
    public String getGoodsList(
            int pageNo,
            int numOfRows,
            String ctgrHirkId,  // 카테고리 추가
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

            // 매각만 (임대 제외) - 필수
            urlBuilder.append("&DPSL_MTD_CD=0001");

            // 카테고리 설정 (선택사항)
            // 카테고리를 지정하지 않으면 모든 카테고리 검색
            if (isNotEmpty(ctgrHirkId)) {
                urlBuilder.append("&CTGR_HIRK_ID=").append(ctgrHirkId);
                log.info("카테고리 지정: {}", ctgrHirkId);
            } else {
                log.info("카테고리 미지정 - 전체 카테고리 검색");
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

            // URI 객체로 변환하여 중복 인코딩 방지
            URI uri = URI.create(url);
            
            log.info(">>> 외부 API 요청 시작 (타임아웃: 연결 15초, 읽기 30초)");
            long startTime = System.currentTimeMillis();
            String response = restTemplate.getForObject(uri, String.class);
            long endTime = System.currentTimeMillis();
            log.info(">>> 외부 API 응답 수신 완료");

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

        } catch (org.springframework.web.client.ResourceAccessException e) {
            log.error("=== OnBid API 타임아웃 또는 연결 실패 ===");
            log.error("Onbid API 서버 응답 없음 (타임아웃 또는 네트워크 오류)");
            log.error("상세 오류: {}", e.getMessage());
            throw new RuntimeException("Onbid API 연결 실패 (타임아웃 또는 네트워크 오류). 잠시 후 다시 시도해주세요.", e);
        } catch (Exception e) {
            log.error("=== OnBid API 호출 실패 ===");
            log.error("오류 타입: {}", e.getClass().getName());
            log.error("오류 메시지: {}", e.getMessage(), e);
            throw new RuntimeException("OnBid API 호출 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 간단한 물건 목록 조회
     */
    public String getGoodsList(int pageNo, int numOfRows, String ctgrHirkId, String sido) {
        return getGoodsList(pageNo, numOfRows, ctgrHirkId, sido, null, null, null, null, null, null, null, null, null, null);
    }

    /**
     * API 연결 테스트
     */
    public String testConnection() {
        log.info("OnBid API 연결 테스트");
        return getGoodsList(1, 5, null, null);
    }

    /**
     * XML 응답을 Java 객체로 변환
     * @param xml XML 문자열
     * @return GoodsResponse 객체
     */
    public GoodsResponse parseXmlToGoodsResponse(String xml) {
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            StringReader reader = new StringReader(xml);
            GoodsResponse response = (GoodsResponse) unmarshaller.unmarshal(reader);
            
            log.debug("XML 파싱 완료: totalCount={}", 
                    response.getBody() != null ? response.getBody().getTotalCount() : 0);
            
            return response;
        } catch (JAXBException e) {
            log.error("XML 파싱 실패", e);
            throw new RuntimeException("XML 파싱 오류: " + e.getMessage(), e);
        }
    }
    
    /**
     * 물건 목록 조회 및 파싱
     * @param pageNo 페이지 번호
     * @param numOfRows 조회 건수
     * @param ctgrHirkId 카테고리 ID
     * @param sido 시도
     * @return 파싱된 물건 목록
     */
    public GoodsResponse getGoodsListParsed(int pageNo, int numOfRows, String ctgrHirkId, String sido) {
        String xml = getGoodsList(pageNo, numOfRows, ctgrHirkId, sido);
        return parseXmlToGoodsResponse(xml);
    }
    
    /**
     * 물건 목록 조회 및 파싱 (전체 파라미터)
     * @return 파싱된 물건 목록
     */
    public GoodsResponse getGoodsListParsed(
            int pageNo, int numOfRows, String ctgrHirkId, String sido, String sgk, String emd,
            String goodsPriceFrom, String goodsPriceTo, String openPriceFrom, String openPriceTo,
            String cltrNm, String pbctBegnDtm, String pbctClsDtm, String cltrMnmtNo) {
        
        String xml = getGoodsList(pageNo, numOfRows, ctgrHirkId, sido, sgk, emd,
                goodsPriceFrom, goodsPriceTo, openPriceFrom, openPriceTo,
                cltrNm, pbctBegnDtm, pbctClsDtm, cltrMnmtNo);
        
        return parseXmlToGoodsResponse(xml);
    }
    
    /**
     * 물건 목록만 추출
     * @param pageNo 페이지 번호
     * @param numOfRows 조회 건수
     * @param ctgrHirkId 카테고리 ID
     * @param sido 시도
     * @return 물건 목록
     */
    public List<Goods> getGoodsItems(int pageNo, int numOfRows, String ctgrHirkId, String sido) {
        try {
            GoodsResponse response = getGoodsListParsed(pageNo, numOfRows, ctgrHirkId, sido);
            if (response != null && response.getBody() != null && response.getBody().getItems() != null) {
                return response.getBody().getItems();
            }
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("물건 목록 조회 실패", e);
            return Collections.emptyList();
        }
    }
    
    /**
     * 문자열이 비어있지 않은지 확인
     */
    private boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
}