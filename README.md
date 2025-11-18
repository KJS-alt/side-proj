# side-proj

# 온비드 공매물건 조회 시스템

---

## 1. 빠른 요약

- **목표**: 온비드 XML 데이터를 조회 → 100건 선별 → DB 저장 → 구매 이력 관리  
- **구성**: React + Vite 프런트엔드, Spring Boot 백엔드, MariaDB  
- **응답 규칙**: 모든 REST 응답은 `success`, `data|items`, `message`, `errorCode` 키만 사용하는 단순 Map

---

## 2. 기술 스택

| 영역 | 사용 기술 | 비고 |
|------|-----------|------|
| Frontend | React 19, React Router 7, Axios, Tailwind, Vite | UI/상태 + API 호출만 담당 |
| Backend | Spring Boot 3.5.7, Java 21, RestTemplate, Jackson XmlMapper, MyBatis | XML 파싱 → 도메인 변환 |
| DB | MariaDB 11.4 | `schema.sql` 로 테이블 초기화 |
| 공통 도구 | Gradle 8.x, Swagger(OpenAPI 2.7) | 8081 포트에서 Swagger UI 제공 |

---

## 3. 전체 흐름

```
React(Vite) ──→ Spring Boot (8081) ──→ MariaDB
     │                 │
     └────── 온비드 OpenAPI(XML) ─────┘
```

- 프런트: 화면과 상태만 담당, 모든 데이터는 `/api/...` 호출로 가져옴  
- 백엔드: 온비드 XML 호출·파싱, DB upsert, 구매 로직, 스케줄러  
- DB: `goods_basic`, `goods_price`, `purchases` 세 테이블만 사용

---

## 4. 프런트엔드 구성 (간단 설명)

```
src/
├── components/        # 재사용 UI (Header, GoodsCard, GoodsTable 등)
├── pages/             # 라우트별 화면
│   ├─ HomePage        # 최근 물건 6건 + CTA
│   ├─ ListPage        # API 조회·필터·저장·삭제를 한 화면에 모음
│   ├─ GoodsDetailPage # 단건 조회 + 구매 버튼
│   └─ PurchasesPage   # 구매 내역 + 초기화 버튼
├── utils/api.js       # axios 인스턴스 + 모든 API 함수
├── App.jsx            # Router 설정 (/, /goods, /goods/:historyNo, /purchases)
└── main.jsx           # Vite 엔트리
```

Tailwind 클래스로만 스타일을 주고, 컴포넌트는 “UI를 뿌리는 역할”에 집중합니다.

---

## 5. 주요 화면 기능

| 화면 | 주요 역할 |
|------|-----------|
| 홈(HomePage) | DB에서 최신 6건을 보여주고 `/goods` 로 이동 안내 |
| 목록(ListPage) | API 조회 → 100건 추출 → DB 저장/삭제 → 필터링 → 테이블+카드 뷰 |
| 상세(GoodsDetailPage) | DB 단건 조회, 구매 상태 확인, 구매 모달 |
| 구매 목록(PurchasesPage) | 구매 내역 표와 초기화 버튼, 상세 페이지로 이동 링크 |

---

## 6. 백엔드 패키지 맵

```
com.onbid
├── config/                 # Swagger, CORS
├── controller/             # Goods, Purchase REST 엔드포인트
├── domain/
│   ├── dto/                # 내부 도메인 DTO (XML 의존성 없음)
│   └── entity/             # MyBatis 매핑용 엔티티
├── exception/              # BusinessException + ErrorCode + 전역 핸들러
├── mapper/
│   ├── GoodsMapper / PurchaseMapper      # MyBatis
│   └── OnbidGoodsMapper                  # Raw XML → 도메인 변환
├── openapi/xml/            # Jackson XmlMapper가 읽는 Raw DTO
└── service/
    ├── OnbidApiService     # RestTemplate 호출 + XmlMapper + Mapper
    ├── GoodsService        # DB 삭제, 조회
    ├── PurchaseService     # 구매 중복 체크 + 저장
    ├── GoodsSyncScheduler  # 1분마다 100건 저장
    └── GoodsSyncStatusService # 최근 동기화 시간 저장
```

