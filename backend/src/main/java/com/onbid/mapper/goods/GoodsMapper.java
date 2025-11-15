package com.onbid.mapper.goods;

import com.onbid.domain.goods.entity.GoodsBasicEntity;
import com.onbid.domain.goods.entity.GoodsEntity;
import com.onbid.domain.goods.entity.GoodsPriceEntity;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface GoodsMapper {

    @Insert("""
            INSERT INTO goods_basic
                (history_no, goods_name, status_name, sale_type_name, category_name, bid_start_date, bid_close_date, address)
            VALUES
                (#{historyNo}, #{goodsName}, #{statusName}, #{saleTypeName}, #{categoryName},
                 #{bidStartDate}, #{bidCloseDate}, #{address})
            ON DUPLICATE KEY UPDATE
                goods_name = VALUES(goods_name),
                status_name = VALUES(status_name),
                sale_type_name = VALUES(sale_type_name),
                category_name = VALUES(category_name),
                bid_start_date = VALUES(bid_start_date),
                bid_close_date = VALUES(bid_close_date),
                address = VALUES(address),
                updated_at = CURRENT_TIMESTAMP
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int upsertBasic(GoodsBasicEntity entity);

    @Insert("""
            INSERT INTO goods_price
                (history_no, min_bid_price, appraisal_price, fee_rate, inquiry_count, favorite_count)
            VALUES
                (#{historyNo}, #{minBidPrice}, #{appraisalPrice}, #{feeRate}, #{inquiryCount}, #{favoriteCount})
            ON DUPLICATE KEY UPDATE
                min_bid_price = VALUES(min_bid_price),
                appraisal_price = VALUES(appraisal_price),
                fee_rate = VALUES(fee_rate),
                inquiry_count = VALUES(inquiry_count),
                favorite_count = VALUES(favorite_count),
                updated_at = CURRENT_TIMESTAMP
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int upsertPrice(GoodsPriceEntity entity);

    @Select("""
            SELECT
                gb.id,
                gb.history_no,
                gb.goods_name,
                gb.status_name,
                gb.sale_type_name,
                gb.category_name,
                gb.bid_start_date,
                gb.bid_close_date,
                gb.address,
                gb.created_at,
                gb.updated_at,
                gp.min_bid_price,
                gp.appraisal_price,
                gp.fee_rate,
                gp.inquiry_count,
                gp.favorite_count
            FROM goods_basic gb
            LEFT JOIN goods_price gp ON gp.history_no = gb.history_no
            ORDER BY gb.created_at DESC
            """)
    @Results(id = "goodsResultMap", value = {
            @Result(property = "id", column = "id", id = true),
            @Result(property = "historyNo", column = "history_no"),
            @Result(property = "goodsName", column = "goods_name"),
            @Result(property = "statusName", column = "status_name"),
            @Result(property = "saleTypeName", column = "sale_type_name"),
            @Result(property = "categoryName", column = "category_name"),
            @Result(property = "bidStartDate", column = "bid_start_date"),
            @Result(property = "bidCloseDate", column = "bid_close_date"),
            @Result(property = "address", column = "address"),
            @Result(property = "minBidPrice", column = "min_bid_price"),
            @Result(property = "appraisalPrice", column = "appraisal_price"),
            @Result(property = "feeRate", column = "fee_rate"),
            @Result(property = "inquiryCount", column = "inquiry_count"),
            @Result(property = "favoriteCount", column = "favorite_count"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    List<GoodsEntity> findAll();

    @Select("""
            SELECT
                gb.id,
                gb.history_no,
                gb.goods_name,
                gb.status_name,
                gb.sale_type_name,
                gb.category_name,
                gb.bid_start_date,
                gb.bid_close_date,
                gb.address,
                gb.created_at,
                gb.updated_at,
                gp.min_bid_price,
                gp.appraisal_price,
                gp.fee_rate,
                gp.inquiry_count,
                gp.favorite_count
            FROM goods_basic gb
            LEFT JOIN goods_price gp ON gp.history_no = gb.history_no
            WHERE gb.history_no = #{historyNo}
            """)
    @ResultMap("goodsResultMap")
    GoodsEntity findByHistoryNo(Long historyNo);

    @Select("SELECT COUNT(*) FROM goods_basic")
    int count();

    @Delete("DELETE FROM goods_price")
    int deleteAllPrices();

    @Delete("DELETE FROM goods_basic")
    int deleteAllBasics();
}
