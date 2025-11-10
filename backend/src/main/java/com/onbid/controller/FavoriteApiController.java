package com.onbid.controller;

import com.onbid.domain.Favorite;
import com.onbid.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 관심물건 REST API Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@Tag(name = "관심물건", description = "관심물건 관련 API")
@SecurityRequirement(name = "bearer-token")
public class FavoriteApiController {
    
    private final FavoriteService favoriteService;
    
    /**
     * 내 관심물건 목록 조회
     */
    @GetMapping
    @Operation(summary = "관심물건 목록", description = "현재 사용자의 관심물건 목록을 조회합니다")
    public ResponseEntity<Map<String, Object>> getFavorites(Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            
            List<Favorite> favorites = favoriteService.getFavoritesByUserId(userId);
            int count = favoriteService.getFavoriteCount(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", favorites);
            response.put("count", count);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("관심물건 목록 조회 실패", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 관심물건 등록
     */
    @PostMapping
    @Operation(summary = "관심물건 등록", description = "새로운 관심물건을 등록합니다")
    public ResponseEntity<Map<String, Object>> addFavorite(
            @RequestBody Favorite favorite,
            Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            
            Favorite created = favoriteService.addFavorite(userId, favorite);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "관심물건이 등록되었습니다");
            response.put("data", created);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("관심물건 등록 실패", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 관심물건 삭제 (ID로)
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "관심물건 삭제", description = "관심물건을 삭제합니다 (ID)")
    public ResponseEntity<Map<String, Object>> deleteFavorite(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            
            favoriteService.deleteFavorite(id, userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "관심물건이 삭제되었습니다");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("관심물건 삭제 실패", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 관심물건 삭제 (물건번호로)
     */
    @DeleteMapping("/goods/{goodsNo}")
    @Operation(summary = "관심물건 삭제 (물건번호)", description = "관심물건을 삭제합니다 (물건번호)")
    public ResponseEntity<Map<String, Object>> deleteFavoriteByGoodsNo(
            @PathVariable String goodsNo,
            Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            
            favoriteService.deleteFavoriteByGoodsNo(goodsNo, userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "관심물건이 삭제되었습니다");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("관심물건 삭제 실패", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 관심물건 여부 확인 (물건이력번호 기준)
     */
    @GetMapping("/check/{historyNo}")
    @Operation(summary = "관심물건 여부 확인", description = "특정 회차가 관심물건으로 등록되어 있는지 확인합니다 (물건이력번호 기준)")
    public ResponseEntity<Map<String, Object>> checkFavorite(
            @PathVariable Long historyNo,
            Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            
            boolean isFavorite = favoriteService.isFavorite(userId, historyNo);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("isFavorite", isFavorite);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("관심물건 확인 실패", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
}

