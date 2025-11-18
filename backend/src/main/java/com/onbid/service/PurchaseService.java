package com.onbid.service;

import com.onbid.domain.dto.Request.PurchaseRequest;
import com.onbid.domain.entity.Purchase;
import com.onbid.exception.BusinessException;
import com.onbid.exception.ErrorCode;
import com.onbid.mapper.PurchaseMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        if (request.getHistoryNo() == null || request.getPurchasePrice() == null) {
            // 필수 값이 비어 있으면 비즈니스 예외로 클라이언트에 안내
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "historyNo와 purchasePrice는 필수입니다.");
        }

        if (purchaseMapper.countByHistoryNo(request.getHistoryNo()) > 0) {
            // 동일 물건 중복 구매를 방지하기 위해 명시적으로 실패 처리
            throw new BusinessException(ErrorCode.DUPLICATED_PURCHASE, "이미 구매가 완료된 물건입니다.");
        }
        
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

    /**
     * 전체 구매 이력 초기화
     * @return 삭제된 데이터 수
     */
    @Transactional
    public int resetAllPurchases() {
        log.warn("구매 이력 전체 삭제 요청 처리");
        return purchaseMapper.deleteAll();
    }
}

