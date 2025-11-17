package com.onbid.controller;

import com.onbid.domain.dto.Goods;
import com.onbid.domain.dto.Request.GoodsItemsRequest;
import com.onbid.domain.dto.Request.GoodsSearchRequest;
import com.onbid.domain.entity.GoodsEntity;
import com.onbid.domain.dto.Response.GoodsResponse;
import com.onbid.service.GoodsService;
import com.onbid.service.GoodsSyncStatusService;
import com.onbid.service.OnbidApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 공매물건 REST API Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/goods")
@RequiredArgsConstructor
@Tag(name = "공매물건", description = "온비드 공매물건 조회 API")
public class GoodsApiController {
    
    private final OnbidApiService onbidApiService;
    private final GoodsService goodsService;
    private final GoodsSyncStatusService goodsSyncStatusService;
    
    /**
     * 물건 목록 조회 (기본)
     ---------------------------------- valid 작성 할 것 ---------------------------
     */

    @GetMapping
    @Operation(summary = "온비드 물건 목록 조회", description = "검색 DTO 기반으로 공매물건 목록 조회")
    public ResponseEntity<Map<String, Object>> getGoodsList(
            @Valid @ParameterObject GoodsSearchRequest request) {

        log.info("물건 목록 조회 API: pageNo={}, numOfRows={}, sido={}",
                request.getPageNo(), request.getNumOfRows(), request.getSido());

        try {
            GoodsResponse goodsResponse = onbidApiService.getGoodsListParsed(
                    request.getPageNo(),
                    request.getNumOfRows(),
                    request.getCtgrHirkId(),
                    request.getSido(),
                    request.getSgk(),
                    request.getEmd(),
                    request.getGoodsPriceFrom(),
                    request.getGoodsPriceTo(),
                    request.getOpenPriceFrom(),
                    request.getOpenPriceTo(),
                    request.getCltrNm(),
                    request.getPbctBegnDtm(),
                    request.getPbctClsDtm(),
                    request.getCltrMnmtNo()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", goodsResponse.getBody());
            response.put("header", goodsResponse.getHeader());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("물건 목록 조회 실패", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "물건 목록 조회 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/items")
    @Operation(summary = "물건 목록만 조회", description = "간단 목록을 DTO로 조회")
    public ResponseEntity<Map<String, Object>> getGoodsItems(
            @Valid @ParameterObject GoodsItemsRequest request) {

        log.info("물건 목록 간단 조회 API: pageNo={}, numOfRows={}",
                request.getPageNo(), request.getNumOfRows());

        try {
            List<Goods> items = onbidApiService.getGoodsItems(
                    request.getPageNo(),
                    request.getNumOfRows(),
                    request.getCtgrHirkId(),
                    request.getSido());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("items", items);
            response.put("count", items.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("물건 목록 조회 실패", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "물건 목록 조회 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * DB에서 물건 목록 조회
     */
    @GetMapping("/db")
    @Operation(summary = "DB에서 물건 목록 조회", description = "데이터베이스에 저장된 물건 목록을 조회합니다")
    public ResponseEntity<Map<String, Object>> getGoodsFromDB() {
        log.info("DB에서 물건 목록 조회 API 호출");
        
        try {
            List<GoodsEntity> goods = goodsService.getAllGoods();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("items", goods);
            response.put("count", goods.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("DB 물건 목록 조회 실패", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "물건 목록 조회 실패: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * DB에서 특정 물건 상세 조회
     */
    @GetMapping("/db/{historyNo}")
    @Operation(summary = "DB에서 물건 상세 조회", description = "물건이력번호로 특정 물건의 상세 정보를 조회합니다")
    public ResponseEntity<Map<String, Object>> getGoodsDetailFromDB(
            @PathVariable @Parameter(description = "물건이력번호") Long historyNo) {
        
        log.info("DB에서 물건 상세 조회 API 호출 - historyNo: {}", historyNo);
        
        try {
            GoodsEntity goods = goodsService.getGoodsByHistoryNo(historyNo);
            
            if (goods == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "물건을 찾을 수 없습니다.");
                return ResponseEntity.notFound().build();
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", goods);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("DB 물건 상세 조회 실패", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "물건 상세 조회 실패: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 물건 목록을 DB에 일괄 저장
     */
    @PostMapping("/db/batch")
    @Operation(summary = "물건 목록 일괄 저장", description = "물건 목록을 DB에 일괄 저장합니다 (기존 데이터 삭제 후 저장)")
    public ResponseEntity<Map<String, Object>> saveGoodsBatch(
            @RequestBody @Parameter(description = "저장할 물건 목록") List<Goods> goods) {
        
        log.info("물건 목록 일괄 저장 API 호출 - 개수: {}", goods != null ? goods.size() : 0);
        
        try {
            int savedCount = goodsService.saveGoodsListToDB(goods);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("savedCount", savedCount);
            response.put("message", savedCount + "개의 물건이 저장되었습니다.");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("물건 목록 저장 실패", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "물건 목록 저장 실패: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 모든 물건 데이터 삭제
     */
    @DeleteMapping("/db/all")
    @Operation(summary = "모든 물건 삭제", description = "데이터베이스의 모든 물건 데이터를 삭제합니다")
    public ResponseEntity<Map<String, Object>> deleteAllGoods() {
        
        log.info("모든 물건 삭제 API 호출");
        
        try {
            int deletedCount = goodsService.deleteAllGoods();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("deletedCount", deletedCount);
            response.put("message", deletedCount + "개의 물건이 삭제되었습니다.");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("물건 삭제 실패", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "물건 삭제 실패: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 동기화 상태 조회
     */
    @GetMapping("/refresh-status")
    @Operation(summary = "동기화 상태 조회", description = "최근 동기화시각과 다음 동기화까지 남은 시간을 반환합니다")
    public ResponseEntity<Map<String, Object>> getRefreshStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("lastSyncedAt", goodsSyncStatusService.getLastSyncedAt());
        response.put("nextSyncAt", goodsSyncStatusService.getNextSyncAt());
        response.put("secondsUntilNextSync", goodsSyncStatusService.getSecondsUntilNextSync());
        return ResponseEntity.ok(response);
    }
}

