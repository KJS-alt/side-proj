package com.onbid.service;

import com.onbid.domain.dto.Goods;
import com.onbid.domain.entity.GoodsBasicEntity;
import com.onbid.domain.entity.GoodsEntity;
import com.onbid.domain.entity.GoodsPriceEntity;
import com.onbid.exception.BusinessException;
import com.onbid.exception.ErrorCode;
import com.onbid.exception.GoodsNotFoundException;
import com.onbid.mapper.GoodsMapper;
import com.onbid.mapper.PurchaseMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 물건 서비스
 * DB에 저장된 물건 정보를 관리하고 API에서 데이터를 가져와 초기화
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GoodsService {
    
    private final GoodsMapper goodsMapper;
    private final PurchaseMapper purchaseMapper;
    
    /**
     * DB에서 전체 물건 조회
     * @return 물건 목록
     */
    public List<GoodsEntity> getAllGoods() {
        log.info("DB에서 전체 물건 조회");
        return goodsMapper.findAll();
    }
    
    /**
     * 물건이력번호로 물건 조회
     * @param historyNo 물건이력번호
     * @return 물건 정보
     */
    public GoodsEntity getGoodsByHistoryNo(Long historyNo) {
        log.info("물건이력번호로 물건 조회: {}", historyNo);
        GoodsEntity entity = goodsMapper.findByHistoryNo(historyNo);
        if (entity == null) {
            // 주어진 물건이력번호에 해당하는 데이터가 없음을 명시적으로 알림
            throw new GoodsNotFoundException(historyNo);
        }
        return entity;
    }
    
    /**
     * 물건 목록을 DB에 저장 (기존 데이터 삭제 후 저장)
     * @param goods 저장할 물건 목록
     * @return 저장된 물건 개수
     */
    @Transactional
    public int saveGoodsListToDB(List<Goods> goods) {
        log.info("=== 물건 데이터 동기화 시작 ===");

        if (goods == null || goods.isEmpty()) {
            // 동기화할 데이터가 없으면 조용히 0건 처리로 반환
            log.warn("저장할 물건 목록이 비어 있어 동기화를 건너뜁니다.");
            return 0;
        }

        int syncedCount = 0;
        for (Goods item : goods) {
            if (item.getHistoryNo() == null) {
                log.warn("물건이력번호가 없어 건너뜁니다: {}", item.getGoodsName());
                continue;
            }

            try {
                goodsMapper.insertOrUpdateBasic(convertToBasicEntity(item));
                goodsMapper.insertOrUpdatePrice(convertToPriceEntity(item));
                syncedCount++;
            } catch (Exception e) {
                log.error("물건 동기화 실패 - historyNo: {}, 오류: {}",
                        item.getHistoryNo(), e.getMessage());
            }
        }

        log.info("=== 물건 데이터 동기화 완료: {}개 반영 ===", syncedCount);
        return syncedCount;
    }
    
    /**
     * 모든 물건 데이터 삭제
     * @return 삭제된 물건 개수
     */
    @Transactional
    public int deleteAllGoods() {
        log.info("=== 모든 물건 데이터 삭제 ===");
        
        try {
            purchaseMapper.deleteAll();
            goodsMapper.deleteAllPrices();
            int deletedBasic = goodsMapper.deleteAllBasics();
            log.info("기본 정보 {}개 삭제 완료", deletedBasic);
            return deletedBasic;
        } catch (Exception e) {
            log.error("물건 데이터 삭제 실패", e);
            throw new BusinessException(ErrorCode.DATABASE_ERROR, "물건 데이터 삭제 실패: " + e.getMessage());
        }
    }

    private GoodsBasicEntity convertToBasicEntity(Goods goods) {
        return GoodsBasicEntity.builder()
                .historyNo(goods.getHistoryNo())
                .goodsName(goods.getGoodsName())
                .statusName(goods.getStatusName())
                .saleTypeName(goods.getSaleTypeName())
                .categoryName(goods.getCategoryName())
                .bidStartDate(goods.getBidStartDate())
                .bidCloseDate(goods.getBidCloseDate())
                .address(goods.getAddress())
                .build();
    }

    private GoodsPriceEntity convertToPriceEntity(Goods goods) {
        return GoodsPriceEntity.builder()
                .historyNo(goods.getHistoryNo())
                .minBidPrice(goods.getMinBidPrice())
                .appraisalPrice(goods.getAppraisalPrice())
                .feeRate(goods.getFeeRate())
                .inquiryCount(goods.getInquiryCount())
                .favoriteCount(goods.getFavoriteCount())
                .build();
    }
}