**핵심 흐름**  
1. `OnbidApiService`가 외부 XML을 Jackson `XmlMapper`로 `OnbidResponseRaw`에 담음  
2. `OnbidGoodsMapper`가 Raw DTO → `GoodsResponse` → `Goods` 리스트로 변환  
3. `GoodsService`가 `GoodsMapper`를 통해 `goods_basic`, `goods_price`에 upsert  
4. `GoodsApiController` / `PurchaseApiController` 는 Map 응답(`success`, `data`, `message`)만 작성  
5. 실패 시 `GlobalExceptionHandler` 가 동일한 구조로 에러 응답을 내려줌

---

## 7. 데이터·스케줄러 흐름

1. **ListPage - API 조회** : `/api/goods` → 온비드 API 호출 → 결과만 화면에 표시  
2. **100개 추출** : 프런트에서 historyNo 기준 최신 100건만 남김  
3. **DB 저장** : `/api/goods/db/batch` → MyBatis upsert → 저장 건수 반환  
4. **스케줄러** : `GoodsSyncScheduler` 가 1분마다 1,000건 조회 → 100건 선별 → DB 저장 → `GoodsSyncStatusService` 업데이트  
5. **구매** : `/api/purchases` 로 historyNo + 금액 전달 → 중복 검사 후 저장  
6. **구매 목록** : `/api/purchases`, `/api/purchases/{historyNo}` 로 단순 조회

---

## 8. API 한눈에 보기

| 구분 | 메서드/경로 | 설명 |
|------|-------------|------|
| 물건 | `GET /api/goods` | 온비드 API 프록시 (필터 파라미터 동일) |
|      | `GET /api/goods/items` | 간단 목록 |
|      | `GET /api/goods/db` | DB에 저장된 전체 목록 |
|      | `GET /api/goods/db/{historyNo}` | 단건 조회 |
|      | `POST /api/goods/db/batch` | 목록 일괄 저장 |
|      | `DELETE /api/goods/db/all` | 전체 삭제 |
| 동기화 | `GET /api/goods/refresh-status` | 마지막 동기화 시간 |
| 구매 | `POST /api/purchases` | 구매 생성 (중복 시 에러 코드) |
|      | `GET /api/purchases` | 전체 구매 목록 |
|      | `GET /api/purchases/{historyNo}` | 단일 물건 구매 내역 |
|      | `DELETE /api/purchases/reset` | 구매 목록 초기화 |

---

## 9. DB 테이블 구조 (요약)

| 테이블 | 주요 컬럼 |
|--------|-----------|
| `goods_basic` | `history_no`(PK), `goods_name`, `status_name`, `sale_type_name`, `category_name`, `bid_start_date`, `bid_close_date`, `address`, `created_at`, `updated_at` |
| `goods_price` | `history_no`(FK), `min_bid_price`, `appraisal_price`, `fee_rate`, `inquiry_count`, `favorite_count`, `updated_at` |
| `purchases` | `id`, `history_no`(FK), `purchase_price`, `purchase_status`(기본 `COMPLETED`), `created_at` |

---

## 10. 빌드 & 실행 핵심

1. **백엔드**  
   ```bash
   cd backend
   ./gradlew bootRun   # 포트 8081
   ```
2. **프런트엔드**  
   ```bash
   cd frontend
   npm install
   npm run dev         # 기본 포트 5173
   ```
3. **환경 변수**  
   - `ONBID_API_KEY` 를 OS 환경 변수로 넣거나 `backend/src/main/resources/application.properties` 에 직접 작성
4. **시연용 싱글 포트**  
   - `npm run build:embed` → 정적 파일이 `backend/src/main/resources/static` 으로 복사되어 8081에서 API+UI 동시 제공

---

> 이 문서는 초보자도 이해하기 쉽도록 불필요한 개념을 모두 제거한 버전입니다.  
> 더 세밀한 정보가 필요하면 각 폴더의 주석(모두 한글)과 Swagger UI를 함께 참고하세요.


