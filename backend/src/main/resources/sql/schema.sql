-- 온비드 공매물건 조회 시스템 데이터베이스 스키마
-- Database: onbid

-- 데이터베이스 생성 (이미 생성되어 있다면 스킵)
-- CREATE DATABASE IF NOT EXISTS onbid CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- USE onbid;

-- 1. users 테이블: 사용자 정보
DROP TABLE IF EXISTS favorites;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '사용자 ID',
    email VARCHAR(100) UNIQUE NOT NULL COMMENT '이메일',
    password VARCHAR(255) NOT NULL COMMENT '암호화된 비밀번호 (BCrypt)',
    username VARCHAR(50) NOT NULL COMMENT '사용자명',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사용자';

-- 2. favorites 테이블: 관심물건
CREATE TABLE favorites (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '관심물건 ID',
    user_id BIGINT NOT NULL COMMENT '사용자 ID',
    history_no BIGINT COMMENT '물건이력번호 (회차별 고유 식별자)',
    goods_no VARCHAR(50) NOT NULL COMMENT '물건관리번호',
    goods_name VARCHAR(500) COMMENT '물건명',
    min_bid_price BIGINT COMMENT '최저입찰가',
    bid_close_date VARCHAR(14) COMMENT '입찰마감일시 (YYYYMMDDHHmmss)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '등록일시',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_history (user_id, history_no),
    INDEX idx_user_id (user_id),
    INDEX idx_goods_no (goods_no),
    INDEX idx_history_no (history_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='관심물건';

-- 초기 테스트 데이터 (선택사항)
-- INSERT INTO users (email, password, username) 
-- VALUES ('test@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '테스트사용자');
-- 비밀번호: password123

