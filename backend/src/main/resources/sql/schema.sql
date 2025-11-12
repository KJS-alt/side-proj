-- 1. goods 테이블: 물건 정보
DROP TABLE IF EXISTS purchases;
DROP TABLE IF EXISTS goods;


CREATE TABLE goods (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '물건 ID',
    history_no BIGINT UNIQUE NOT NULL COMMENT '물건이력번호 (회차별 고유 식별자)',
    goods_name VARCHAR(1000) NOT NULL COMMENT '물건명',
    min_bid_price BIGINT NOT NULL COMMENT '최저입찰가',
    appraisal_price BIGINT COMMENT '감정가',
    bid_close_date VARCHAR(14) NOT NULL COMMENT '입찰마감일시 (YYYYMMDDHHmmss)',
    address VARCHAR(1000) COMMENT '물건소재지',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    INDEX idx_history_no (history_no),
    INDEX idx_bid_close_date (bid_close_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='물건';

-- 2. purchases 테이블: 매매 정보
CREATE TABLE purchases (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '매매 ID',
    history_no BIGINT NOT NULL COMMENT '물건이력번호',
    purchase_price BIGINT NOT NULL COMMENT '구매가격',
    purchase_status VARCHAR(20) DEFAULT 'COMPLETED' COMMENT '구매상태 (PENDING, COMPLETED, CANCELLED)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '구매일시',
    FOREIGN KEY (history_no) REFERENCES goods(history_no) ON DELETE CASCADE,
    INDEX idx_history_no (history_no),
    INDEX idx_purchase_status (purchase_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='매매';
