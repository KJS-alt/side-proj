# 온비드 공매물건 조회 시스템 - 프로젝트 아키텍처 문서

---

## 📋 목차

1. [프로젝트 개요](#1-프로젝트-개요)
2. [전체 아키텍처](#2-전체-아키텍처)
3. [기술 스택](#3-기술-스택)
4. [백엔드 구조](#4-백엔드-구조)
5. [프론트엔드 구조](#5-프론트엔드-구조)
6. [데이터 흐름](#6-데이터-흐름)
7. [API 엔드포인트](#7-api-엔드포인트)
8. [데이터베이스](#8-데이터베이스)
9. [보안 및 인증](#9-보안-및-인증)
10. [주요 기능](#10-주요-기능)

---

## 1. 프로젝트 개요

### 1.1 프로젝트 소개

**온비드 공매물건 조회 시스템**은 한국자산관리공사(KAMCO)의 온비드 OpenAPI를 활용하여 공매물건을 조회하고 관심물건을 관리할 수 있는 풀스택 웹 애플리케이션입니다.

### 1.2 주요 기능

- ✅ **회원 관리**: 회원가입, 로그인, 내 정보 조회/수정, 회원 탈퇴
- ✅ **공매물건 조회**: 온비드 API를 통한 실시간 공매물건 조회
- ✅ **검색 및 필터링**: 지역, 가격, 카테고리, 입찰일자 등 다양한 조건으로 검색
- ✅ **관심물건 관리**: 관심 있는 물건을 등록하고 관리
- ✅ **반응형 UI**: 모바일, 태블릿, 데스크톱 모든 디바이스 지원

### 1.3 개발 환경

- **개발 기간**: 약 1주 (7일)
- **개발자**: Side Project Team
- **목적**: 학습 및 포트폴리오

---

## 2. 전체 아키텍처

### 2.1 아키텍처 다이어그램

```
┌─────────────────┐
│   사용자        │
│   (브라우저)    │
└────────┬────────┘
         │ HTTP
         ↓
┌─────────────────────────────────────────────┐
│          Frontend (React + Vite)            │
│  ┌────────────┬──────────────┬───────────┐  │
│  │   Pages    │  Components  │   Utils   │  │
│  │  (화면)    │   (UI 조각)  │  (API 호출)│  │
│  └────────────┴──────────────┴───────────┘  │
└────────┬────────────────────────────────────┘
         │ REST API (JSON)
         │ JWT Token
         ↓
┌─────────────────────────────────────────────┐
│      Backend (Spring Boot + Java)          │
│  ┌──────────────────────────────────────┐  │
│  │        Controller (API 엔드포인트)    │  │
│  └────────────┬─────────────────────────┘  │
│               ↓                             │
│  ┌──────────────────────────────────────┐  │
│  │     Service (비즈니스 로직)          │  │
│  └────────┬────────────┬────────────────┘  │
│           ↓            ↓                    │
│  ┌────────────┐  ┌───────────────────┐     │
│  │   Mapper   │  │  OnBid API        │     │
│  │  (MyBatis) │  │  Service          │     │
│  └─────┬──────┘  └────────┬──────────┘     │
│        ↓                  ↓                 │
└────────┼──────────────────┼─────────────────┘
         │                  │
         ↓                  ↓
┌─────────────────┐  ┌──────────────────────┐
│    MariaDB      │  │  온비드 OpenAPI      │
│   (Database)    │  │  (외부 API)          │
└─────────────────┘  └──────────────────────┘
```

### 2.2 시스템 구성 요소

#### Frontend (프론트엔드)
- **역할**: 사용자 인터페이스 제공
- **위치**: `frontend/` 디렉토리
- **포트**: 5173 (개발 서버)
- **통신**: Backend와 REST API로 통신

#### Backend (백엔드)
- **역할**: 비즈니스 로직 처리 및 API 제공
- **위치**: `backend/` 디렉토리
- **포트**: 8081
- **통신**: Frontend의 요청 처리, DB 연결, 외부 API 호출

#### Database (데이터베이스)
- **역할**: 사용자 정보 및 관심물건 저장
- **DBMS**: MariaDB
- **포트**: 3306
- **스키마**: `onbid`

#### 외부 API
- **역할**: 공매물건 정보 제공
- **제공**: 한국자산관리공사 온비드
- **형식**: XML 응답

---

## 3. 기술 스택

### 3.1 Frontend (프론트엔드)

| 기술 | 버전 | 역할 |
|------|------|------|
| **React** | 19 | UI 라이브러리 - 컴포넌트 기반 화면 구성 |
| **Vite** | 최신 | 빠른 빌드 도구 |
| **React Router** | v6 | 페이지 라우팅 (URL 관리) |
| **Axios** | 최신 | HTTP 통신 라이브러리 |
| **Tailwind CSS** | 최신 | 유틸리티 기반 CSS 프레임워크 |
| **JavaScript** | ES6+ | 프로그래밍 언어 |

### 3.2 Backend (백엔드)

| 기술 | 버전 | 역할 |
|------|------|------|
| **Spring Boot** | 3.5.7 | 웹 애플리케이션 프레임워크 |
| **Java** | 21 | 프로그래밍 언어 |
| **Spring Security** | 최신 | 보안 및 인증 처리 |
| **JWT** | - | 토큰 기반 인증 (jjwt 라이브러리) |
| **MyBatis** | 최신 | SQL 매핑 프레임워크 (어노테이션 방식) |
| **MariaDB Driver** | 최신 | 데이터베이스 연결 드라이버 |
| **SpringDoc OpenAPI** | 최신 | Swagger API 문서 자동 생성 |
| **Lombok** | 최신 | 보일러플레이트 코드 자동 생성 |
| **JAXB** | 최신 | XML 파싱 라이브러리 |
| **Gradle** | 8.x | 빌드 도구 |

### 3.3 Database (데이터베이스)

| 기술 | 버전 | 역할 |
|------|------|------|
| **MariaDB** | 11.4 | 관계형 데이터베이스 |

### 3.4 외부 API

| 서비스 | 제공 | 역할 |
|--------|------|------|
| **온비드 OpenAPI** | 한국자산관리공사 | 공매물건 정보 제공 |

---

## 4. 백엔드 구조

### 4.1 전체 디렉토리 구조

```
backend/src/main/java/com/onbid/
├── BackendApplication.java          # 메인 애플리케이션 진입점
├── config/                           # 설정 클래스
│   ├── SwaggerConfig.java           # Swagger 문서 설정
│   └── WebConfig.java               # CORS 설정
├── controller/                       # REST API 컨트롤러 (요청 처리)
│   ├── UserApiController.java       # 사용자 API
│   ├── FavoriteApiController.java   # 관심물건 API
│   └── GoodsApiController.java      # 공매물건 API
├── service/                          # 비즈니스 로직
│   ├── UserService.java             # 사용자 서비스
│   ├── FavoriteService.java         # 관심물건 서비스
│   └── OnbidApiService.java         # 온비드 API 호출 서비스
├── mapper/                           # MyBatis 데이터베이스 매퍼
│   ├── UserMapper.java              # 사용자 데이터 매퍼
│   └── FavoriteMapper.java          # 관심물건 데이터 매퍼
├── domain/                           # 엔티티 (데이터 모델)
│   ├── User.java                    # 사용자 엔티티
│   ├── Favorite.java                # 관심물건 엔티티
│   ├── Goods.java                   # 공매물건 엔티티
│   └── GoodsResponse.java           # 온비드 API 응답 엔티티
├── dto/                              # 데이터 전송 객체 (요청/응답)
│   ├── LoginRequest.java            # 로그인 요청
│   ├── LoginResponse.java           # 로그인 응답
│   ├── RegisterRequest.java         # 회원가입 요청
│   ├── UserResponse.java            # 사용자 응답
│   ├── UpdateUserRequest.java       # 정보 수정 요청
│   └── DeleteUserRequest.java       # 회원 탈퇴 요청
└── security/                         # 보안 관련 클래스
    ├── SecurityConfig.java          # Spring Security 설정
    ├── JwtTokenProvider.java        # JWT 토큰 생성/검증
    └── JwtAuthenticationFilter.java # JWT 인증 필터
```

### 4.2 계층별 상세 설명

#### 4.2.1 Controller 계층 (컨트롤러)

**역할**: HTTP 요청을 받아 Service에 전달하고 응답을 반환

**주요 컨트롤러**:

1. **UserApiController** (`/api/users`)
   - 회원가입 (`POST /register`)
   - 로그인 (`POST /login`)
   - 내 정보 조회 (`GET /me`)
   - 내 정보 수정 (`PUT /me`)
   - 회원 탈퇴 (`DELETE /me`)

2. **GoodsApiController** (`/api/goods`)
   - 물건 목록 조회 (`GET /`)
   - 물건 목록 간단 조회 (`GET /items`)
   - XML 원본 조회 (`GET /xml`)

3. **FavoriteApiController** (`/api/favorites`)
   - 관심물건 목록 조회 (`GET /`)
   - 관심물건 등록 (`POST /`)
   - 관심물건 삭제 (`DELETE /{id}`)
   - 관심물건 여부 확인 (`GET /check/{goodsNo}`)

**예시 코드 구조**:
```java
@RestController
@RequestMapping("/api/users")
public class UserApiController {
    
    private final UserService userService;
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        // 응답 반환
    }
}
```

#### 4.2.2 Service 계층 (서비스)

**역할**: 비즈니스 로직 처리 (핵심 기능)

**주요 서비스**:

1. **UserService**
   - 회원가입 처리 (이메일 중복 체크, 비밀번호 암호화)
   - 로그인 처리 (비밀번호 확인, JWT 토큰 생성)
   - 사용자 정보 조회/수정/삭제
   - 트랜잭션 처리

2. **FavoriteService**
   - 관심물건 등록 (중복 체크)
   - 관심물건 목록 조회
   - 관심물건 삭제
   - 관심물건 여부 확인

3. **OnbidApiService**
   - 온비드 API 호출
   - XML 응답 파싱 (JAXB 사용)
   - 검색 조건 처리
   - 타임아웃 처리

**예시 로직**:
```java
@Service
public class UserService {
    
    public LoginResponse login(LoginRequest request) {
        // 1. 사용자 조회
        User user = userMapper.findByEmail(request.getEmail());
        
        // 2. 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다");
        }
        
        // 3. JWT 토큰 생성
        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail());
        
        // 4. 응답 반환
        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .build();
    }
}
```

#### 4.2.3 Mapper 계층 (데이터베이스 접근)

**역할**: 데이터베이스 SQL 실행 (MyBatis 어노테이션 방식)

**주요 매퍼**:

1. **UserMapper** - 사용자 테이블 CRUD
2. **FavoriteMapper** - 관심물건 테이블 CRUD

**예시 코드**:
```java
@Mapper
public interface UserMapper {
    
    @Select("SELECT * FROM users WHERE email = #{email}")
    User findByEmail(String email);
    
    @Insert("INSERT INTO users (email, password, username) VALUES (#{email}, #{password}, #{username})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user);
    
    @Update("UPDATE users SET username = #{username}, password = #{password} WHERE id = #{id}")
    void update(User user);
    
    @Delete("DELETE FROM users WHERE id = #{id}")
    void delete(Long id);
}
```

#### 4.2.4 Domain 계층 (엔티티)

**역할**: 데이터베이스 테이블과 매핑되는 Java 객체

**주요 엔티티**:

1. **User** - 사용자
   - id, email, password, username, createdAt, updatedAt

2. **Favorite** - 관심물건
   - id, userId, historyNo, goodsNo, goodsName, minBidPrice, bidCloseDate, createdAt

3. **Goods** - 공매물건 (온비드 API 응답)
   - historyNo, goodsNo, goodsName, minBidPrice, bidCloseDate, address, 등

#### 4.2.5 DTO 계층 (데이터 전송 객체)

**역할**: API 요청/응답 데이터 구조 정의

**특징**:
- 엔티티와 분리하여 보안 향상 (비밀번호 등 민감 정보 제외)
- API 버전 관리 용이
- 유효성 검증 (`@Valid`, `@NotNull` 등)

**예시**:
```java
@Data
public class LoginRequest {
    @NotBlank(message = "이메일을 입력해주세요")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;
    
    @NotBlank(message = "비밀번호를 입력해주세요")
    private String password;
}
```

#### 4.2.6 Security 계층 (보안)

**역할**: 인증 및 보안 처리

**주요 클래스**:

1. **SecurityConfig**
   - Spring Security 설정
   - 공개/인증 필요 URL 설정
   - CORS 설정
   - JWT 필터 등록

2. **JwtTokenProvider**
   - JWT 토큰 생성
   - JWT 토큰 검증
   - 사용자 ID 추출

3. **JwtAuthenticationFilter**
   - 모든 요청에서 JWT 토큰 확인
   - 토큰이 유효하면 인증 정보 설정

### 4.3 설정 파일

#### application.properties

주요 설정:
```properties
# 서버 포트
server.port=8081

# 데이터베이스 연결
spring.datasource.url=jdbc:mariadb://localhost:3306/onbid
spring.datasource.username=root
spring.datasource.password=1234

# JWT 설정
jwt.secret=<긴-시크릿-키>
jwt.expiration=86400000  # 24시간

# 온비드 API
onbid.api.key=${ONBID_API_KEY}
onbid.api.url=http://openapi.onbid.co.kr/...
```

---

## 5. 프론트엔드 구조

### 5.1 전체 디렉토리 구조

```
frontend/src/
├── main.jsx                    # 애플리케이션 진입점
├── App.jsx                     # 메인 라우터 컴포넌트
├── index.css                   # 전역 스타일
├── components/                 # 재사용 가능한 컴포넌트
│   ├── Header.jsx             # 상단 네비게이션 바
│   ├── PrivateRoute.jsx       # 인증 보호 라우트
│   ├── GoodsCard.jsx          # 물건 카드 (홈페이지용)
│   ├── GoodsMobileCard.jsx    # 물건 카드 (모바일용)
│   └── GoodsTable.jsx         # 물건 테이블 (데스크톱용)
├── pages/                      # 페이지 컴포넌트
│   ├── HomePage.jsx           # 메인 페이지
│   ├── ListPage.jsx           # 물건 목록 페이지
│   ├── LoginPage.jsx          # 로그인 페이지
│   ├── RegisterPage.jsx       # 회원가입 페이지
│   ├── FavoritesPage.jsx      # 관심물건 페이지
│   └── ProfilePage.jsx        # 프로필/설정 페이지
└── utils/
    └── api.js                  # API 통신 유틸리티
```

### 5.2 계층별 상세 설명

#### 5.2.1 App.jsx (메인 라우터)

**역할**: 페이지 라우팅 설정

**구조**:
```jsx
<Router>
  <Header />  {/* 모든 페이지 상단에 표시 */}
  <Routes>
    <Route path="/" element={<HomePage />} />
    <Route path="/goods" element={<ListPage />} />
    <Route path="/login" element={<LoginPage />} />
    <Route path="/register" element={<RegisterPage />} />
    
    {/* 인증 필요 페이지 */}
    <Route path="/favorites" element={
      <PrivateRoute>
        <FavoritesPage />
      </PrivateRoute>
    } />
  </Routes>
</Router>
```

#### 5.2.2 Components (컴포넌트)

**재사용 가능한 UI 조각**

1. **Header** - 상단 네비게이션
   - 로고, 메뉴, 로그인/로그아웃 버튼
   - 로그인 상태에 따라 다른 메뉴 표시

2. **PrivateRoute** - 인증 보호 라우트
   - 로그인 하지 않은 사용자는 로그인 페이지로 리다이렉트

3. **GoodsCard** - 물건 카드 (그리드 형식)
   - 홈페이지에서 사용
   - 물건 이미지, 이름, 가격, 입찰일 표시

4. **GoodsTable** - 물건 테이블 (테이블 형식)
   - 물건 목록 페이지에서 사용 (데스크톱)
   - 정렬 기능, 관심물건 등록/해제

5. **GoodsMobileCard** - 모바일용 카드
   - 물건 목록 페이지에서 사용 (모바일)
   - 터치 친화적인 UI

#### 5.2.3 Pages (페이지)

**각 URL에 해당하는 화면**

1. **HomePage** (`/`)
   - 메인 페이지
   - 최근 물건 6개 표시
   - 서비스 안내

2. **ListPage** (`/goods`)
   - 물건 목록 페이지
   - 검색 필터 (지역, 가격, 날짜 등)
   - 페이징
   - 정렬 기능
   - 반응형 (데스크톱: 테이블, 모바일: 카드)

3. **LoginPage** (`/login`)
   - 로그인 페이지
   - 이메일, 비밀번호 입력
   - JWT 토큰 저장

4. **RegisterPage** (`/register`)
   - 회원가입 페이지
   - 이메일, 비밀번호, 이름 입력

5. **FavoritesPage** (`/favorites`) - 인증 필요
   - 관심물건 목록
   - 삭제 기능

6. **ProfilePage** (`/profile`) - 인증 필요
   - 내 정보 조회
   - 정보 수정
   - 비밀번호 변경
   - 회원 탈퇴

#### 5.2.4 Utils (유틸리티)

**api.js** - API 통신 관리

**주요 기능**:
1. Axios 인스턴스 생성 (baseURL 설정)
2. 요청 인터셉터 (JWT 토큰 자동 추가)
3. 응답 인터셉터 (401 에러 시 로그아웃)
4. API 함수 정의

**예시**:
```javascript
// axios 인스턴스
const api = axios.create({
  baseURL: 'http://localhost:8081/api',
});

// 요청 시 토큰 자동 추가
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// API 함수
export const getGoodsList = async (pageNo, numOfRows, filters) => {
  const response = await api.get('/goods', { params: { pageNo, numOfRows, ...filters } });
  return response.data;
};
```

### 5.3 스타일링 (Tailwind CSS)

**Tailwind CSS** 유틸리티 클래스 사용

**예시**:
```jsx
<div className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700">
  버튼
</div>
```

**주요 클래스**:
- `bg-{color}-{shade}`: 배경색
- `text-{color}-{shade}`: 글자색
- `px-{size}`, `py-{size}`: 패딩
- `rounded`: 모서리 둥글게
- `hover:{property}`: 마우스 오버 시
- `md:{property}`: 중간 화면 이상에서만 적용

---

## 6. 데이터 흐름

### 6.1 회원가입 흐름

```
사용자
  │
  │ 1. 회원가입 양식 작성 (이메일, 비밀번호, 이름)
  ↓
RegisterPage.jsx
  │
  │ 2. registerUser() 호출
  ↓
api.js
  │
  │ 3. POST /api/users/register
  ↓
UserApiController
  │
  │ 4. userService.register() 호출
  ↓
UserService
  │
  │ 5. 이메일 중복 체크
  │ 6. 비밀번호 암호화 (BCrypt)
  │ 7. 사용자 저장
  ↓
UserMapper (MyBatis)
  │
  │ 8. INSERT INTO users...
  ↓
MariaDB
  │
  │ 9. 저장 완료
  ↓
UserService
  │
  │ 10. UserResponse 반환
  ↓
UserApiController
  │
  │ 11. JSON 응답
  ↓
RegisterPage.jsx
  │
  │ 12. 성공 메시지 표시, 로그인 페이지로 이동
  ↓
사용자
```

### 6.2 로그인 흐름

```
사용자
  │
  │ 1. 로그인 양식 작성 (이메일, 비밀번호)
  ↓
LoginPage.jsx
  │
  │ 2. loginUser() 호출
  ↓
api.js
  │
  │ 3. POST /api/users/login
  ↓
UserApiController
  │
  │ 4. userService.login() 호출
  ↓
UserService
  │
  │ 5. 이메일로 사용자 조회
  ↓
UserMapper
  │
  │ 6. SELECT * FROM users WHERE email = ?
  ↓
MariaDB
  │
  │ 7. 사용자 정보 반환
  ↓
UserService
  │
  │ 8. 비밀번호 확인 (BCrypt.matches)
  │ 9. JWT 토큰 생성 (JwtTokenProvider)
  │ 10. LoginResponse 반환 (토큰 포함)
  ↓
UserApiController
  │
  │ 11. JSON 응답 (토큰 포함)
  ↓
api.js
  │
  │ 12. 토큰을 localStorage에 저장
  ↓
LoginPage.jsx
  │
  │ 13. 홈페이지로 이동
  ↓
사용자
```

### 6.3 공매물건 조회 흐름

```
사용자
  │
  │ 1. 물건 목록 페이지 접속, 검색 조건 입력
  ↓
ListPage.jsx
  │
  │ 2. getGoodsList(pageNo, numOfRows, filters) 호출
  ↓
api.js
  │
  │ 3. GET /api/goods?pageNo=1&numOfRows=20&sido=서울
  ↓
GoodsApiController
  │
  │ 4. onbidApiService.getGoodsListParsed() 호출
  ↓
OnbidApiService
  │
  │ 5. 온비드 API URL 생성 (검색 조건 포함)
  │ 6. RestTemplate으로 API 호출
  ↓
온비드 OpenAPI (외부)
  │
  │ 7. XML 응답 반환
  ↓
OnbidApiService
  │
  │ 8. JAXB로 XML 파싱
  │ 9. GoodsResponse 객체로 변환
  │ 10. 물건 목록 추출
  ↓
GoodsApiController
  │
  │ 11. JSON 응답
  ↓
api.js
  │
  │ 12. 응답 데이터 반환
  ↓
ListPage.jsx
  │
  │ 13. 물건 목록 화면에 표시
  ↓
사용자
```

### 6.4 관심물건 등록 흐름

```
사용자 (로그인 상태)
  │
  │ 1. 물건 목록에서 "관심물건 등록" 버튼 클릭
  ↓
GoodsTable.jsx
  │
  │ 2. addFavorite(favorite) 호출
  ↓
api.js
  │
  │ 3. POST /api/favorites (JWT 토큰 포함)
  ↓
JwtAuthenticationFilter
  │
  │ 4. Authorization 헤더에서 토큰 추출
  │ 5. 토큰 검증
  │ 6. 사용자 ID 추출
  │ 7. SecurityContext에 인증 정보 설정
  ↓
FavoriteApiController
  │
  │ 8. Authentication에서 userId 추출
  │ 9. favoriteService.addFavorite() 호출
  ↓
FavoriteService
  │
  │ 10. 중복 체크 (이미 등록된 물건인지)
  │ 11. 관심물건 저장
  ↓
FavoriteMapper
  │
  │ 12. INSERT INTO favorites...
  ↓
MariaDB
  │
  │ 13. 저장 완료
  ↓
FavoriteService
  │
  │ 14. 저장된 Favorite 반환
  ↓
FavoriteApiController
  │
  │ 15. JSON 응답
  ↓
GoodsTable.jsx
  │
  │ 16. "관심물건 등록됨" 표시
  ↓
사용자
```

---

## 7. API 엔드포인트

### 7.1 사용자 API (`/api/users`)

| HTTP | 엔드포인트 | 설명 | 인증 필요 | 요청 Body | 응답 |
|------|-----------|------|----------|----------|------|
| POST | `/register` | 회원가입 | ❌ | `{ email, password, username }` | `{ success, message, user }` |
| POST | `/login` | 로그인 | ❌ | `{ email, password }` | `{ success, data: { token, userId, username, email } }` |
| GET | `/me` | 내 정보 조회 | ✅ | - | `{ success, data: { id, email, username, createdAt } }` |
| PUT | `/me` | 내 정보 수정 | ✅ | `{ username?, currentPassword?, newPassword? }` | `{ success, message, data }` |
| DELETE | `/me` | 회원 탈퇴 | ✅ | `{ password }` | `{ success, message }` |

### 7.2 공매물건 API (`/api/goods`)

| HTTP | 엔드포인트 | 설명 | 인증 필요 | 쿼리 파라미터 | 응답 |
|------|-----------|------|----------|-------------|------|
| GET | `/` | 물건 목록 조회 (전체 정보) | ❌ | `pageNo, numOfRows, sido, ctgrHirkId, ...` | `{ success, data: { totalCount, items: [...] } }` |
| GET | `/items` | 물건 목록만 간단 조회 | ❌ | 위와 동일 | `{ success, data: [...] }` |
| GET | `/xml` | XML 원본 조회 (디버깅용) | ❌ | 위와 동일 | XML 문자열 |

**검색 파라미터**:
- `pageNo`: 페이지 번호 (기본값: 1)
- `numOfRows`: 페이지당 건수 (기본값: 10)
- `ctgrHirkId`: 카테고리 ID
- `sido`: 시도 (예: "서울")
- `sgk`: 시군구 (예: "강남구")
- `emd`: 읍면동 (예: "역삼동")
- `goodsPriceFrom`: 감정가 최소
- `goodsPriceTo`: 감정가 최대
- `openPriceFrom`: 최저입찰가 최소
- `openPriceTo`: 최저입찰가 최대
- `cltrNm`: 물건명 검색
- `cltrMnmtNo`: 물건관리번호 검색
- `pbctBegnDtm`: 입찰시작일 (YYYY-MM-DD)
- `pbctClsDtm`: 입찰종료일 (YYYY-MM-DD)

### 7.3 관심물건 API (`/api/favorites`)

| HTTP | 엔드포인트 | 설명 | 인증 필요 | 요청 Body | 응답 |
|------|-----------|------|----------|----------|------|
| GET | `/` | 관심물건 목록 조회 | ✅ | - | `{ success, data: [...] }` |
| POST | `/` | 관심물건 등록 | ✅ | `{ historyNo, goodsNo, goodsName, minBidPrice, bidCloseDate }` | `{ success, message, data }` |
| DELETE | `/{id}` | 관심물건 삭제 (ID로) | ✅ | - | `{ success, message }` |
| DELETE | `/goods/{goodsNo}` | 관심물건 삭제 (물건번호로) | ✅ | - | `{ success, message }` |
| GET | `/check/{goodsNo}` | 관심물건 여부 확인 | ✅ | - | `{ success, isFavorite: true/false }` |

---

## 8. 데이터베이스

### 8.1 데이터베이스 스키마

#### 8.1.1 users 테이블 (사용자)

```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '사용자 ID',
    email VARCHAR(100) UNIQUE NOT NULL COMMENT '이메일',
    password VARCHAR(255) NOT NULL COMMENT '암호화된 비밀번호 (BCrypt)',
    username VARCHAR(50) NOT NULL COMMENT '사용자명',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

**필드 설명**:
- `id`: 사용자 고유 ID (자동 증가)
- `email`: 이메일 (로그인 ID, 중복 불가)
- `password`: BCrypt로 암호화된 비밀번호
- `username`: 사용자 이름
- `created_at`: 가입일시
- `updated_at`: 최종 수정일시

#### 8.1.2 favorites 테이블 (관심물건)

```sql
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

**필드 설명**:
- `id`: 관심물건 고유 ID (자동 증가)
- `user_id`: 사용자 ID (외래 키)
- `history_no`: 물건이력번호 (온비드의 회차별 고유 식별자)
- `goods_no`: 물건관리번호 (온비드의 물건 식별자, 예: 2025-1234-001)
- `goods_name`: 물건명
- `min_bid_price`: 최저입찰가
- `bid_close_date`: 입찰마감일시
- `created_at`: 등록일시

**제약조건**:
- `FOREIGN KEY (user_id)`: users 테이블 참조, 사용자 삭제 시 관심물건도 함께 삭제 (CASCADE)
- `UNIQUE (user_id, history_no)`: 같은 사용자가 동일한 물건을 중복 등록 불가

### 8.2 ERD (Entity Relationship Diagram)

```
┌─────────────────────┐
│       users         │
├─────────────────────┤
│ PK: id              │
│ UK: email           │
│     password        │
│     username        │
│     created_at      │
│     updated_at      │
└──────────┬──────────┘
           │
           │ 1
           │
           │ N
┌──────────┴──────────┐
│     favorites       │
├─────────────────────┤
│ PK: id              │
│ FK: user_id         │
│ UK: (user_id,       │
│      history_no)    │
│     history_no      │
│     goods_no        │
│     goods_name      │
│     min_bid_price   │
│     bid_close_date  │
│     created_at      │
└─────────────────────┘
```

**관계 설명**:
- **users : favorites** = **1 : N** (일대다)
- 한 명의 사용자는 여러 개의 관심물건을 등록할 수 있음
- 사용자 삭제 시 관심물건도 함께 삭제됨

---

## 9. 보안 및 인증

### 9.1 인증 방식: JWT (JSON Web Token)

#### 9.1.1 JWT란?

JWT는 JSON 형태의 정보를 안전하게 전송하기 위한 토큰입니다.

**구조**:
```
Header.Payload.Signature
```

**예시**:
```
eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiZW1haWwiOiJ0ZXN0QGV4YW1wbGUuY29tIiwiaWF0IjoxNzMxMjQwMDAwLCJleHAiOjE3MzEzMjY0MDB9.signature
```

**구성 요소**:
1. **Header**: 토큰 타입 및 암호화 알고리즘
   ```json
   {
     "alg": "HS512",
     "typ": "JWT"
   }
   ```

2. **Payload**: 실제 데이터 (Claims)
   ```json
   {
     "sub": "1",              // 사용자 ID
     "email": "test@example.com",
     "iat": 1731240000,       // 발급 시간
     "exp": 1731326400        // 만료 시간 (24시간 후)
   }
   ```

3. **Signature**: 위변조 방지 서명
   ```
   HMACSHA512(
     base64UrlEncode(header) + "." + base64UrlEncode(payload),
     secret
   )
   ```

#### 9.1.2 JWT 흐름

```
1. 로그인
   사용자 → 이메일/비밀번호 → 백엔드
   
2. 토큰 생성
   백엔드 → JWT 토큰 생성 → 사용자
   
3. 토큰 저장
   사용자 → localStorage에 토큰 저장
   
4. API 요청
   사용자 → Authorization: Bearer {token} → 백엔드
   
5. 토큰 검증
   백엔드 → 토큰 검증 → 사용자 ID 추출 → 요청 처리
```

#### 9.1.3 JWT 토큰 생성 (Backend)

**JwtTokenProvider.java**:
```java
public String generateToken(Long userId, String email) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + 86400000); // 24시간
    
    return Jwts.builder()
            .subject(String.valueOf(userId))
            .claim("email", email)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey, Jwts.SIG.HS512)
            .compact();
}
```

#### 9.1.4 JWT 토큰 검증 (Backend)

**JwtAuthenticationFilter.java**:
```java
protected void doFilterInternal(HttpServletRequest request, 
                                HttpServletResponse response, 
                                FilterChain filterChain) {
    // 1. 요청 헤더에서 토큰 추출
    String jwt = getJwtFromRequest(request); // "Bearer {token}"에서 토큰만 추출
    
    // 2. 토큰 검증
    if (jwt != null && jwtTokenProvider.validateToken(jwt)) {
        // 3. 토큰에서 사용자 ID 추출
        Long userId = jwtTokenProvider.getUserIdFromToken(jwt);
        
        // 4. 인증 정보 설정
        UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    
    // 5. 다음 필터 실행
    filterChain.doFilter(request, response);
}
```

#### 9.1.5 JWT 토큰 사용 (Frontend)

**api.js**:
```javascript
// 로그인 시 토큰 저장
const loginUser = async (email, password) => {
  const response = await api.post('/users/login', { email, password });
  const token = response.data.data.token;
  localStorage.setItem('token', token); // 토큰 저장
  return response.data;
};

// 모든 요청에 토큰 자동 추가 (Interceptor)
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
```

### 9.2 Spring Security 설정

#### 9.2.1 공개/인증 필요 URL 설정

**SecurityConfig.java**:
```java
http.authorizeHttpRequests(auth -> auth
    // 공개 API (인증 불필요)
    .requestMatchers("/api/users/register", "/api/users/login").permitAll()
    .requestMatchers("/api/goods/**").permitAll()
    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
    
    // 나머지 /api/** (인증 필요)
    .requestMatchers("/api/**").authenticated()
    
    // 기타 모든 요청 허용
    .anyRequest().permitAll()
);
```

#### 9.2.2 CORS 설정

**SecurityConfig.java**:
```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173")); // Frontend URL
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

### 9.3 비밀번호 암호화

**BCrypt 사용**:
- 단방향 해시 함수 (복호화 불가)
- Salt 자동 생성 (무지개 테이블 공격 방지)

**예시**:
```java
// 회원가입 시 비밀번호 암호화
String encodedPassword = passwordEncoder.encode("password123");
// 결과: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy

// 로그인 시 비밀번호 확인
boolean matches = passwordEncoder.matches("password123", encodedPassword);
// 결과: true
```

---

## 10. 주요 기능

### 10.1 회원 관리

#### 10.1.1 회원가입
- 이메일 중복 체크
- 비밀번호 BCrypt 암호화
- 유효성 검증 (`@Valid`)

#### 10.1.2 로그인
- 이메일/비밀번호 확인
- JWT 토큰 발급 (24시간 유효)
- localStorage에 토큰 저장

#### 10.1.3 내 정보 조회
- JWT 토큰으로 사용자 식별
- 비밀번호 제외하고 반환

#### 10.1.4 내 정보 수정
- 사용자명 수정
- 비밀번호 변경 (현재 비밀번호 확인 필수)

#### 10.1.5 회원 탈퇴
- 비밀번호 확인 필수
- 관심물건도 함께 삭제 (CASCADE)

### 10.2 공매물건 조회

#### 10.2.1 온비드 API 호출
- RestTemplate으로 외부 API 호출
- XML 응답 받기

#### 10.2.2 XML 파싱
- JAXB로 XML → Java 객체 변환
- GoodsResponse, Goods 엔티티 사용

#### 10.2.3 검색 및 필터링
- 지역별 검색 (시도, 시군구, 읍면동)
- 가격 범위 검색 (감정가, 최저입찰가)
- 입찰일자 검색
- 물건명 검색
- 카테고리 검색

#### 10.2.4 페이징
- 페이지 번호, 페이지당 건수 설정
- 전체 건수 표시

#### 10.2.5 정렬
- 클라이언트 사이드 정렬
- 필드별 오름차순/내림차순

### 10.3 관심물건 관리

#### 10.3.1 관심물건 등록
- 로그인 필수
- 중복 체크 (이미 등록된 물건)
- 물건 정보 저장 (물건번호, 이름, 가격, 입찰마감일)

#### 10.3.2 관심물건 목록 조회
- 로그인한 사용자의 관심물건만 조회
- 등록일 기준 최신순

#### 10.3.3 관심물건 삭제
- ID로 삭제 또는 물건번호로 삭제
- 본인 소유 확인

#### 10.3.4 관심물건 여부 확인
- 물건 목록에서 하트 아이콘으로 표시
- 이미 등록된 물건은 채워진 하트

### 10.4 반응형 UI

#### 10.4.1 데스크톱 (md 이상)
- 테이블 형식 (GoodsTable)
- 많은 정보를 한 번에 표시

#### 10.4.2 모바일 (md 미만)
- 카드 형식 (GoodsMobileCard)
- 터치 친화적
- 세로 레이아웃

#### 10.4.3 Tailwind CSS 반응형 클래스
```jsx
<div className="hidden md:block">
  {/* 데스크톱에서만 표시 */}
</div>

<div className="md:hidden">
  {/* 모바일에서만 표시 */}
</div>

<div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3">
  {/* 화면 크기에 따라 컬럼 수 변경 */}
</div>
```

### 10.5 API 문서화 (Swagger)

#### 10.5.1 Swagger UI
- URL: `http://localhost:8081/swagger-ui.html`
- 모든 API 엔드포인트 문서화
- 브라우저에서 직접 테스트 가능

#### 10.5.2 JWT 인증 테스트
1. `/api/users/login`으로 로그인
2. 응답에서 토큰 복사
3. 우측 상단 "Authorize" 버튼 클릭
4. 토큰 입력 (Bearer 제외)
5. "Authorize" 클릭
6. 인증 필요한 API 테스트 가능

---

## 11. 프로젝트 실행 방법

### 11.1 사전 준비

1. **JDK 21** 설치
2. **MariaDB 11.4** 설치 및 실행
3. **Node.js** 설치 (v18 이상)
4. **온비드 API 키** 발급 (공공데이터 포털)

### 11.2 데이터베이스 설정

```sql
-- 데이터베이스 생성
CREATE DATABASE IF NOT EXISTS onbid CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE onbid;

-- 테이블 생성
SOURCE backend/src/main/resources/sql/schema.sql;
```

### 11.3 백엔드 실행

```bash
cd backend

# 환경변수 설정 (Windows PowerShell)
$env:ONBID_API_KEY="your-api-key-here"

# 또는 application.properties에 직접 입력
# onbid.api.key=your-api-key-here

# 실행
./gradlew bootRun

# 또는 빌드 후 실행
./gradlew build
java -jar build/libs/backend-0.0.1-SNAPSHOT.jar
```

**확인**: `http://localhost:8081/swagger-ui.html`

### 11.4 프론트엔드 실행

```bash
cd frontend

# 의존성 설치
npm install

# 개발 서버 실행
npm run dev
```

**확인**: `http://localhost:5173`

---

## 12. 트러블슈팅

### 12.1 데이터베이스 연결 오류

**증상**: `Could not connect to address=(host=localhost)(port=3306)`

**해결**:
1. MariaDB 서비스 실행 중인지 확인
2. `application.properties`의 DB 정보 확인
3. 방화벽 확인

### 12.2 JWT 403 Forbidden 오류

**증상**: `/api/users/me` 호출 시 403 에러

**해결**:
1. 로그인하여 토큰 발급
2. Swagger UI: "Authorize" 버튼 클릭 후 토큰 입력
3. Frontend: localStorage에 토큰 저장 확인

### 12.3 CORS 오류

**증상**: `Access-Control-Allow-Origin` 오류

**해결**:
1. `SecurityConfig.java`에서 Frontend URL 확인
2. `setAllowedOrigins(Arrays.asList("http://localhost:5173"))`

### 12.4 온비드 API 타임아웃

**증상**: `Onbid API 연결 실패 (타임아웃)`

**해결**:
1. API 키 확인
2. 인터넷 연결 확인
3. 온비드 API 서버 상태 확인

---

## 13. 개발 시 참고 사항

### 13.1 코드 작성 규칙

1. **모든 코드에 한글 주석 작성**
2. **예외 처리 철저히**
3. **Lombok 활용** (`@Data`, `@Builder` 등)
4. **Validation 어노테이션 사용** (`@NotNull`, `@Email` 등)
5. **로그 남기기** (`log.info`, `log.error`)

### 13.2 Git 브랜치 전략

- `main`: 프로덕션 브랜치
- `dev`: 개발 브랜치
- `feature/기능명`: 기능 개발 브랜치

### 13.3 주요 라이브러리 버전

- Spring Boot: 3.5.7
- Java: 21
- React: 19
- MariaDB: 11.4

---

## 14. 확장 가능성

### 14.1 추가 가능한 기능

1. **알림 기능**: 입찰 마감 임박 시 알림
2. **물건 비교 기능**: 여러 물건 비교
3. **검색 기록**: 최근 검색어 저장
4. **지도 연동**: 물건 위치 지도에 표시
5. **통계 대시보드**: 지역별, 가격대별 통계
6. **SNS 로그인**: 카카오, 네이버 로그인

### 14.2 성능 최적화

1. **캐싱**: Redis 도입
2. **페이징 최적화**: Cursor 기반 페이징
3. **이미지 최적화**: CDN 사용
4. **API 응답 캐싱**: 자주 조회되는 데이터 캐싱

---

## 15. 라이센스 및 연락처

- **라이센스**: Apache 2.0
- **개발팀**: Side Project Team
- **GitHub**: https://github.com/kjs-alt/side-proj

---

**이 문서는 프로젝트의 전체 구조를 이해하고 설명하는 데 도움이 되도록 작성되었습니다.**
**초보자도 쉽게 이해할 수 있도록 상세한 설명과 예시를 포함하였습니다.**


