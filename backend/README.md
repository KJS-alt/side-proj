# 온비드 공매물건 조회 시스템 - REST API Backend

한국자산관리공사 온비드 OpenAPI를 활용한 공매물건 조회 REST API 백엔드

## 📋 프로젝트 개요

- **프로젝트명**: side-proj (온비드 공매물건 조회 시스템)
- **아키텍처**: REST API 백엔드 (프론트엔드는 별도 React 프로젝트로 구성 예정)
- **목적**: 온비드 공매물건 조회 및 관심물건 관리 REST API 제공
- **개발 기간**: 약 1주 (7일)
- **GitHub**: https://github.com/kjs-alt/side-proj

## 🛠️ 기술 스택

### Backend (REST API)
- **프레임워크**: Spring Boot 3.5.7
- **언어**: Java 21
- **빌드 도구**: Gradle
- **ORM**: MyBatis (어노테이션 방식)
- **API 통신**: RestTemplate
- **인증**: JWT (JSON Web Token)
- **보안**: Spring Security
- **API 문서화**: SpringDoc OpenAPI (Swagger)

### Frontend (예정)
- **프레임워크**: React + Vite
- **언어**: JavaScript
- **스타일링**: Tailwind CSS
- **별도 리포지토리로 관리 예정**

### Database
- **DBMS**: MariaDB 11.4
- **DB 매핑**: MyBatis 어노테이션 (@Select, @Insert, @Update, @Delete)

## 🚀 시작하기

### 1. 사전 요구사항

- JDK 21 이상
- MariaDB 11.4 이상
- Gradle 8.x
- 온비드 API 키 (공공데이터 포털에서 발급)

### 2. 데이터베이스 설정

```sql
-- MariaDB에 데이터베이스 생성
CREATE DATABASE IF NOT EXISTS onbid CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE onbid;

-- 테이블 생성
SOURCE src/main/resources/sql/schema.sql;
```

또는 `src/main/resources/sql/schema.sql` 파일을 직접 실행하세요.

### 3. 환경 변수 설정

#### Windows (PowerShell)
```powershell
$env:ONBID_API_KEY="your-api-key-here"
```

#### macOS/Linux
```bash
export ONBID_API_KEY="your-api-key-here"
```

또는 `src/main/resources/application.properties`에서 직접 설정:
```properties
onbid.api.key=your-api-key-here
```

### 4. 애플리케이션 실행

```bash
# Gradle로 실행
./gradlew bootRun

# 또는 JAR 파일 빌드 후 실행
./gradlew build
java -jar build/libs/backend-0.0.1-SNAPSHOT.jar
```

애플리케이션이 `http://localhost:8081`에서 실행됩니다.

## 📚 API 문서 및 테스트

### Swagger UI (추천)
애플리케이션 실행 후 다음 URL에서 **대화형 API 문서**를 확인하고 직접 테스트할 수 있습니다:

- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **API Docs (JSON)**: http://localhost:8081/v3/api-docs

> 💡 **Tip**: Swagger UI를 통해 모든 API를 브라우저에서 바로 테스트할 수 있습니다!
> 
> 1. 회원가입/로그인으로 JWT 토큰 발급
> 2. 우측 상단 "Authorize" 버튼 클릭
> 3. 발급받은 토큰 입력
> 4. 인증이 필요한 API 테스트 가능

### 주요 API 엔드포인트

#### 1. 사용자 API (`/api/users`)

| 메서드 | 엔드포인트 | 설명 | 인증 |
|--------|-----------|------|------|
| POST | `/api/users/register` | 회원가입 | ❌ |
| POST | `/api/users/login` | 로그인 | ❌ |
| GET | `/api/users/me` | 내 정보 조회 | ✅ |

#### 2. 관심물건 API (`/api/favorites`)

| 메서드 | 엔드포인트 | 설명 | 인증 |
|--------|-----------|------|------|
| GET | `/api/favorites` | 관심물건 목록 | ✅ |
| POST | `/api/favorites` | 관심물건 등록 | ✅ |
| DELETE | `/api/favorites/{id}` | 관심물건 삭제 | ✅ |
| GET | `/api/favorites/check/{goodsNo}` | 관심물건 여부 확인 | ✅ |

#### 3. 공매물건 API (`/api/goods`)

