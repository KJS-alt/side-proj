package com.onbid.mapper;

import com.onbid.domain.GoodsEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 물건 매퍼 인터페이스
 * MyBatis 어노테이션을 통해 goods 테이블에 접근
 */
@Mapper
public interface GoodsMapper {
    
    /**
     * 물건 등록 (매매에 필수적인 정보만)
     * @param goods 물건 정보
     * @return 등록된 행의 수
     */
    @Insert("INSERT INTO goods (history_no, goods_name, min_bid_price, appraisal_price, bid_close_date, address) " +
            "VALUES (#{historyNo}, #{goodsName}, #{minBidPrice}, #{appraisalPrice}, #{bidCloseDate}, #{address})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(GoodsEntity goods);
    
    /**
     * 전체 물건 조회
     * @return 물건 목록
     */
    @Select("SELECT * FROM goods ORDER BY created_at DESC")
    @Results(id = "goodsResultMap", value = {
            @Result(property = "id", column = "id", id = true),
            @Result(property = "historyNo", column = "history_no"),
            @Result(property = "goodsName", column = "goods_name"),
            @Result(property = "minBidPrice", column = "min_bid_price"),
            @Result(property = "appraisalPrice", column = "appraisal_price"),
            @Result(property = "bidCloseDate", column = "bid_close_date"),
            @Result(property = "address", column = "address"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    List<GoodsEntity> findAll();
    
    /**
     * 물건이력번호로 물건 조회
     * @param historyNo 물건이력번호
     * @return 물건 정보
     */
    @Select("SELECT * FROM goods WHERE history_no = #{historyNo}")
    @ResultMap("goodsResultMap")
    GoodsEntity findByHistoryNo(Long historyNo);
    
    /**
     * 전체 물건 개수 조회
     * @return 물건 개수
     */
    @Select("SELECT COUNT(*) FROM goods")
    int count();
    
    /**
     * 전체 물건 삭제 (초기화용)
     * @return 삭제된 행의 수
     */
    @Delete("DELETE FROM goods")
    int deleteAll();
}

