package com.onbid.openapi.xml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

/**
 * 온비드 XML 응답 전체 구조를 담는 루트 DTO
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "response")
public class OnbidResponseRaw {
    
    /**
     * 헤더 영역
     */
    @JacksonXmlProperty(localName = "header")
    private OnbidHeaderRaw header;
    
    /**
     * 바디 영역
     */
    @JacksonXmlProperty(localName = "body")
    private OnbidBodyRaw body;
}


