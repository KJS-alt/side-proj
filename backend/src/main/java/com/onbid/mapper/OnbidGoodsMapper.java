package com.onbid.mapper;

import com.onbid.domain.dto.Goods;
import com.onbid.domain.dto.Response.GoodsResponse;
import com.onbid.openapi.xml.OnbidBodyRaw;
import com.onbid.openapi.xml.OnbidHeaderRaw;
import com.onbid.openapi.xml.OnbidItemRaw;
import com.onbid.openapi.xml.OnbidResponseRaw;
import java.util.List;
import java.util.Objects;

/**
 * 온비드 XML Raw DTO를 내부 도메인 DTO로 바꿔주는 전용 매퍼
 */
public final class OnbidGoodsMapper {
    
    private OnbidGoodsMapper() {
    }
    
    /**
     * Raw 응답 전체를 도메인 응답으로 변환
     */
    public static GoodsResponse toDomain(OnbidResponseRaw raw) {
        if (raw == null) {
            return GoodsResponse.builder().build();
        }
        
        return GoodsResponse.builder()
                .header(mapHeader(raw.getHeader()))
                .body(mapBody(raw.getBody()))
                .build();
    }
    
    /**
     * Raw 목록을 도메인 물건 목록으로 변환
     */
    public static List<Goods> toDomainList(List<OnbidItemRaw> raws) {
        if (raws == null || raws.isEmpty()) {
            return List.of();
        }
        
        return raws.stream()
                .filter(Objects::nonNull)
                .map(OnbidGoodsMapper::mapItem)
                .toList();
    }
    
    /**
     * 헤더 정보를 도메인 헤더로 변환
     */
    private static GoodsResponse.Header mapHeader(OnbidHeaderRaw headerRaw) {
        if (headerRaw == null) {
            return null;
        }
        
        return GoodsResponse.Header.builder()
                .resultCode(headerRaw.getResultCode())
                .resultMsg(headerRaw.getResultMsg())
                .build();
    }
    
    /**
     * 바디 정보를 도메인 바디로 변환
     */
    private static GoodsResponse.Body mapBody(OnbidBodyRaw bodyRaw) {
        if (bodyRaw == null) {
            return null;
        }
        
        return GoodsResponse.Body.builder()
                .items(toDomainList(bodyRaw.getItems()))
                .numOfRows(bodyRaw.getNumOfRows())
                .pageNo(bodyRaw.getPageNo())
                .totalCount(bodyRaw.getTotalCount())
                .build();
    }
    
    /**
     * 개별 물건 Raw DTO를 도메인 DTO로 변환
     */
    private static Goods mapItem(OnbidItemRaw raw) {
        return Goods.builder()
                .historyNo(raw.getHistoryNo())
                .goodsNo(raw.getGoodsNo())
                .goodsName(raw.getGoodsName())
                .goodsDetail(raw.getGoodsDetail())
                .appraisalPrice(raw.getAppraisalPrice())
                .minBidPrice(raw.getMinBidPrice())
                .bidStartDate(raw.getBidStartDate())
                .bidCloseDate(raw.getBidCloseDate())
                .noticeNo(raw.getNoticeNo())
                .statusName(raw.getStatusName())
                .address(raw.getAddress())
                .roadAddress(raw.getRoadAddress())
                .saleTypeCode(raw.getSaleTypeCode())
                .saleTypeName(raw.getSaleTypeName())
                .bidMethodName(raw.getBidMethodName())
                .categoryName(raw.getCategoryName())
                .inquiryCount(raw.getInquiryCount())
                .favoriteCount(raw.getFavoriteCount())
                .feeRate(raw.getFeeRate())
                .imageFiles(raw.getImageFiles())
                .build();
    }
}


