package com.onbid.service.goods;

import com.onbid.domain.goods.dto.Goods;
import com.onbid.domain.goods.entity.GoodsEntity;
import com.onbid.mapper.goods.GoodsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 물건 서비스
 * DB에 저장된 물건 정보를 관리하고 API에서 데이터를 가져와 초기화
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GoodsService {
    
    private final GoodsMapper goodsMapper;
    
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
        return goodsMapper.findByHistoryNo(historyNo);
    }
    
    /**
     * 물건 목록을 DB에 저장 (기존 데이터 삭제 후 저장)
     * @param goods 저장할 물건 목록
     * @return 저장된 물건 개수
     */
    @Transactional
    public int saveGoodsListToDB(List<Goods> goods) {
        log.info("=== 물건 데이터 저장 시작 ===");
        
        try {
            // 기존 데이터 삭제
            int deletedCount = goodsMapper.deleteAll();
            log.info("기존 물건 데이터 {}개 삭제", deletedCount);
            
            if (goods == null || goods.isEmpty()) {
                log.warn("저장할 물건이 없습니다.");
                return 0;
            }
            
            // DB에 저장
            int savedCount = 0;
            for (Goods item : goods) {
                // historyNo가 없는 경우 건너뛰기
                if (item.getHistoryNo() == null) {
                    log.warn("물건이력번호가 없어 건너뜁니다: {}", item.getGoodsName());
                    continue;
                }
                
                try {
                    GoodsEntity entity = convertToEntity(item);
                    goodsMapper.insert(entity);
                    savedCount++;
                } catch (Exception e) {
                    log.error("물건 저장 실패 - historyNo: {}, 오류: {}", 
                            item.getHistoryNo(), e.getMessage());
                }
            }
            
            log.info("=== 물건 데이터 저장 완료: {}개 저장 ===", savedCount);
            return savedCount;
            
        } catch (Exception e) {
            log.error("물건 데이터 저장 실패", e);
            throw new RuntimeException("물건 데이터 저장 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * 모든 물건 데이터 삭제
     * @return 삭제된 물건 개수
     */
    @Transactional
    public int deleteAllGoods() {
        log.info("=== 모든 물건 데이터 삭제 ===");
        
        try {
            int deletedCount = goodsMapper.deleteAll();
            log.info("{}개의 물건 데이터 삭제 완료", deletedCount);
            return deletedCount;
        } catch (Exception e) {
            log.error("물건 데이터 삭제 실패", e);
            throw new RuntimeException("물건 데이터 삭제 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * API 응답(Goods)을 DB 엔티티(GoodsEntity)로 변환
     * 매매에 필수적인 정보만 저장
     */
    private GoodsEntity convertToEntity(Goods goods) {
        return GoodsEntity.builder()
                .historyNo(goods.getHistoryNo())
                .goodsName(goods.getGoodsName())
                .minBidPrice(goods.getMinBidPrice())
                .appraisalPrice(goods.getAppraisalPrice())
                .bidCloseDate(goods.getBidCloseDate())
                .address(goods.getAddress())
                .build();
    }
}

