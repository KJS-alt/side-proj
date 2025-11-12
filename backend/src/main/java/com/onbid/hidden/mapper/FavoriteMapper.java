package com.onbid.mapper;

import com.onbid.domain.Favorite;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 관심물건 MyBatis Mapper (어노테이션 방식)
 */
@Mapper
public interface FavoriteMapper {
    
    /**
     * 관심물건 등록
     * @param favorite 관심물건 정보
     */
    @Insert("INSERT INTO favorites (user_id, history_no, goods_no, goods_name, min_bid_price, bid_close_date, created_at) " +
            "VALUES (#{userId}, #{historyNo}, #{goodsNo}, #{goodsName}, #{minBidPrice}, #{bidCloseDate}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Favorite favorite);
    
    /**
     * 사용자별 관심물건 목록 조회
     * @param userId 사용자 ID
     * @return 관심물건 목록
     */
    @Select("SELECT id, user_id, history_no, goods_no, goods_name, min_bid_price, bid_close_date, created_at " +
            "FROM favorites WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Favorite> findByUserId(Long userId);
    
    /**
     * 관심물건 상세 조회
     * @param id 관심물건 ID
     * @return 관심물건 정보
     */
    @Select("SELECT id, user_id, history_no, goods_no, goods_name, min_bid_price, bid_close_date, created_at " +
            "FROM favorites WHERE id = #{id}")
    Favorite findById(Long id);
    
    /**
     * 관심물건 삭제 (본인 확인)
     * @param id 관심물건 ID
     * @param userId 사용자 ID
     * @return 삭제된 행 수
     */
    @Delete("DELETE FROM favorites WHERE id = #{id} AND user_id = #{userId}")
    int delete(@Param("id") Long id, @Param("userId") Long userId);
    
    /**
     * 물건번호로 관심물건 삭제
     * @param goodsNo 물건번호
     * @param userId 사용자 ID
     * @return 삭제된 행 수
     */
    @Delete("DELETE FROM favorites WHERE goods_no = #{goodsNo} AND user_id = #{userId}")
    int deleteByGoodsNo(@Param("goodsNo") String goodsNo, @Param("userId") Long userId);
    
    /**
     * 중복 체크 (사용자가 이미 해당 회차를 관심물건으로 등록했는지)
     * @param userId 사용자 ID
     * @param historyNo 물건이력번호
     * @return 존재 여부 (1: 존재, 0: 없음)
     */
    @Select("SELECT COUNT(*) FROM favorites WHERE user_id = #{userId} AND history_no = #{historyNo}")
    int existsByUserIdAndHistoryNo(@Param("userId") Long userId, @Param("historyNo") Long historyNo);
    
    /**
     * 사용자의 관심물건 개수 조회
     * @param userId 사용자 ID
     * @return 관심물건 개수
     */
    @Select("SELECT COUNT(*) FROM favorites WHERE user_id = #{userId}")
    int countByUserId(Long userId);
}

