# 온비드 공매물건 조회 시스템 - 아키텍처 요약 (2025-11-15)

> ⚠️ **중요**  
> 2025-11-15 이후 버전에서는 회원/로그인/JWT/관심물건 기능이 완전히 제거되었습니다.  
> 기존 문서에 있던 해당 내용은 더 이상 유효하지 않으며, 아래 요약만 최신 상태를 반영합니다.

---

## 1. 프로젝트 개요

- **목표**: 한국자산관리공사 온비드(OpenAPI) 데이터를 조회·필터링·저장하고, 저장된 물건의 구매 이력을 관리
- **구성**: React + Vite 프런트엔드 / Spring Boot 백엔드 / MariaDB
- **현재 기능**
  1. 온비드 API 호출 및 XML → JSON 파싱
  2. 100건 추출 & DB 일괄 저장/삭제
  3. DB 기반 물건/구매 이력 조회
  4. Swagger UI/React UI를 통한 운영

---

## 2. 전체 구조

```
┌──────────────┐      ┌────────────────────┐      ┌───────────────┐
│  React SPA   │  →   │ Spring Boot REST   │  →   │  MariaDB      │
│  (Vite 5173) │      │ (8081, MyBatis)    │      │  goods/purchases
└──────────────┘      └────────────────────┘      └───────────────┘
         │                          │
         └──────── 외부 온비드 OpenAPI (XML) ─────┘
```

- **프런트엔드**: UI/상태 관리, API 호출만 담당 (JWT/토큰 없음)
- **백엔드**: 온비드 API 연동, XML 파싱, DB CRUD, 구매 로직
- **데이터베이스**: `goods`, `purchases` 두 테이블만 사용

---

## 3. 기술 스택

| 영역 | 기술 |
|------|------|
| Frontend | React 19, React Router v6, Axios, Tailwind, Vite |
| Backend | Spring Boot 3.5.7, Java 21, RestTemplate, JAXB, MyBatis |
| DB | MariaDB 11.4 (JDBC), schema.sql |
| Infra | Gradle 8.x, Swagger (springdoc-openapi 2.7) |

> 🔐 인증/보안 프레임워크(Spring Security, JWT)는 제거되었습니다.

---

## 4. 백엔드 구조

```
com.onbid
├── config
│   ├── WebConfig        # CORS
│   └── SwaggerConfig    # OpenAPI
├── controller
│   ├── GoodsApiController
│   └── PurchaseApiController
├── service
│   ├── OnbidApiService  # 외부 API + JAXB 파싱
│   ├── GoodsService
│   └── PurchaseService
├── mapper
│   ├── GoodsMapper
│   └── PurchaseMapper
└── domain
    ├── Goods / GoodsEntity / GoodsResponse
    ├── Purchase
    └── PurchaseRequest
```

### 주요 흐름
1. `GoodsApiController` → `OnbidApiService` → 온비드 REST 호출 (XML)
2. JAXB로 파싱 → DTO 변환 → `GoodsService` 저장/조회
3. `PurchaseApiController` → `PurchaseService` → DB CRUD

---

## 5. 프런트엔드 구조

```
src/
├── components
│   ├── Header
│   ├── GoodsTable / GoodsCard / GoodsMobileCard
│   └── PurchaseModal
├── pages
│   ├── HomePage
│   ├── ListPage        # API 조회 + DB 저장/검색 UI
│   ├── GoodsDetailPage # 구매 입력/조회
│   └── PurchasesPage
├── utils/api.js        # Axios 인스턴스 (token 없음)
├── App.jsx             # 라우팅 정의
└── main.jsx
```

- React Router 경로: `/`, `/goods`, `/goods/:historyNo`, `/purchases`
- `utils/api.js` 는 goods/purchases 관련 함수만 제공

---

## 6. 데이터 흐름 (ListPage 기준)

1. **API 조회** 버튼  
   - `getGoodsList` → `/api/goods` → 온비드 API 호출 → 결과 표시
2. **100개 조회**  
   - 프런트에서 최신 회차 기준 100건 필터링
3. **DB 저장**  
   - `saveGoodsToDB` → `/api/goods/db/batch` → MyBatis upsert
4. **DB 조회**  
   - `getGoodsFromDB` → `/api/goods/db`
5. **구매 등록**  
   - 상세 페이지 → `createPurchase` → `/api/purchases`

---

## 7. API 요약

| 카테고리 | 엔드포인트 | 설명 |
|----------|------------|------|
| Goods | `GET /api/goods` | 온비드 API 중계 |
|       | `GET /api/goods/items` | 간단 목록 |
|       | `GET /api/goods/xml` | XML Raw |
|       | `GET /api/goods/db` | 저장된 물건 전체 |
|       | `GET /api/goods/db/{historyNo}` | 단건 조회 |
|       | `POST /api/goods/db/batch` | 일괄 저장 |
|       | `DELETE /api/goods/db/all` | 전체 삭제 |
| Purchases | `POST /api/purchases` | 구매 생성 |
|          | `GET /api/purchases` | 전체 구매 목록 |
|          | `GET /api/purchases/{historyNo}` | 특정 물건 구매 |

---

## 8. 데이터베이스

### goods
```
history_no (UNIQUE), goods_name, min_bid_price, appraisal_price,
bid_close_date, address, created_at, updated_at
```

### purchases
```
id, history_no (FK → goods.history_no), purchase_price,
purchase_status (기본 COMPLETED), created_at
```

> 관심물건 `favorites`, 사용자 `users` 테이블은 더 이상 존재하지 않습니다.

---

## 9. 향후 확장 시 참고

- 인증/즐겨찾기를 다시 도입하려면 별도 모듈로 구현하고, JWT/보안 설정을 신규 작성해야 합니다.
- 프런트엔드는 “컴포넌트 배포”에만 집중하도록 유지하고, 상태/비즈니스 로직은 훅 또는 서비스 계층으로 분리하는 것을 권장합니다.
- 백엔드는 `OnbidApiService`와 `GoodsService` 사이에 Scheduler 또는 Batch 모듈을 추가해 자동 동기화를 확장할 수 있습니다.

--- 

> 이 문서는 실제 코드 구조를 기준으로 간결하게 유지됩니다.  
> 더 오래된 상세 설명이 필요하면 이전 커밋의 문서를 참조하세요.

