# OnBid Backend API

온비드(OnBid) 공매 물건 조회 시스템의 백엔드 API 서버입니다.

## 기술 스택

- **Language**: Java 21
- **Framework**: Spring Boot 3.5.7
- **Build Tool**: Gradle 8.11.1
- **Database**: MariaDB
- **ORM**: MyBatis
- **Template Engine**: Thymeleaf

## 프로젝트 구조

```
src/main/java/com/onbid/
├── controller/          # API 컨트롤러 (예정)
├── service/            # 비즈니스 로직 (예정)
├── mapper/             # MyBatis 매퍼 (예정)
├── domain/             # 엔티티/DTO (예정)
├── config/             # 설정 클래스
│   └── WebConfig.java
├── test/               # 테스트 클래스
│   └── ApiExplorer.java
└── BackendApplication.java
```

## 환경 설정

### 1. 환경변수 설정

OnBid API 키를 환경변수로 설정해야 합니다:

**PowerShell:**
```powershell
$env:ONBID_API_KEY="your_api_key_here"
```

**IntelliJ IDEA:**
1. Run → Edit Configurations
2. Environment variables 항목에 추가:
   ```
   ONBID_API_KEY=your_api_key_here
   ```

### 2. 데이터베이스 설정

MariaDB 데이터베이스를 생성하고 `application.properties`의 설정을 확인하세요:

```sql
CREATE DATABASE onbid CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## 실행 방법

### ApiExplorer 테스트 실행

OnBid API 연결 테스트를 위한 독립 실행 프로그램:

1. 환경변수 설정 (위 참조)
2. IntelliJ에서 `ApiExplorer.java` 파일 열기
3. `main` 메서드 옆의 실행 버튼(▶️) 클릭

또는 명령줄에서:

```powershell
# 컴파일
.\gradlew build

# 실행
java -cp build\classes\java\main com.onbid.test.ApiExplorer
```

### Spring Boot 애플리케이션 실행

```powershell
.\gradlew bootRun
```

서버는 `http://localhost:8081`에서 실행됩니다.

## API 테스트 결과

ApiExplorer 실행 시 다음 정보를 확인할 수 있습니다:

- ✅ API 키 로드 성공 여부
- ✅ HTTP 요청 정보 (엔드포인트, 파라미터)
- ✅ HTTP 응답 코드
- ✅ 응답 데이터 (XML 형식)

## 다음 단계

- [ ] Controller 클래스 작성 (GoodsController, UserController, FavoriteController)
- [ ] Service 클래스 작성 (OnbidApiService, UserService, FavoriteService)
- [ ] Mapper 클래스 작성 (UserMapper, FavoriteMapper)
- [ ] Domain 클래스 작성 (User, Favorite, Goods, Notice)
- [ ] 데이터베이스 테이블 생성 SQL 작성

## 문의

프로젝트 관련 문의사항이 있으시면 이슈를 등록해주세요.

