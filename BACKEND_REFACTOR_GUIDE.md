# 백엔드 구조 정비 가이드

온비드 백엔드의 주요 파일과 폴더 구조를 간결하게 유지하면서도 설명하기 쉬운 형태로 재정비하기 위한 가이드입니다. 아래 순서를 따라가면 XML 파싱, 예외 처리, 응답 포맷 등을 명확히 나눌 수 있습니다.

---

## 1. 목표 요약

- **외부 XML 응답과 내부 도메인 코드 분리**: `openapi/xml` 패키지로 Raw DTO를 집중 배치
- **예외 처리 단일화**: `exception` 패키지 + `@RestControllerAdvice`로 실패 응답 통합
- **응답 포맷 통일**: 성공/실패를 동일 구조(`success`, `data`, `errorCode`, `message`)로 표현
- **DTO 검증 일원화**: `@Valid` + Bean Validation으로 서비스 내부 검증 로직을 최소화

---

## 2. 새 폴더 구조

```
backend/src/main/java/com/onbid
├── BackendApplication.java
├── config/                                    # 기존 유지
├── controller/
│   ├── GoodsApiController.java
│   └── PurchaseApiController.java
├── domain/
│   ├── goods/
│   │   ├── dto/                               # 내부 도메인 DTO
│   │   └── entity/
│   └── purchase/...
├── exception/
│   ├── BusinessException.java
│   ├── GoodsNotFoundException.java (예시)
│   └── GlobalExceptionHandler.java
├── mapper/
│   ├── GoodsMapper.java
│   └── PurchaseMapper.java
├── openapi/
│   └── xml/
│       ├── OnbidResponseRaw.java
│       ├── OnbidBodyRaw.java
│       ├── OnbidItemsRaw.java
│       └── OnbidItemRaw.java
└── service/
    ├── OnbidApiService.java
    ├── GoodsService.java
    └── ...
```

> `exception` 패키지 한 군데에서 실패 응답을 일괄 처리하므로, 추가적인 `common` 계층 없이도 구조를 단순하게 유지할 수 있습니다.

---

## 3. 빌드 설정 (`backend/build.gradle`)

```groovy
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.5'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'com.fasterxml.jackson.dataformat:jackson-dataformat-xml' // XML 파싱
    ...
}
```

- XML 파싱을 Jackson으로 처리하면 JAXB 의존성을 최소화하고 DTO 어노테이션을 단순하게 유지할 수 있습니다.

---

## 4. XML Raw DTO 계층 (`com.onbid.openapi.xml`)

1. **OnbidResponseRaw** – `<response>` 전체
2. **OnbidBodyRaw** – `<body>`
3. **OnbidItemsRaw** – `<items>`
4. **OnbidItemRaw** – `<item>`

```java
@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
public class OnbidResponseRaw {
    @XmlElement(name = "header")
    private OnbidHeaderRaw header;
    @XmlElement(name = "body")
    private OnbidBodyRaw body;
    // getter/setter + 한글 주석
}
```

> Raw DTO에는 XML 어노테이션만 유지합니다. 내부 도메인(`com.onbid.domain.goods.dto.Goods`)는 순수 Java 객체로 남겨두면 설명이 훨씬 쉬워집니다.

---

## 5. 도메인 변환 매퍼 (`com.onbid.domain.goods.mapper`)

```java
public class OnbidGoodsMapper {
    public static Goods toDomain(OnbidItemRaw raw) {
        return Goods.builder()
                .historyNo(raw.getHistoryNo())
                .goodsNo(raw.getGoodsNo())
                .goodsName(raw.getGoodsName())
                .build();
    }
    public static List<Goods> toDomainList(List<OnbidItemRaw> raws) {
        return raws == null ? List.of()
                : raws.stream().map(OnbidGoodsMapper::toDomain).toList();
    }
}
```

- `OnbidApiService`는 `XmlMapper`로 Raw DTO를 만들고, Mapper를 통해 도메인 DTO로 변환합니다.
- 컨트롤러/서비스에서는 Raw DTO를 몰라도 되므로 책임이 명확합니다.

---

## 6. 예외 처리 계층 (`com.onbid.exception`)

1. **BusinessException**: `errorCode`와 메시지를 포함한 기본 런타임 예외
2. **도메인별 예외**: 예 `GoodsNotFoundException`, `InvalidSyncStateException`
3. **GlobalExceptionHandler** (`@RestControllerAdvice`)

