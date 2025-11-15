-- 1. 테이블 초기화
DROP TABLE IF EXISTS purchases;
DROP TABLE IF EXISTS goods_price;
DROP TABLE IF EXISTS goods_basic;

-- 2. 공매 기본 정보
CREATE TABLE goods_basic (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '기본 ID',
    history_no BIGINT UNIQUE NOT NULL COMMENT '물건이력번호',
    goods_name VARCHAR(1000) NOT NULL COMMENT '물건명',
    status_name VARCHAR(100) COMMENT '물건상태',
    sale_type_name VARCHAR(100) COMMENT '처분방식명',
    category_name VARCHAR(200) COMMENT '카테고리',
    bid_start_date VARCHAR(14) COMMENT '입찰시작일시',
    bid_close_date VARCHAR(14) NOT NULL COMMENT '입찰마감일시',
    address VARCHAR(1000) COMMENT '물건소재지',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '입력일시',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    INDEX idx_history_no (history_no),
    INDEX idx_bid_close_date (bid_close_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='공매기본';

-- 3. 공매 가격 정보
CREATE TABLE goods_price (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '가격 ID',
    history_no BIGINT NOT NULL UNIQUE COMMENT '물건이력번호',
    min_bid_price BIGINT NOT NULL COMMENT '최저입찰가',
    appraisal_price BIGINT COMMENT '감정가',
    fee_rate VARCHAR(20) COMMENT '최저입찰가율',
    inquiry_count INT COMMENT '조회수',
    favorite_count INT COMMENT '관심수',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '가격기준일',
    FOREIGN KEY (history_no) REFERENCES goods_basic(history_no) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='공매가격';

-- 4. 구매 이력
CREATE TABLE purchases (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '매매 ID',
    history_no BIGINT NOT NULL COMMENT '물건이력번호',
    purchase_price BIGINT NOT NULL COMMENT '구매가격',
    purchase_status VARCHAR(20) DEFAULT 'COMPLETED' COMMENT '구매상태 (PENDING, COMPLETED, CANCELLED)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '구매일시',
    FOREIGN KEY (history_no) REFERENCES goods_basic(history_no),
    INDEX idx_history_no (history_no),
    INDEX idx_purchase_status (purchase_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='매매';
