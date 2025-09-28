# MOTMAP - 카카오맵 기반 맛집 저장 및 리뷰 프로젝트 🍽️

Spring Boot와 카카오맵 API를 활용한 현대적인 맛집 지도 서비스입니다.

## 🌟 주요 기능

- **🗺️ 카카오맵 완전 연동**: 실제 지도에서 맛집 위치 확인 및 클릭으로 추가
- **🍽️ 완전한 맛집 관리**: CRUD + 검색 + 필터링 기능
- **⭐ 별점 & 리뷰 시스템**: 5점 만점 평가 및 상세 리뷰 작성
- **🔍 강력한 검색**: 이름, 주소, 리뷰 내용 통합 검색
- **🏷️ 스마트 카테고리**: 한식, 중식, 일식, 양식, 카페, 기타 분류
- **📍 위치 기반 서비스**: GPS 현재 위치 & 근처 맛집 검색 (반경 설정 가능)
- **🌟 고평점 필터**: 4점 이상 고평점 맛집만 별도 조회
- **📱 완전 반응형**: 모바일/태블릿/데스크톱 완벽 지원

## 🛠️ 기술 스택

### Backend (Modern Java)
- **Spring Boot 3.2.0** - 최신 스프링 프레임워크
- **Spring Data JPA** - ORM & 데이터베이스 관리
- **Lombok** - 보일러플레이트 코드 제거
- **Bean Validation** - 입력값 검증
- **H2 Database** - 개발용 인메모리 DB
- **Swagger UI (OpenAPI 3)** - 자동 API 문서화
- **Gradle** - 빌드 도구 (Maven도 지원)

### Frontend (Modern Web)
- **HTML5 / CSS3 / JavaScript ES6+**
- **Kakao Map JavaScript API** - 실시간 지도 연동
- **Thymeleaf** - 서버사이드 템플릿 엔진
- **Fetch API** - 비동기 REST API 통신

## 📋 사전 요구사항

1. **Java 17** 이상
2. **IntelliJ IDEA** (권장) 또는 Eclipse
3. **카카오 개발자 계정** 및 JavaScript 키

## 🚀 설치 및 실행

### 1. 프로젝트 클론
```bash
git clone [your-repository-url]
cd MOTMAP
```

