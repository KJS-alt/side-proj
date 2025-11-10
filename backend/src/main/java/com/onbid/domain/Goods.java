package com.onbid.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 온비드 공매물건 DTO
 * XML 파싱용 JAXB 어노테이션 포함
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Goods {
    
    /**
     * 물건이력번호 (회차별 고유 식별자)
     */
    @XmlElement(name = "CLTR_HSTR_NO")
    private Long historyNo;
    
    /**
     * 물건관리번호 (물건 식별자) - 예: 2025-09090-001
     */
    @XmlElement(name = "CLTR_MNMT_NO")
    private String goodsNo;
    
    /**
     * 물건명 (전체 제목)
     */
    @XmlElement(name = "CLTR_NM")
    private String goodsName;
    
    /**
     * 물건상세명 (설명)
     */
    @XmlElement(name = "GOODS_NM")
    private String goodsDetail;
    
    /**
     * 감정가 (평가액)
     */
    @XmlElement(name = "APSL_ASES_AVG_AMT")
    private Long appraisalPrice;
    
    /**
     * 최저입찰가격 (입찰 시작가)
     */
    @XmlElement(name = "MIN_BID_PRC")
    private Long minBidPrice;
    
    /**
     * 입찰시작일시 (YYYYMMDDHHmmss)
     */
    @XmlElement(name = "PBCT_BEGN_DTM")
    private String bidStartDate;
    
    /**
     * 입찰마감일시 (YYYYMMDDHHmmss)
     */
    @XmlElement(name = "PBCT_CLS_DTM")
    private String bidCloseDate;
    
    /**
     * 공고번호
     */
    @XmlElement(name = "PBCT_NO")
    private String noticeNo;
    
    /**
     * 물건진행상태명 (예: 인터넷입찰진행중, 입찰준비중)
     */
    @XmlElement(name = "PBCT_CLTR_STAT_NM")
    private String statusName;
    
    /**
     * 지번 주소
     */
    @XmlElement(name = "LDNM_ADRS")
    private String address;
    
    /**
     * 도로명 주소
     */
    @XmlElement(name = "NMRD_ADRS")
    private String roadAddress;
    
    /**
     * 매각구분코드 (0001: 매각, 0002: 임대)
     */
    @XmlElement(name = "DPSL_MTD_CD")
    private String saleTypeCode;
    
    /**
     * 매각구분명 (매각, 임대)
     */
    @XmlElement(name = "DPSL_MTD_NM")
    private String saleTypeName;
    
    /**
     * 입찰방법명 (예: 일반경쟁(최고가방식) / 총액)
     */
    @XmlElement(name = "BID_MTD_NM")
    private String bidMethodName;
    
    /**
     * 카테고리 전체명 (예: 주거용건물 / 다세대주택)
     */
    @XmlElement(name = "CTGR_FULL_NM")
    private String categoryName;
    
    /**
     * 조회수
     */
    @XmlElement(name = "IQRY_CNT")
    private Integer inquiryCount;
    
    /**
     * 관심물건 등록 수
     */
    @XmlElement(name = "USCBD_CNT")
    private Integer favoriteCount;
    
    /**
     * 비율 (예: (100%), (90%))
     */
    @XmlElement(name = "FEE_RATE")
    private String feeRate;
    
    /**
     * 물건 이미지 파일들
     */
    @XmlElement(name = "CLTR_IMG_FILES")
    private String imageFiles;
}

