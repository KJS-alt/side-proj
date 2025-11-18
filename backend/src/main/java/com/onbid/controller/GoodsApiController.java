package com.onbid.controller;

import com.onbid.domain.dto.Goods;
import com.onbid.domain.dto.Request.GoodsItemsRequest;
import com.onbid.domain.dto.Request.GoodsSearchRequest;
import com.onbid.domain.dto.Response.GoodsResponse;
import com.onbid.domain.entity.GoodsEntity;
import com.onbid.service.GoodsService;
import com.onbid.service.GoodsSyncStatusService;
import com.onbid.service.OnbidApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     */
    @GetMapping
    @Operation(summary = "온비드 물건 목록 조회", description = "검색 DTO 기반으로 공매물건 목록 조회")
    public ResponseEntity<Map<String, Object>> getGoodsList(
            @Valid @ParameterObject GoodsSearchRequest request) {
        
        log.info("물건 목록 조회 API: pageNo={}, numOfRows={}, sido={}",
                request.getPageNo(), request.getNumOfRows(), request.getSido());
        
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
        
        Map<String, Object> body = new HashMap<>();
        body.put("success", true); // 성공 여부를 한눈에 확인하도록 구성
        body.put("data", goodsResponse.getBody());
        body.put("header", goodsResponse.getHeader());
        return ResponseEntity.ok(body);
    }
    
    /**
     * 물건 목록만 조회
     */
    @GetMapping("/items")
    @Operation(summary = "물건 목록만 조회", description = "간단 목록을 DTO로 조회")
    public ResponseEntity<Map<String, Object>> getGoodsItems(
            @Valid @ParameterObject GoodsItemsRequest request) {
        
        List<Goods> items = onbidApiService.getGoodsItems(
                request.getPageNo(),
                request.getNumOfRows(),
                request.getCtgrHirkId(),
                request.getSido());
        
        Map<String, Object> body = new HashMap<>();
        body.put("success", true); // 단순 목록 응답임을 알리기 위한 플래그
        body.put("items", items);
        body.put("count", items.size());
        return ResponseEntity.ok(body);
    }
    
    /**
     * DB에서 물건 목록 조회
     */
    @GetMapping("/db")
    @Operation(summary = "DB에서 물건 목록 조회", description = "데이터베이스에 저장된 물건 목록을 조회합니다")
    public ResponseEntity<Map<String, Object>> getGoodsFromDB() {
        List<GoodsEntity> goods = goodsService.getAllGoods();
        Map<String, Object> body = new HashMap<>();
        body.put("success", true); // DB 조회 결과가 성공했음을 표시
        body.put("items", goods);
        body.put("count", goods.size());
        return ResponseEntity.ok(body);
    }
    
    /**
     * DB에서 특정 물건 상세 조회
     */
    @GetMapping("/db/{historyNo}")
    @Operation(summary = "DB에서 물건 상세 조회", description = "물건이력번호로 특정 물건의 상세 정보를 조회합니다")
    public ResponseEntity<Map<String, Object>> getGoodsDetailFromDB(
            @PathVariable @Parameter(description = "물건이력번호") Long historyNo) {
        
        GoodsEntity goods = goodsService.getGoodsByHistoryNo(historyNo);
        Map<String, Object> body = new HashMap<>();
        body.put("success", true); // 단건 데이터도 동일한 구조로 전달
        body.put("data", goods);
        return ResponseEntity.ok(body);
    }
    
    /**
     * 물건 목록을 DB에 일괄 저장
     */
    @PostMapping("/db/batch")
    @Operation(summary = "물건 목록 일괄 저장", description = "물건 목록을 DB에 일괄 저장합니다 (기존 데이터 삭제 후 저장)")
    public ResponseEntity<Map<String, Object>> saveGoodsBatch(
            @RequestBody @Parameter(description = "저장할 물건 목록") List<Goods> goods) {
        
        log.info("물건 목록 일괄 저장 API 호출 - 개수: {}", goods != null ? goods.size() : 0);
        int savedCount = goodsService.saveGoodsListToDB(goods);
        Map<String, Object> body = new HashMap<>();
        body.put("success", true); // 저장 성공 플래그
        body.put("savedCount", savedCount);
        body.put("message", savedCount + "개의 물건이 저장되었습니다.");
        return ResponseEntity.ok(body);
    }
    
    /**
     * 모든 물건 데이터 삭제
     */
    @DeleteMapping("/db/all")
    @Operation(summary = "모든 물건 삭제", description = "데이터베이스의 모든 물건 데이터를 삭제합니다")
    public ResponseEntity<Map<String, Object>> deleteAllGoods() {
        int deletedCount = goodsService.deleteAllGoods();
        Map<String, Object> body = new HashMap<>();
        body.put("success", true); // 삭제 결과를 플래그로 표기
        body.put("deletedCount", deletedCount);
        body.put("message", deletedCount + "개의 물건이 삭제되었습니다.");
        return ResponseEntity.ok(body);
    }
    
    /**
     * 동기화 상태 조회
     */
    @GetMapping("/refresh-status")
    @Operation(summary = "동기화 상태 조회", description = "최근 동기화시각과 다음 동기화까지 남은 시간을 반환합니다")
    public ResponseEntity<Map<String, Object>> getRefreshStatus() {
        Map<String, Object> body = new HashMap<>();
        body.put("success", true); // 동기화 지표 응답도 동일 구조 사용
        body.put("lastSyncedAt", goodsSyncStatusService.getLastSyncedAt());
        body.put("nextSyncAt", goodsSyncStatusService.getNextSyncAt());
        body.put("secondsUntilNextSync", goodsSyncStatusService.getSecondsUntilNextSync());
        return ResponseEntity.ok(body);
    }
}

