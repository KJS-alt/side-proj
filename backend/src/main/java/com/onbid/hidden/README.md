# Hidden 폴더 안내

이 폴더는 일시적으로 숨김 처리된 기능들의 소스코드를 보관하는 공간입니다.

## 📦 보관된 기능

### 1. 인증/인가 기능
- **controller**: UserApiController.java
- **domain**: User.java
- **dto**: 
  - DeleteUserRequest.java
  - LoginRequest.java
  - LoginResponse.java
  - RegisterRequest.java
  - UpdateUserRequest.java
  - UserResponse.java
- **mapper**: UserMapper.java
- **service**: UserService.java
- **security**: 
  - JwtAuthenticationFilter.java
  - JwtTokenProvider.java
  - SecurityConfig.java

### 2. 관심물건 기능
- **controller**: FavoriteApiController.java
- **domain**: Favorite.java
- **mapper**: FavoriteMapper.java
- **service**: FavoriteService.java

## ⚠️ 주의사항

1. 이 폴더의 파일들은 현재 애플리케이션에서 사용되지 않습니다.
2. 기능을 재활성화하려면 해당 파일들을 원래 위치로 이동시켜야 합니다.
3. 프론트엔드의 기능 토글도 함께 활성화해야 합니다.

## 🔄 복원 방법

### 백엔드 복원
각 파일을 원래 위치로 이동:
- `hidden/controller/*.java` → `controller/`
- `hidden/domain/*.java` → `domain/`
- `hidden/dto/*.java` → `dto/`
- `hidden/mapper/*.java` → `mapper/`
- `hidden/service/*.java` → `service/`
- `hidden/security/*.java` → `security/`

### 프론트엔드 복원
다음 파일들의 `FEATURES` 상수를 수정:
- `frontend/src/components/Header.jsx`
  - `AUTH: true` (로그인/회원가입 기능 활성화)
  - `FAVORITES: true` (관심물건 기능 활성화)
- `frontend/src/components/GoodsCard.jsx`
  - `FAVORITES: true`
- `frontend/src/components/GoodsTable.jsx`
  - `FAVORITES: true`

## 📅 숨김 처리 날짜
2025-11-12

## 📝 비고
나중에 다시 구현할 예정

