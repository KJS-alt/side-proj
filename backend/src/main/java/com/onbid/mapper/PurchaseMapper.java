package com.onbid.mapper;

import com.onbid.domain.entity.Purchase;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 매매(구매) 매퍼 인터페이스
 * MyBatis 어노테이션을 통해 purchases 테이블에 접근
 */
@Mapper
public interface PurchaseMapper {
    
    /**
     * 구매 등록
     * @param purchase 구매 정보
     * @return 등록된 행의 수
     */
    @Insert("INSERT INTO purchases (history_no, purchase_price, purchase_status) " +
            "VALUES (#{historyNo}, #{purchasePrice}, #{purchaseStatus})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Purchase purchase);
    
    /**
     * 물건이력번호로 구매 이력 조회
     * @param historyNo 물건이력번호
     * @return 구매 이력 목록
     */
    @Select("SELECT id, history_no, purchase_price, purchase_status, created_at " +
            "FROM purchases WHERE history_no = #{historyNo} ORDER BY created_at DESC")
    @Results(id = "purchaseResultMap", value = {
            @Result(property = "id", column = "id", id = true),
            @Result(property = "historyNo", column = "history_no"),
            @Result(property = "purchasePrice", column = "purchase_price"),
            @Result(property = "purchaseStatus", column = "purchase_status"),
            @Result(property = "createdAt", column = "created_at")
    })
    List<Purchase> findByHistoryNo(Long historyNo);
    
    /**
     * 전체 구매 이력 조회
     * @return 구매 이력 목록
     */
    @Select("SELECT id, history_no, purchase_price, purchase_status, created_at " +
            "FROM purchases ORDER BY created_at DESC")
    @ResultMap("purchaseResultMap")
    List<Purchase> findAll();

    /**
     * 구매 데이터 초기화
     * @return 삭제된 행 수
     */
    @Delete("DELETE FROM purchases")
    int deleteAll();

    /**
     * 특정 물건이 이미 구매되었는지 개수 반환
     */
    @Select("SELECT COUNT(*) FROM purchases WHERE history_no = #{historyNo}")
    int countByHistoryNo(Long historyNo);
}

