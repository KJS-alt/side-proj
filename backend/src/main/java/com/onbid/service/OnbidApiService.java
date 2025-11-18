package com.onbid.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.onbid.domain.dto.Goods;
import com.onbid.domain.dto.Response.GoodsResponse;
import com.onbid.exception.BusinessException;
import com.onbid.exception.ErrorCode;
import com.onbid.mapper.OnbidGoodsMapper;
import com.onbid.openapi.xml.OnbidResponseRaw;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * 온비드 외부 API 호출과 XML 파싱을 담당하는 서비스
 */
@Slf4j
@Service
public class OnbidApiService {
    
    @Value("${onbid.api.key}")
    private String apiKey;
    
    @Value("${onbid.api.url}")
    private String apiUrl;
    
    private final RestTemplate restTemplate;
    private final XmlMapper xmlMapper;
    
    /**
     * RestTemplate/XmlMapper를 주입받아 초기화
     */
    public OnbidApiService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
        this.xmlMapper = XmlMapper.builder()
                .defaultUseWrapper(false)
                .build();
        this.xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    
    /**
     * 물건 목록 조회 (원시 XML 문자열 반환)
     */
    public String getGoodsList(
            int pageNo,
            int numOfRows,
            String ctgrHirkId,
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
            urlBuilder.append("?serviceKey=").append(apiKey);
            urlBuilder.append("&numOfRows=").append(numOfRows);
            urlBuilder.append("&pageNo=").append(pageNo);
            urlBuilder.append("&DPSL_MTD_CD=0001");
            
            if (isNotEmpty(ctgrHirkId)) {
                urlBuilder.append("&CTGR_HIRK_ID=").append(ctgrHirkId);
            }
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
            
            URI uri = URI.create(urlBuilder.toString());
            log.info("Onbid API 호출 URL: {}", uri);
            long start = System.currentTimeMillis();
            String response = restTemplate.getForObject(uri, String.class);
            long elapsed = System.currentTimeMillis() - start;
            log.info("Onbid API 호출 완료 - {}ms", elapsed);
            return response;
        } catch (Exception ex) {
            log.error("Onbid API 호출 실패", ex);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Onbid API 호출 실패: " + ex.getMessage());
        }
    }
    
    /**
     * 간단한 물건 목록 조회
     */
    public String getGoodsList(int pageNo, int numOfRows, String ctgrHirkId, String sido) {
        return getGoodsList(pageNo, numOfRows, ctgrHirkId, sido,
                null, null, null, null, null, null, null, null, null, null);
    }
    
    /**
     * XML 문자열을 도메인 응답으로 변환
     */
    public GoodsResponse parseXmlToGoodsResponse(String xml) {
        try {
            OnbidResponseRaw raw = xmlMapper.readValue(xml, OnbidResponseRaw.class);
            return OnbidGoodsMapper.toDomain(raw);
        } catch (JsonProcessingException ex) {
            log.error("XML 파싱 실패", ex);
            throw new BusinessException(ErrorCode.XML_PARSE_ERROR, "XML 파싱 실패: " + ex.getOriginalMessage());
        }
    }
    
    /**
     * 물건 목록 조회 및 파싱
     */
    public GoodsResponse getGoodsListParsed(int pageNo, int numOfRows, String ctgrHirkId, String sido) {
        String xml = getGoodsList(pageNo, numOfRows, ctgrHirkId, sido);
        return parseXmlToGoodsResponse(xml);
    }
    
    /**
     * 물건 목록 조회 및 파싱 (전체 파라미터)
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
     */
    public List<Goods> getGoodsItems(int pageNo, int numOfRows, String ctgrHirkId, String sido) {
        GoodsResponse response = getGoodsListParsed(pageNo, numOfRows, ctgrHirkId, sido);
        return Optional.ofNullable(response.getBody())
                .map(GoodsResponse.Body::getItems)
                .orElse(List.of());
    }
    
    private boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
}