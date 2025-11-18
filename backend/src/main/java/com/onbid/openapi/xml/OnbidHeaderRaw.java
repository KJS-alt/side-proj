package com.onbid.openapi.xml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

/**
 * 온비드 XML 헤더를 그대로 담는 Raw DTO
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OnbidHeaderRaw {
    
    /**
     * 결과 코드 (00 등)
     */
    @JacksonXmlProperty(localName = "resultCode")
    private String resultCode;
    
    /**
     * 결과 메시지
     */
    @JacksonXmlProperty(localName = "resultMsg")
    private String resultMsg;
}


