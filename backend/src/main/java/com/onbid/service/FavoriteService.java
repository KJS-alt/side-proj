package com.onbid.service;

import com.onbid.domain.Favorite;
import com.onbid.mapper.FavoriteMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 관심물건 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteService {
    
    private final FavoriteMapper favoriteMapper;
    
    /**
     * 관심물건 등록
     * @param userId 사용자 ID
     * @param favorite 관심물건 정보
     * @return 등록된 관심물건
     */
    @Transactional
    public Favorite addFavorite(Long userId, Favorite favorite) {
        // 중복 체크 (물건이력번호 기준)
        if (favorite.getHistoryNo() != null && 
            favoriteMapper.existsByUserIdAndHistoryNo(userId, favorite.getHistoryNo()) > 0) {
            throw new RuntimeException("이미 관심물건으로 등록되어 있습니다");
        }
        
        // 사용자 ID 설정
        favorite.setUserId(userId);
        
        // 저장
        favoriteMapper.insert(favorite);
        
        log.info("관심물건 등록 완료: userId={}, historyNo={}, goodsNo={}", 
                userId, favorite.getHistoryNo(), favorite.getGoodsNo());
        
        return favorite;
    }
    
    /**
     * 사용자의 관심물건 목록 조회
     * @param userId 사용자 ID
     * @return 관심물건 목록
     */
    @Transactional(readOnly = true)
    public List<Favorite> getFavoritesByUserId(Long userId) {
        return favoriteMapper.findByUserId(userId);
    }
    
    /**
     * 관심물건 삭제 (ID로)
     * @param id 관심물건 ID
     * @param userId 사용자 ID (소유자 확인용)
     */
    @Transactional
    public void deleteFavorite(Long id, Long userId) {
        int deleted = favoriteMapper.delete(id, userId);
        if (deleted == 0) {
            throw new RuntimeException("관심물건을 찾을 수 없거나 삭제 권한이 없습니다");
        }
        log.info("관심물건 삭제 완료: favoriteId={}, userId={}", id, userId);
    }
    
    /**
     * 관심물건 삭제 (물건번호로)
     * @param goodsNo 물건번호
     * @param userId 사용자 ID
     */
    @Transactional
    public void deleteFavoriteByGoodsNo(String goodsNo, Long userId) {
        int deleted = favoriteMapper.deleteByGoodsNo(goodsNo, userId);
        if (deleted == 0) {
            throw new RuntimeException("관심물건을 찾을 수 없습니다");
        }
        log.info("관심물건 삭제 완료: goodsNo={}, userId={}", goodsNo, userId);
    }
    
    /**
     * 관심물건 여부 확인 (물건이력번호 기준)
     * @param userId 사용자 ID
     * @param historyNo 물건이력번호
     * @return 관심물건 등록 여부
     */
    @Transactional(readOnly = true)
    public boolean isFavorite(Long userId, Long historyNo) {
        return favoriteMapper.existsByUserIdAndHistoryNo(userId, historyNo) > 0;
    }
    
    /**
     * 사용자의 관심물건 개수 조회
     * @param userId 사용자 ID
     * @return 관심물건 개수
     */
    @Transactional(readOnly = true)
    public int getFavoriteCount(Long userId) {
        return favoriteMapper.countByUserId(userId);
    }
}

