package com.onbid.service.purchase;

import com.onbid.domain.purchase.entity.Purchase;
import com.onbid.domain.purchase.dto.PurchaseRequest;
import com.onbid.mapper.purchase.PurchaseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 매매(구매) 서비스
 * 물건 구매 관련 비즈니스 로직 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseService {
    
    private final PurchaseMapper purchaseMapper;
    
    /**
     * 구매 생성
     * @param request 구매 요청 정보
     * @return 생성된 구매 정보
     */
    @Transactional
    public Purchase createPurchase(PurchaseRequest request) {
        log.info("구매 생성 - historyNo: {}, price: {}", 
                request.getHistoryNo(), request.getPurchasePrice());
        
        // Purchase 엔티티 생성
        Purchase purchase = Purchase.builder()
                .historyNo(request.getHistoryNo())
                .purchasePrice(request.getPurchasePrice())
                .purchaseStatus("COMPLETED")  // 기본값: 완료
                .build();
        
        // DB에 저장
        purchaseMapper.insert(purchase);
        
        log.info("구매 완료 - purchaseId: {}", purchase.getId());
        return purchase;
    }
    
    /**
     * 물건이력번호로 구매 이력 조회
     * @param historyNo 물건이력번호
     * @return 구매 이력 목록
     */
    public List<Purchase> getPurchasesByHistoryNo(Long historyNo) {
        log.info("구매 이력 조회 - historyNo: {}", historyNo);
        return purchaseMapper.findByHistoryNo(historyNo);
    }
    
    /**
     * 전체 구매 이력 조회
     * @return 구매 이력 목록
     */
    public List<Purchase> getAllPurchases() {
        log.info("전체 구매 이력 조회");
        return purchaseMapper.findAll();
    }
}