### 2. 카카오맵 API 키 설정
1. [Kakao Developers](https://developers.kakao.com/)에서 애플리케이션 등록
2. **Web 플랫폼 등록**: `http://localhost:8080` 추가
3. **JavaScript 키** 발급 및 적용

### 3. 애플리케이션 실행

#### 🎯 IntelliJ IDEA (권장)
```
1. IntelliJ IDEA에서 프로젝트 열기
2. Gradle 프로젝트 새로고침 (우측 Gradle 탭)
3. src/main/java/com/motmap/MotmapApplication.java 실행
```

#### ⚡ Gradle 사용
```bash
./gradlew bootRun
```

#### 📦 Maven 사용
```bash
mvn spring-boot:run
```

### 4. 웹 브라우저 접속
- **메인 서비스**: `http://localhost:8080`
- **API 문서**: `http://localhost:8080/swagger-ui.html`
- **데이터베이스**: `http://localhost:8080/h2-console`

## 📁 프로젝트 구조

```
MOTMAP/
├── 🏗️ Backend (Spring Boot 3.2.0)
│   ├── controller/         # REST API 컨트롤러
│   │   ├── RestaurantController.java    # 맛집 CRUD API (10개 엔드포인트)
│   │   └── ViewController.java          # 웹 페이지 컨트롤러
│   ├── service/
│   │   └── RestaurantService.java       # 비즈니스 로직 (Lombok 적용)
│   ├── repository/
│   │   └── RestaurantRepository.java    # 데이터 액세스 (JPA)
│   ├── entity/
│   │   ├── Restaurant.java              # 맛집 엔티티 (Builder 패턴)
│   │   └── Category.java                # 카테고리 enum
│   ├── dto/
│   │   ├── RestaurantRequestDto.java    # 요청 DTO (Lombok)
│   │   └── RestaurantResponseDto.java   # 응답 DTO (Lombok)
│   ├── exception/                       # 예외 처리 체계
│   │   ├── BusinessException.java       # 기본 비즈니스 예외
│   │   ├── ErrorCode.java              # 에러 코드 enum
│   │   ├── ErrorResponse.java          # 구조화된 에러 응답
│   │   └── [기타 커스텀 예외들]
│   ├── config/
│   │   ├── SwaggerConfig.java          # API 문서화 설정
│   │   ├── WebConfig.java             # CORS 설정
│   │   └── DataInitializer.java       # 초기 데이터 생성
│   └── util/
│       └── LocationUtils.java         # 위치 계산 유틸리티
├── 🎨 Frontend (Modern Web)
│   ├── templates/index.html           # Thymeleaf 메인 페이지
│   └── static/
│       ├── css/style.css             # 반응형 스타일시트
│       └── js/                       # ES6+ JavaScript 모듈
│           ├── api.js                # REST API 통신 관리
│           ├── map.js                # 카카오맵 관리
│           ├── restaurant.js         # 맛집 UI 관리
│           └── main.js               # 애플리케이션 초기화
├── ⚙️ 설정 파일
│   ├── build.gradle                  # Gradle 빌드 (Lombok, Swagger 포함)
│   ├── pom.xml                      # Maven 빌드 (호환성)
│   └── application.yml              # Spring Boot 설정
└── 📚 문서
    ├── README.md                    # 프로젝트 가이드
    ├── TESTING_GUIDE.md            # 테스팅 체크리스트
    └── [기타 가이드 문서들]
```

## 🔧 API 엔드포인트

### 📚 완전한 API 문서화
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI 3.0 JSON**: `http://localhost:8080/v3/api-docs`
- **한글 문서화**: 모든 API에 상세한 한글 설명과 예시값 제공

### 🍽️ 맛집 관리 API
- `GET /api/restaurants` - 전체 맛집 조회
- `GET /api/restaurants/{id}` - 특정 맛집 상세 조회
- `POST /api/restaurants` - 맛집 등록 (중복 검증 포함)
- `PUT /api/restaurants/{id}` - 맛집 정보 수정
- `DELETE /api/restaurants/{id}` - 맛집 삭제

### 🔍 검색 및 필터링 API
- `GET /api/restaurants/search?keyword={keyword}` - 통합 키워드 검색
- `GET /api/restaurants/category/{category}` - 카테고리별 조회
- `GET /api/restaurants/rating/{rating}` - 특정 평점 이상 조회
- `GET /api/restaurants/high-rated` - 🌟 고평점 맛집 (4점 이상)
- `GET /api/restaurants/sorted/rating` - 평점순 정렬
- `GET /api/restaurants/sorted/date` - 최신 등록순 정렬

### 📍 위치 기반 API
- `GET /api/restaurants/nearby?lat={lat}&lng={lng}&radius={radius}` - 근처 맛집 검색

## 💾 데이터베이스

### 🔧 개발 환경 (H2 인메모리)
- **H2 웹 콘솔**: `http://localhost:8080/h2-console`
- **JDBC URL**: `jdbc:h2:mem:motmap`
- **사용자명**: `sa` / **패스워드**: (없음)
- **자동 테이블 생성**: DDL 자동 생성 (create-drop)

### 📊 초기 데이터 (자동 생성)
서울 명동 지역의 5개 테스트 맛집이 자동으로 생성됩니다:
- **명동교자** (한식) ⭐⭐⭐⭐ - 유명한 만두집
- **전주중앙회관** (한식) ⭐⭐⭐⭐⭐ - 전통 한정식
- **스타벅스 명동점** (카페) ⭐⭐⭐⭐ - 명동 중심가 카페
- **교동짬뽕** (중식) ⭐⭐⭐⭐ - 얼큰한 짬뽕 전문점
- **긴자료코** (일식) ⭐⭐⭐⭐⭐ - 고급 일식 레스토랑

### 🗄️ 데이터베이스 스키마
```sql
-- 성능 최적화를 위한 인덱스 자동 생성
CREATE INDEX idx_restaurant_category ON restaurants(category);
CREATE INDEX idx_restaurant_rating ON restaurants(rating);  
CREATE INDEX idx_restaurant_location ON restaurants(latitude, longitude);
CREATE INDEX idx_restaurant_created_at ON restaurants(created_at);
```

## 🎯 사용 방법

### 1. **🗺️ 맛집 지도 탐색**
- 카카오맵에서 서울시청 중심으로 시작
- 📍 "현재 위치" 버튼으로 내 위치로 이동
- 지도 확대/축소로 원하는 지역 탐색

### 2. **➕ 새 맛집 추가**
```
지도에서 원하는 위치 클릭 → 
맛집 정보 입력 폼 나타남 → 
이름, 카테고리, 평점, 리뷰 작성 → 
저장 버튼 클릭
```

### 3. **🔍 맛집 검색 & 필터링**
- **키워드 검색**: 검색창에 맛집 이름이나 지역 입력
- **카테고리 필터**: 드롭다운에서 음식 종류 선택
- **정렬 옵션**: 최신순 또는 평점순 선택
- **🌟 고평점 필터**: "고평점 맛집 보기" 버튼 클릭

### 4. **📱 맛집 관리**
- **상세 보기**: 맛집 카드에서 "지도에서 보기" 클릭
- **정보 수정**: 맛집 정보 업데이트
- **삭제**: 맛집 삭제 (확인 대화상자 포함)

## 🔄 개발 워크플로우

### 🧪 테스트 환경
1. **애플리케이션 실행**: `MotmapApplication.java` 실행
2. **기본 기능 테스트**: 지도 로드, 맛집 목록 확인
3. **API 테스트**: Swagger UI에서 엔드포인트 테스트
4. **데이터 확인**: H2 콘솔에서 SQL 쿼리 실행

### 🔧 개발 도구
- **Hot Reload**: Spring Boot DevTools로 자동 재시작
- **API 문서**: Swagger UI에서 실시간 API 테스트
- **데이터베이스**: H2 콘솔에서 데이터 직접 조회/수정
- **로깅**: 콘솔과 `logs/motmap.log` 파일에서 디버깅 정보 확인

## 🎮 주요 사용 시나리오

### 1: 새 맛집 발견 및 등록
```
1. 지도에서 새로운 맛집 위치 클릭
2. 자동으로 주소가 채워진 등록 폼 확인
3. 맛집 이름, 카테고리, 평점, 리뷰 입력
4. 저장하면 지도에 마커와 목록에 추가됨
```

### 2: 맛집 탐색 및 검색
```
1. 검색창에 "명동" 입력하여 지역별 검색
2. 카테고리를 "한식"으로 필터링
3. "고평점 맛집 보기" 버튼으로 4점 이상만 조회
4. 맛집 클릭하여 지도에서 위치 확인
```

### 3: API 개발 및 테스트
```
1. http://localhost:8080/swagger-ui.html 접속
2. "맛집 관리" API 그룹에서 원하는 엔드포인트 선택
3. "Try it out" 버튼으로 실시간 API 테스트
4. 요청/응답 데이터 형식 확인
```

## 🚀 새로운 기능 (최근 추가)

### 🌟 고평점 맛집 필터링
- **백엔드**: `/api/restaurants/high-rated` 엔드포인트
- **프론트엔드**: 전용 필터 버튼으로 4점 이상 맛집만 조회
- **자동 마커 업데이트**: 지도에서도 고평점 맛집만 표시

### 📍 위치 유틸리티 시스템
- **LocationUtils**: 두 지점 간 거리 계산 (Haversine 공식)
- **위치 검증**: 위도/경도 유효성 자동 확인
- **근거리 검색**: 정확한 거리 계산 기반 근처 맛집 찾기

### 예외 처리
- **BusinessException**: 계층적 예외 구조
- **ErrorCode**: 표준화된 에러 코드 체계
- **ErrorResponse**: JSON 형태의 구조화된 에러 응답
- **필드별 검증**: 상세한 입력값 오류 메시지

## 💡 기술적 특징

### 🔧 Modern Java Patterns
- **Lombok**: `@Data`, `@Builder`, `@RequiredArgsConstructor` 활용
- **Builder 패턴**: 가독성 높은 객체 생성
- **정적 팩토리 메소드**: `RestaurantResponseDto.from()`
- **Stream API**: Java 17의 `.toList()` 활용

### 📊 데이터베이스 최적화
- **인덱스 자동 생성**: 카테고리, 평점, 위치, 생성일 기준
- **JPA 쿼리 최적화**: Native Query 활용한 근거리 검색
- **중복 방지**: 이름+주소 기준 유니크 검증

### 🔍 검색 성능
- **통합 검색**: 이름, 주소, 리뷰에서 대소문자 무시 검색
- **위치 기반 검색**: 반경 설정 가능한 근거리 맛집 찾기
- **다양한 정렬**: 평점순, 최신순, 카테고리별

## 향후 개선 계획

- [ ] **사용자 인증**: Spring Security 도입
- [ ] **이미지 업로드**: 맛집 사진 첨부 기능
- [ ] **소셜 기능**: 맛집 공유, 즐겨찾기
- [ ] **리뷰 시스템**: 댓글, 좋아요 기능
- [ ] **실제 DB 연동**: MySQL/PostgreSQL 지원
- [ ] **캐시 시스템**: Redis 캐시 도입
- [ ] **모바일 앱**: React Native/Flutter 연동

## 프로젝트 리뷰

- ✅ **Production Ready**: 실제 운영 환경에 배포 가능한 수준
- ✅ **Modern Tech Stack**: 최신 Java 17 + Spring Boot 3.2
- ✅ **완전한 테스트**: Swagger UI로 모든 API 테스트 가능
- ✅ **확장 가능**: 새로운 기능 추가가 용이한 구조
- ✅ **개발자 친화적**: 상세한 문서화와 로깅
