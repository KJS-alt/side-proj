# 물건이력번호(historyNo) 도입 변경사항

## 📌 개요
- **날짜**: 2025-11-10
- **변경 목적**: 같은 물건의 회차별 구분을 위해 물건이력번호(CLTR_HSTR_NO) 추가
- **변경 범위**: 백엔드(Java, DB) + 프론트엔드(React)

---

## 🔄 주요 변경사항

### 1. 데이터 구조 변경
- **물건관리번호(goodsNo)**: 같은 물건에 대해 회차 공통 (예: `2025-09090-001`)
- **물건이력번호(historyNo)**: 회차별로 고유한 숫자 ID (예: `2060241`)

### 2. 중복 키 문제 해결
- **이전**: 물건관리번호가 여러 회차에서 중복되어 React 키 충돌 발생
- **현재**: 물건이력번호를 React 컴포넌트의 `key`로 사용하여 고유성 보장

---

## 🗄️ 백엔드 변경사항

### 1. 도메인 모델 수정

#### `Goods.java`
```java
// 새로 추가된 필드
@XmlElement(name = "CLTR_HSTR_NO")
private Long historyNo;  // 물건이력번호 (회차별 고유 식별자)
```

#### `Favorite.java`
```java
// 새로 추가된 필드
private Long historyNo;  // 물건이력번호 (회차별 고유 식별자)
```

### 2. 데이터베이스 스키마 수정

#### 신규 설치
`backend/src/main/resources/sql/schema.sql` 사용 (이미 반영됨)

#### 기존 DB 마이그레이션
```bash
# MariaDB에 접속
mysql -u root -p onbid

# 마이그레이션 실행
source backend/src/main/resources/sql/migration_add_history_no.sql
```

**마이그레이션 내용:**
- `favorites` 테이블에 `history_no BIGINT` 컬럼 추가
- UNIQUE KEY를 `(user_id, goods_no)` → `(user_id, history_no)`로 변경
- `history_no` 인덱스 추가

### 3. Mapper 수정

#### `FavoriteMapper.java`
- INSERT 쿼리에 `history_no` 추가
- SELECT 쿼리에 `history_no` 추가
- 중복 체크 메서드 변경:
  - `existsByUserIdAndGoodsNo()` → `existsByUserIdAndHistoryNo()`

### 4. Service 수정

#### `FavoriteService.java`
- 관심물건 등록 시 `historyNo` 기반 중복 체크
- 로그에 `historyNo` 정보 추가

---

## 🎨 프론트엔드 변경사항

### 1. 테이블 및 카드 컴포넌트 수정

#### `GoodsTable.jsx`
- **테이블 헤더**: "물건이력번호" + "물건관리번호" 두 컬럼으로 분리
- **React key**: `key={item.historyNo || `goods-${index}`}`
- **상세 정보**: 확장 행에 물건이력번호 표시

#### `GoodsMobileCard.jsx`
- 상세정보에 "물건이력번호" + "물건관리번호" 표시
- **React key**: `key={item.historyNo || `mobile-${index}`}`

#### `GoodsCard.jsx`
- 관심등록 시 `historyNo` 포함

### 2. 페이지 수정

#### `ListPage.jsx`
- 물건관리번호 필터에 설명 추가: "같은 물건의 모든 회차를 검색합니다"
- 모바일/데스크톱 모두 `historyNo` 기반 키 사용

#### `FavoritesPage.jsx`
- "물건이력번호" + "물건관리번호" 별도 표시

### 3. API 호출 수정

#### `api.js` (변경 불필요)
- 백엔드가 자동으로 `historyNo`를 응답에 포함하므로 프론트엔드 API 코드 수정 없음

---

## ✅ 테스트 체크리스트

### 백엔드
- [ ] 데이터베이스 마이그레이션 실행 확인
- [ ] `GET /api/goods/list` 응답에 `historyNo` 포함 확인
- [ ] 관심물건 등록 시 `history_no` DB 저장 확인
- [ ] 같은 회차 중복 등록 방지 확인

### 프론트엔드
- [ ] 물건 목록 페이지에서 "물건이력번호" 컬럼 표시 확인
- [ ] React 콘솔에 중복 키 경고 없음 확인
- [ ] 관심물건 등록/조회 시 물건이력번호 표시 확인
- [ ] 모바일 화면에서 물건이력번호 표시 확인

---

## 🚀 배포 순서

1. **데이터베이스 마이그레이션**
   ```bash
   mysql -u root -p onbid < backend/src/main/resources/sql/migration_add_history_no.sql
   ```

2. **백엔드 재시작**
   ```bash
   cd backend
   ./gradlew clean build
   ./gradlew bootRun
   ```

3. **프론트엔드 재시작**
   ```bash
   cd frontend
   npm run dev
   ```

---

## 📝 참고사항

- **하위 호환성**: 기존 관심물건 데이터는 `history_no`가 NULL이므로, 점진적으로 새 데이터로 대체됨
- **API 스펙**: Onbid API의 `CLTR_HSTR_NO` 필드를 그대로 사용
- **검색**: 물건관리번호로 검색하면 같은 물건의 모든 회차가 검색됨 (회차별 필터는 별도 구현 필요)

---

## ⚠️ 주의사항

- 마이그레이션 실행 전 **반드시 데이터베이스 백업**
- 기존 관심물건 데이터는 `history_no`가 NULL이므로, 필요시 데이터 정리 고려
- UNIQUE KEY가 변경되어 기존 `(user_id, goods_no)` 조합 중복 데이터가 있다면 마이그레이션 실패 가능