```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusiness(BusinessException e) {
        return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "errorCode", e.getErrorCode(),
                "message", e.getMessage()
        ));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        log.error("서버 오류", e);
        return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "errorCode", "INTERNAL_ERROR",
                "message", "서버 오류가 발생했습니다."
        ));
    }
}
```

> 이렇게 하면 컨트롤러에서 `try/catch`와 에러 메시지를 제거할 수 있어 코드와 설명이 간결해집니다.

---

## 7. 컨트롤러 간소화 (`GoodsApiController`, `PurchaseApiController`)

- **성공 응답**: `ResponseEntity.ok(Map.of("success", true, "data", ...))`
- **실패 처리**: 서비스/컨트롤러에서 예외만 던지고, 전역 예외 처리기가 응답 생성
- **검증**: `@Valid @ParameterObject GoodsSearchRequest request`

```java
@GetMapping
public ResponseEntity<Map<String, Object>> getGoodsList(@Valid @ParameterObject GoodsSearchRequest request) {
    GoodsResponse goodsResponse = onbidApiService.getGoodsListParsed(...);
    return ResponseEntity.ok(Map.of(
            "success", true,
            "data", goodsResponse.getBody(),
            "header", goodsResponse.getHeader()
    ));
}
```

> 실패 시 `throw new GoodsFetchException(e)`처럼 예외만 던지면 됩니다.

---

## 8. 서비스 계층 정리 (`GoodsService`, `PurchaseService`)

- **XML ↔ 도메인 변환**: 반드시 Mapper를 거치도록 하여 서비스가 Raw DTO를 몰라도 되게 구성
- **DB 로직**: `GoodsService.saveGoodsListToDB()`는 `GoodsMapper` 호출 외에 유효성 검사만 수행
- **검증 실패 처리**: `throw new BusinessException("INVALID_GOODS", "...")`

```java
public GoodsEntity getGoodsByHistoryNo(Long historyNo) {
    GoodsEntity entity = goodsMapper.findByHistoryNo(historyNo);
    if (entity == null) {
        throw new GoodsNotFoundException(historyNo);
    }
    return entity;
}
```

---

## 9. DTO 검증 (`GoodsItemsRequest`, `GoodsSearchRequest`, `PurchaseRequest`)

- 이미 사용 중인 `@Min`, `@NotNull` 어노테이션을 유지하고, 메시지를 한글로 작성
- 컨트롤러 메서드 파라미터에 `@Valid`를 붙여 전역 예외 처리기에서 오류 응답을 통일

```java
@Schema(description = "페이지 번호", defaultValue = "1")
@Min(value = 1, message = "페이지 번호는 1 이상이어야 합니다.")
private int pageNo = 1;
```

---

## 10. 적용 순서 체크리스트

1. `jackson-dataformat-xml` 의존성 추가
2. `openapi/xml` 패키지 생성 및 Raw DTO 이동
3. `OnbidGoodsMapper` 추가 후 `OnbidApiService`에 적용
4. `exception` 패키지 + 전역 예외 처리기 구성
5. 컨트롤러에서 `try/catch` 제거, 예외 던지기 방식으로 변경
6. 서비스/DTO에 Bean Validation 보강
7. README 또는 Swagger 문서에 새로운 응답 포맷과 오류 코드 문서화

이 순서를 따라가면 코드 구조가 자연스럽게 정리되고, 설명이 한층 쉬워집니다. 필요 시 각 단계별로 Git 커밋을 분리해 추적하면 더 좋습니다.

---

## 11. 참고 사항

- 프런트엔드는 `success/errorCode/message` 구조만 알면 되므로 다국어, 토스트 메시지 등을 자유롭게 커스터마이즈할 수 있습니다.
- 스케줄러(`GoodsSyncScheduler`)는 기존과 동일하게 동작하되, 실패 시 예외를 던져 전역 처리기로 전달하면 됩니다.
- `schema.sql`, `README.md`, `SwaggerConfig` 등 문서/설정 파일도 필요에 따라 최신 구조에 맞춰 설명을 업데이트하세요.

필요한 변경 항목이 늘어나거나 세부 구현 예시가 필요하면, 이 문서를 기준으로 세부 가이드를 추가해 나가면 됩니다.