| 메서드 | 엔드포인트 | 설명 | 인증 |
|--------|-----------|------|------|
| GET | `/api/goods` | 물건 목록 조회 | ❌ |
| GET | `/api/goods/items` | 물건 목록만 간단 조회 | ❌ |
| GET | `/api/goods/xml` | XML 원본 조회 (디버깅) | ❌ |

## 🔐 인증 방식

### JWT 토큰 인증

1. **로그인**으로 JWT 토큰 발급
2. 이후 요청 시 **Authorization 헤더**에 토큰 포함

```
Authorization: Bearer {your-jwt-token}
```

### 예시: 로그인 및 API 호출

```bash
# 1. 회원가입
curl -X POST http://localhost:8081/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123",
    "username": "테스트사용자"
  }'

# 2. 로그인
curl -X POST http://localhost:8081/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'

# 3. 내 정보 조회 (토큰 필요)
curl -X GET http://localhost:8081/api/users/me \
  -H "Authorization: Bearer {받은_토큰}"
```

## 📁 프로젝트 구조

```
src/main/java/com/onbid/
├── BackendApplication.java          # 메인 애플리케이션
├── config/
│   ├── SwaggerConfig.java           # Swagger 설정
│   └── WebConfig.java               # CORS 설정
├── controller/
│   └── api/                         # REST API 컨트롤러
│       ├── UserApiController.java
│       ├── FavoriteApiController.java
│       └── GoodsApiController.java
├── domain/                          # 엔티티 및 DTO
│   ├── User.java
│   ├── Favorite.java
│   ├── Goods.java
│   └── GoodsResponse.java
├── dto/                             # 요청/응답 DTO
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   ├── RegisterRequest.java
│   └── UserResponse.java
├── mapper/                          # MyBatis Mapper
│   ├── UserMapper.java
│   └── FavoriteMapper.java
├── security/                        # 보안 설정 (JWT)
│   ├── JwtTokenProvider.java
│   ├── JwtAuthenticationFilter.java
│   └── SecurityConfig.java
└── service/                         # 서비스 계층
    ├── UserService.java
    ├── FavoriteService.java
    └── OnbidApiService.java

src/main/resources/
├── application.properties           # 애플리케이션 설정
└── sql/
    └── schema.sql                   # 데이터베이스 스키마
```

## 🗄️ 데이터베이스 스키마

### users (사용자)
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    username VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### favorites (관심물건)
```sql
CREATE TABLE favorites (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    goods_no VARCHAR(50) NOT NULL,
    goods_name VARCHAR(500),
    min_bid_price BIGINT,
    bid_close_date VARCHAR(14),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_goods (user_id, goods_no)
);
```

## ⚙️ 설정 파일

### application.properties 주요 설정

```properties
# 서버 포트
server.port=8081

# MariaDB 연결
spring.datasource.url=jdbc:mariadb://localhost:3306/onbid
spring.datasource.username=root
spring.datasource.password=1234

# JWT 설정
jwt.secret=your-secret-key
jwt.expiration=86400000

# Swagger 설정
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
```

## 🔧 개발 도구

- **IDE**: IntelliJ IDEA, Cursor
- **API 테스트**: Postman, Swagger UI
- **버전 관리**: Git

## 📝 참고 자료

- [온비드 OpenAPI 문서](https://www.data.go.kr/data/15000851/openapi.do)
- [Spring Boot 공식 문서](https://spring.io/projects/spring-boot)
- [MyBatis 공식 문서](https://mybatis.org/mybatis-3/)
- [SpringDoc OpenAPI 문서](https://springdoc.org/)

## 🐛 트러블슈팅

### 1. 데이터베이스 연결 오류
- MariaDB 서비스가 실행 중인지 확인
- `application.properties`의 DB 연결 정보 확인

### 2. API 키 관련 오류
- 환경변수 `ONBID_API_KEY`가 설정되었는지 확인
- API 키 발급 상태 및 할당량 확인

### 3. JWT 토큰 오류
- 토큰 만료 시간 확인 (기본 24시간)
- Authorization 헤더 형식 확인: `Bearer {token}`

## 📄 라이센스

Apache 2.0

## 👥 개발자

Side Project Team

---

**주의**: 이 프로젝트는 학습 및 포트폴리오 목적으로 개발되었습니다.
