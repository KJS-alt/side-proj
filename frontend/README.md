# 온비드 공매물건 조회 시스템 - Frontend

React + Vite + Tailwind CSS로 구성된 공매물건 조회/관리 전용 프런트엔드입니다.  
2025-11-15 기준으로 **로그인·회원가입·관심물건 UI** 및 관련 토글 코드는 모두 제거되었으며, 현재는 공매 데이터 탐색/DB 동기화/구매 이력 열람에 집중합니다.

## ⚙️ 기술 스택

- React 19 / Vite
- React Router v6
- Axios
- Tailwind CSS

## 📁 디렉터리 개요

```
src/
├── components/
│   ├── Header.jsx          # 상단 네비게이션
│   ├── GoodsTable.jsx      # 데스크톱 테이블 뷰
│   ├── GoodsCard.jsx       # 카드 뷰
│   ├── GoodsMobileCard.jsx # 모바일 카드
│   └── PurchaseModal.jsx   # 구매 입력 모달
├── pages/
│   ├── HomePage.jsx
│   ├── ListPage.jsx        # 조회/저장/필터링 핵심 페이지
│   ├── GoodsDetailPage.jsx
│   └── PurchasesPage.jsx
├── utils/
│   └── api.js              # 공매/구매 REST API 호출
├── App.jsx
└── main.jsx
```

## ✨ 주요 화면

- **홈**: 프로젝트 소개 및 주요 기능 요약
- **물건 목록**: 온비드 API 연동, 100건 필터링, DB 저장/삭제, 검색·정렬·페이지네이션
- **물건 상세**: 선택한 물건의 세부 정보와 구매 기록 관리
- **구매 이력**: 저장된 모든 구매 내역 조회

## 🚀 사용 방법

```bash
npm install
npm run dev
```

브라우저에서 `http://localhost:5173` 접속.  
프로덕션 빌드는 `npm run build`, 빌드 결과는 `dist/`.

## 🔌 백엔드 연동

- 기본 API 베이스 URL은 `src/utils/api.js` 에서 `http://localhost:8081/api` 로 설정되어 있습니다.
- 현재 제공되는 유틸 함수는 공매물건(`getGoodsList`, `getGoodsFromDB`, `saveGoodsToDB`, `deleteAllGoods`, `getGoodsDetail`)과 구매(`createPurchase`, `getPurchasesByHistoryNo`, `getAllPurchases`) 호출만 포함합니다.

## 🧭 라우팅

- `/` – 홈
- `/goods` – 공매물건 목록/필터
- `/goods/:historyNo` – 물건 상세
- `/purchases` – 구매 이력

## 📝 개발 메모

- 더 이상 JWT 토큰이나 로컬스토리지 기반 인증 상태를 확인하지 않습니다.
- Header, Goods 컴포넌트, API 유틸에서 관련 토글/헬퍼가 모두 제거되었습니다.
- 인증 기능을 재도입하려면 별도 브랜치에서 새 UI/상태를 설계하세요.

