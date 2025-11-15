package com.onbid.controller.goods;

import com.onbid.domain.goods.dto.Goods;
import com.onbid.domain.goods.entity.GoodsEntity;
import com.onbid.domain.goods.dto.GoodsResponse;
import com.onbid.service.goods.GoodsService;
import com.onbid.service.external.OnbidApiService;
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
    
    /**
     * 물건 목록 조회 (기본)
     */
    @GetMapping
    @Operation(summary = "물건 목록 조회", description = "온비드 공매물건 목록을 조회합니다")
    public ResponseEntity<Map<String, Object>> getGoodsList(
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "1") int pageNo,
            @Parameter(description = "페이지당 건수") @RequestParam(defaultValue = "10") int numOfRows,
            @Parameter(description = "카테고리 ID") @RequestParam(required = false) String ctgrHirkId,
            @Parameter(description = "시도") @RequestParam(required = false) String sido,
            @Parameter(description = "시군구") @RequestParam(required = false) String sgk,
            @Parameter(description = "읍면동") @RequestParam(required = false) String emd,
            @Parameter(description = "물건가격 시작") @RequestParam(required = false) String goodsPriceFrom,
            @Parameter(description = "물건가격 끝") @RequestParam(required = false) String goodsPriceTo,
            @Parameter(description = "개찰가격 시작") @RequestParam(required = false) String openPriceFrom,
            @Parameter(description = "개찰가격 끝") @RequestParam(required = false) String openPriceTo,
            @Parameter(description = "물건명") @RequestParam(required = false) String cltrNm,
            @Parameter(description = "공고시작일시") @RequestParam(required = false) String pbctBegnDtm,
            @Parameter(description = "공고종료일시") @RequestParam(required = false) String pbctClsDtm,
            @Parameter(description = "물건관리번호") @RequestParam(required = false) String cltrMnmtNo) {
        
        log.info("물건 목록 조회 API: pageNo={}, numOfRows={}, sido={}", pageNo, numOfRows, sido);
        
        try {
            GoodsResponse goodsResponse = onbidApiService.getGoodsListParsed(
                    pageNo, numOfRows, ctgrHirkId, sido, sgk, emd,
                    goodsPriceFrom, goodsPriceTo, openPriceFrom, openPriceTo,
                    cltrNm, pbctBegnDtm, pbctClsDtm, cltrMnmtNo
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
    
    /**
     * 물건 목록만 간단히 조회
     */
    @GetMapping("/items")
    @Operation(summary = "물건 목록만 조회", description = "물건 목록만 간단히 조회합니다 (헤더 정보 제외)")
    public ResponseEntity<Map<String, Object>> getGoodsItems(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int numOfRows,
            @RequestParam(required = false) String ctgrHirkId,
            @RequestParam(required = false) String sido) {
        
        log.info("물건 목록 간단 조회 API: pageNo={}, numOfRows={}", pageNo, numOfRows);
        
        try {
            List<Goods> items = onbidApiService.getGoodsItems(pageNo, numOfRows, ctgrHirkId, sido);
            
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
     * XML 원본 조회 (디버깅용)
     */
    @GetMapping("/xml")
    @Operation(summary = "XML 원본 조회", description = "온비드 API의 XML 응답을 그대로 반환합니다 (디버깅용)")
    public ResponseEntity<String> getGoodsXml(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int numOfRows,
            @RequestParam(required = false) String ctgrHirkId,
            @RequestParam(required = false) String sido) {
        
        try {
            String xml = onbidApiService.getGoodsList(pageNo, numOfRows, ctgrHirkId, sido);
            return ResponseEntity.ok(xml);
        } catch (Exception e) {
            log.error("XML 조회 실패", e);
            return ResponseEntity.badRequest().body("오류: " + e.getMessage());
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
}

