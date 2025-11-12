package com.onbid.controller;

import com.onbid.domain.Purchase;
import com.onbid.domain.PurchaseRequest;
import com.onbid.service.PurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            @RequestBody PurchaseRequest request) {
        
        log.info("구매 생성 API 호출 - historyNo: {}, price: {}", 
                request.getHistoryNo(), request.getPurchasePrice());
        
        try {
            Purchase purchase = purchaseService.createPurchase(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", purchase);
            response.put("message", "구매가 완료되었습니다.");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("구매 생성 실패", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "구매 실패: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 특정 물건의 구매 이력 조회
     */
    @GetMapping("/{historyNo}")
    @Operation(summary = "구매 이력 조회", description = "특정 물건의 구매 이력을 조회합니다")
    public ResponseEntity<Map<String, Object>> getPurchasesByHistoryNo(
            @PathVariable @Parameter(description = "물건이력번호") Long historyNo) {
        
        log.info("구매 이력 조회 API 호출 - historyNo: {}", historyNo);
        
        try {
            List<Purchase> purchases = purchaseService.getPurchasesByHistoryNo(historyNo);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("items", purchases);
            response.put("count", purchases.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("구매 이력 조회 실패", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "구매 이력 조회 실패: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 전체 구매 이력 조회
     */
    @GetMapping
    @Operation(summary = "전체 구매 이력 조회", description = "모든 구매 이력을 조회합니다")
    public ResponseEntity<Map<String, Object>> getAllPurchases() {
        log.info("전체 구매 이력 조회 API 호출");
        
        try {
            List<Purchase> purchases = purchaseService.getAllPurchases();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("items", purchases);
            response.put("count", purchases.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("전체 구매 이력 조회 실패", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "구매 이력 조회 실패: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
}

