package com.onbid.controller;

import com.onbid.domain.dto.Request.PurchaseRequest;
import com.onbid.domain.entity.PurchaseEntity;
import com.onbid.service.PurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 매매(구매) REST API Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
@Tag(name = "매매", description = "물건 구매 관련 API")
public class PurchaseApiController {
    
    private final PurchaseService purchaseService;
    
    /**
     * 구매 생성
     */
    @PostMapping
    @Operation(summary = "구매 생성", description = "물건을 구매합니다")
    public ResponseEntity<Map<String, Object>> createPurchase(
            @Valid @RequestBody PurchaseRequest request) {
        
        log.info("구매 생성 API 호출 - historyNo: {}, price: {}",
                request.getHistoryNo(), request.getPurchasePrice());
        
        PurchaseEntity purchase = purchaseService.createPurchase(request);
        Map<String, Object> body = new HashMap<>();
        body.put("success", true); // 구매 요청이 정상 완료되었음을 알리는 플래그
        body.put("purchase", purchase);
        body.put("message", "구매가 완료되었습니다.");
        return ResponseEntity.ok(body);
    }
    
    /**
     * 특정 물건의 구매 이력 조회 (이미 구매했으면 상세 페이지에서 구매 불가능)
     */
    @GetMapping("/{historyNo}")
    @Operation(summary = "구매 이력 조회", description = "특정 물건의 구매 이력을 조회합니다")
    public ResponseEntity<Map<String, Object>> getPurchasesByHistoryNo(
            @PathVariable @Parameter(description = "물건이력번호") Long historyNo) {
        
        List<PurchaseEntity> purchases = purchaseService.getPurchasesByHistoryNo(historyNo);
        Map<String, Object> body = new HashMap<>();
        body.put("success", true); // 조회 성공 여부를 명시
        body.put("items", purchases);
        body.put("count", purchases.size());
        return ResponseEntity.ok(body);
    }
    
    /**
     * 전체 구매 이력 조회
     */
    @GetMapping
    @Operation(summary = "전체 구매 이력 조회", description = "모든 구매 이력을 조회합니다")
    public ResponseEntity<Map<String, Object>> getAllPurchases() {
        List<PurchaseEntity> purchases = purchaseService.getAllPurchases();
        Map<String, Object> body = new HashMap<>();
        body.put("success", true); // 전체 목록을 동일 구조로 반환
        body.put("items", purchases);
        body.put("count", purchases.size());
        return ResponseEntity.ok(body);
    }
    
    /**
     * 전체 구매 이력 초기화
     */
    @DeleteMapping("/reset")
    @Operation(summary = "구매 이력 초기화", description = "저장된 모든 구매 데이터를 삭제합니다")
    public ResponseEntity<Map<String, Object>> resetPurchases() {
        log.warn("구매 이력 초기화 API 호출");
        int deleted = purchaseService.resetAllPurchases();
        Map<String, Object> body = new HashMap<>();
        body.put("success", true); // 위험 작업 성공 여부 표시
        body.put("deletedCount", deleted);
        body.put("message", deleted + "개의 구매 이력이 삭제되었습니다.");
        return ResponseEntity.ok(body);
    }
}

