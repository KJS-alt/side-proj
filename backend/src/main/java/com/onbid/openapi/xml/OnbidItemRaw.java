package com.onbid.openapi.xml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

/**
 * 온비드 XML item 노드를 표현하는 Raw DTO
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OnbidItemRaw {
    
    /**
     * 물건이력번호
     */
    @JacksonXmlProperty(localName = "CLTR_HSTR_NO")
    private Long historyNo;
    
    /**
     * 물건관리번호
     */
    @JacksonXmlProperty(localName = "CLTR_MNMT_NO")
    private String goodsNo;
    
    /**
     * 물건명
     */
    @JacksonXmlProperty(localName = "CLTR_NM")
    private String goodsName;
    
    /**
     * 물건상세명
     */
    @JacksonXmlProperty(localName = "GOODS_NM")
    private String goodsDetail;
    
    /**
     * 감정가
     */
    @JacksonXmlProperty(localName = "APSL_ASES_AVG_AMT")
    private Long appraisalPrice;
    
    /**
     * 최저입찰가
     */
    @JacksonXmlProperty(localName = "MIN_BID_PRC")
    private Long minBidPrice;
    
    /**
     * 입찰시작일시
     */
    @JacksonXmlProperty(localName = "PBCT_BEGN_DTM")
    private String bidStartDate;
    
    /**
     * 입찰마감일시
     */
    @JacksonXmlProperty(localName = "PBCT_CLS_DTM")
    private String bidCloseDate;
    
    /**
     * 공고번호
     */
    @JacksonXmlProperty(localName = "PBCT_NO")
    private String noticeNo;
    
    /**
     * 물건상태명
     */
    @JacksonXmlProperty(localName = "PBCT_CLTR_STAT_NM")
    private String statusName;
    
    /**
     * 지번 주소
     */
    @JacksonXmlProperty(localName = "LDNM_ADRS")
    private String address;
    
    /**
     * 도로명 주소
     */
    @JacksonXmlProperty(localName = "NMRD_ADRS")
    private String roadAddress;
    
    /**
     * 매각구분코드
     */
    @JacksonXmlProperty(localName = "DPSL_MTD_CD")
    private String saleTypeCode;
    
    /**
     * 매각구분명
     */
    @JacksonXmlProperty(localName = "DPSL_MTD_NM")
    private String saleTypeName;
    
    /**
     * 입찰방법명
     */
    @JacksonXmlProperty(localName = "BID_MTD_NM")
    private String bidMethodName;
    
    /**
     * 카테고리 전체명
     */
    @JacksonXmlProperty(localName = "CTGR_FULL_NM")
    private String categoryName;
    
    /**
     * 조회수
     */
    @JacksonXmlProperty(localName = "IQRY_CNT")
    private Integer inquiryCount;
    
    /**
     * 관심물건 등록 수
     */
    @JacksonXmlProperty(localName = "USCBD_CNT")
    private Integer favoriteCount;
    
    /**
     * 수수료 비율
     */
    @JacksonXmlProperty(localName = "FEE_RATE")
    private String feeRate;
    
    /**
     * 물건 이미지 파일 경로
     */
    @JacksonXmlProperty(localName = "CLTR_IMG_FILES")
    private String imageFiles;
}


