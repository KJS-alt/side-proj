package com.onbid.openapi.xml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.util.List;
import lombok.Data;

/**
 * 온비드 XML 응답의 body 영역을 그대로 들고 있는 Raw DTO
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OnbidBodyRaw {
    
    /**
     * 물건 목록 XML 노드
     */
    @JacksonXmlElementWrapper(localName = "items")
    @JacksonXmlProperty(localName = "item")
    private List<OnbidItemRaw> items;
    
    /**
     * 페이지당 건수
     */
    @JacksonXmlProperty(localName = "numOfRows")
    private Integer numOfRows;
    
    /**
     * 현재 페이지 번호
     */
    @JacksonXmlProperty(localName = "pageNo")
    private Integer pageNo;
    
    /**
     * 전체 건수
     */
    @JacksonXmlProperty(localName = "totalCount")
    private Integer totalCount;
}


