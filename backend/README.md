# 온비드 공매물건 조회 시스템 - REST API Backend

한국자산관리공사 온비드 OpenAPI를 호출해 공매물건을 조회·가공·저장하는 REST API 백엔드입니다.  
2025-11-15 기준으로 **회원/관심물건/JWT 기능은 완전히 제거**되어 공매 데이터 처리와 매매 이력 관리에만 집중합니다.

## 📋 프로젝트 개요

- **구성**: Spring Boot 백엔드 + React 프런트엔드(별도 디렉터리)
- **핵심 기능**
  - 온비드 공매물건 실시간 조회 및 필터링
  - 100건 샘플 추출 후 MariaDB에 일괄 저장/삭제
  - 저장된 물건을 기준으로 구매 이력 기록/조회
  - Swagger UI 기반 API 테스트

## 🛠️ 기술 스택

| 구분 | 사용 기술 |
|------|-----------|
| Framework | Spring Boot 3.5.7 |
| Language | Java 21 |
| Build | Gradle 8.x |
| DB Access | MyBatis (어노테이션) |
| External API | RestTemplate + JAXB |
| DB | MariaDB 11.4 |
| Docs | SpringDoc OpenAPI 2.7 |

> 🔐 **보안/인증**: 현재 제공하지 않습니다. 모든 엔드포인트는 공개 상태이며, CORS 는 `WebConfig`에서 직접 허용합니다.

## 🚀 실행 방법

1. **환경 변수 설정**
   ```powershell
   # PowerShell
   $env:ONBID_API_KEY="your-api-key"
   ```
   ```bash
   # macOS/Linux
   export ONBID_API_KEY="your-api-key"
   ```
   또는 `src/main/resources/application.properties`에서 `onbid.api.key` 값을 직접 지정합니다.

2. **데이터베이스 준비**
   ```sql
   CREATE DATABASE IF NOT EXISTS onbid CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   USE onbid;
   SOURCE src/main/resources/sql/schema.sql;
   ```

3. **애플리케이션 실행**
   ```bash
   ./gradlew bootRun
   # 또는
   ./gradlew build
   java -jar build/libs/backend-0.0.1-SNAPSHOT.jar
   ```
   기본 포트: `http://localhost:8081`

4. **Swagger UI**
   - http://localhost:8081/swagger-ui.html
   - http://localhost:8081/v3/api-docs

## 📡 주요 API

### 1. 공매물건 API (`/api/goods`)

| 메서드 | 엔드포인트 | 설명 |
|--------|------------|------|
| GET | `/api/goods` | 온비드 API에서 조건 기반 조회 |
| GET | `/api/goods/items` | 목록만 간단 조회 |
| GET | `/api/goods/xml` | XML 원본 확인 (디버깅용) |
| GET | `/api/goods/db` | DB에 저장된 물건 목록 |
| GET | `/api/goods/db/{historyNo}` | 특정 물건 상세 |
| POST | `/api/goods/db/batch` | 물건 목록 일괄 저장 |
| DELETE | `/api/goods/db/all` | DB 전체 삭제 |

### 2. 매매 API (`/api/purchases`)

| 메서드 | 엔드포인트 | 설명 |
|--------|------------|------|
| POST | `/api/purchases` | 구매 생성 |
| GET | `/api/purchases` | 전체 구매 이력 |
| GET | `/api/purchases/{historyNo}` | 특정 물건의 구매 이력 |

## 📁 디렉터리 구조

```
backend/src/main/java/com/onbid/
├── BackendApplication.java
├── config/
│   ├── SwaggerConfig.java      # Swagger 설정
│   └── WebConfig.java          # CORS 및 Front 허용
├── controller/
│   ├── GoodsApiController.java
│   └── PurchaseApiController.java
├── service/
│   ├── OnbidApiService.java    # 온비드 API + JAXB 파싱
│   ├── GoodsService.java
│   └── PurchaseService.java
├── mapper/
│   ├── GoodsMapper.java
│   └── PurchaseMapper.java
├── domain/
│   ├── Goods.java / GoodsEntity.java / GoodsResponse.java
│   ├── Purchase.java
│   └── PurchaseRequest.java
└── resources/
    ├── application.properties
    └── sql/schema.sql
```

## 🗄️ 데이터베이스 개요

`schema.sql` 에 정의된 현재 테이블은 두 개뿐입니다.

1. **goods**
   - `history_no`, `goods_name`, `min_bid_price`, `appraisal_price`, `bid_close_date`, `address` 등 핵심 열만 보관
   - 조회/정렬 인덱스: `history_no`, `bid_close_date`

2. **purchases**
   - `history_no`, `purchase_price`, `purchase_status`, `created_at`
   - `history_no`는 `goods(history_no)`를 참조하며 `ON DELETE CASCADE`

## ⚙️ 주요 설정 (application.properties)

```properties
server.port=8081

spring.datasource.url=jdbc:mariadb://localhost:3306/onbid?useUnicode=true&characterEncoding=utf8mb4&serverTimezone=Asia/Seoul
spring.datasource.username=root
spring.datasource.password=1234
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver

spring.sql.init.mode=never
spring.sql.init.schema-locations=classpath:sql/schema.sql

mybatis.type-aliases-package=com.onbid.domain
mybatis.configuration.map-underscore-to-camel-case=true
mybatis.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl

onbid.api.key=${ONBID_API_KEY}
onbid.api.url=http://openapi.onbid.co.kr/openapi/services/KamcoPblsalThingInquireSvc/getKamcoPbctCltrList
```

## 📝 참고

- `spring-boot-starter-security` 및 JWT 관련 라이브러리는 제거되었습니다.
- Swagger 보안 스키마 또한 Bearer 토큰을 요구하지 않습니다.
- 인증/사용자 테이블이 필요하면 별도 브랜치에서 복구하거나 새 모듈로 추가해주세요.

