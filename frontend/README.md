# 온비드 공매물건 조회 시스템 - Frontend

React + Vite + Tailwind CSS로 구현된 온비드 공매물건 조회 웹 애플리케이션의 프론트엔드입니다.

## 기술 스택

- **React 19**: UI 라이브러리
- **Vite**: 빌드 도구
- **React Router v6**: 클라이언트 사이드 라우팅
- **Axios**: HTTP 클라이언트
- **Tailwind CSS**: 유틸리티 기반 CSS 프레임워크

## 프로젝트 구조

```
src/
├── components/          # 재사용 가능한 컴포넌트
│   ├── Header.jsx      # 상단 네비게이션 바
│   ├── GoodsCard.jsx   # 물건 정보 카드
│   └── PrivateRoute.jsx # 인증 보호 라우트
├── pages/              # 페이지 컴포넌트
│   ├── HomePage.jsx    # 메인 페이지
│   ├── ListPage.jsx    # 물건 목록 페이지
│   ├── LoginPage.jsx   # 로그인 페이지
│   ├── RegisterPage.jsx # 회원가입 페이지
│   └── FavoritesPage.jsx # 관심물건 페이지
├── utils/
│   └── api.js          # API 통신 유틸리티
├── App.jsx             # 메인 앱 컴포넌트
└── main.jsx            # 앱 진입점
```

## 주요 기능

### 1. 사용자 인증
- 회원가입
- 로그인/로그아웃
- JWT 토큰 기반 인증

### 2. 공매물건 조회
- 최근 물건 조회
- 물건 목록 페이징
- 지역별 필터링

### 3. 관심물건 관리
- 관심물건 등록
- 관심물건 목록 조회
- 관심물건 삭제

## 설치 및 실행

### 1. 의존성 설치

```bash
npm install
```

### 2. 개발 서버 실행

```bash
npm run dev
```

개발 서버가 실행되면 브라우저에서 `http://localhost:5173`으로 접속할 수 있습니다.

### 3. 프로덕션 빌드

```bash
npm run build
```

빌드된 파일은 `dist` 폴더에 생성됩니다.

### 4. 프로덕션 미리보기

```bash
npm run preview
```

## 환경 설정

백엔드 API 서버 주소는 `src/utils/api.js`에서 설정할 수 있습니다.

```javascript
const api = axios.create({
  baseURL: 'http://localhost:8081/api',  // 백엔드 서버 주소
  // ...
});
```

## API 엔드포인트

### 사용자
- `POST /api/users/register` - 회원가입
- `POST /api/users/login` - 로그인
- `GET /api/users/me` - 내 정보 조회

### 공매물건
- `GET /api/goods` - 물건 목록 조회
- `GET /api/goods/items` - 물건 목록 간단 조회

### 관심물건
- `GET /api/favorites` - 관심물건 목록
- `POST /api/favorites` - 관심물건 등록
- `DELETE /api/favorites/{id}` - 관심물건 삭제
- `GET /api/favorites/check/{goodsNo}` - 관심물건 여부 확인

## 페이지 라우팅

- `/` - 홈 페이지
- `/goods` - 물건 목록
- `/login` - 로그인
- `/register` - 회원가입
- `/favorites` - 관심물건 (로그인 필요)

## 개발 가이드

### 새 페이지 추가

1. `src/pages/` 디렉토리에 페이지 컴포넌트 생성
2. `src/App.jsx`에 라우트 추가

### API 함수 추가

`src/utils/api.js`에 새로운 API 호출 함수를 추가합니다.

### 스타일링

Tailwind CSS 유틸리티 클래스를 사용하여 스타일링합니다.

```jsx
<button className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700">
  버튼
</button>
```

## 브라우저 지원

- Chrome (최신 버전)
- Firefox (최신 버전)
- Safari (최신 버전)
- Edge (최신 버전)

## 라이선스

이 프로젝트는 개인 학습 목적으로 작성되었습니다.
