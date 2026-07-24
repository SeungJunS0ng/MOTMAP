# MOTMAP - 카카오맵 기반 맛집 저장 및 리뷰 서비스

Spring Boot 3.2와 카카오맵 API 기반의 맛집 저장, 위치 관리 및 리뷰 웹 애플리케이션입니다.

---

## 주요 기능 (Features)

### 1. 웹 디자인 & 테마 (Design System)
- **글래스모피즘 (Glassmorphism)**: Frosted glass 효과 기반의 헤더 및 모달 레이아웃
- **다크/라이트 테마**: 상단 버튼 토글, 시스템 기본 테마 자동 감지 및 `localStorage` 기반 테마 상태 유지
- **UI 마이크로 애니메이션**: 카드 호버, 로딩 스켈레톤, 모달 슬라이드, 마커 바운스 트랜지션

### 2. 위치 & 카카오맵 연동 (Location & Map UX)
- **현재 위치 마커**: GPS 현재 위치 이동 시 파란색 맥박(Pulse) 애니메이션 커스텀 오버레이 표시
- **등록 예정 위치 핀 (Draft Pin)**: 지도 클릭 시 위치 확인용 드래프트 핀 표시
- **실시간 거리 계산**: Haversine 공식을 적용하여 사용자 현재 위치와 맛집 간 거리(예: `350m`, `1.2km`) 실시간 계산 및 표시
- **주소/장소 직접 검색**: 등록 폼 내 주소 입력 및 검색 시 카카오 지오코딩을 통한 위치 자동 탐색
- **지도 영역 자동 맞춤 (`fitBounds`)**: 검색 및 카테고리 필터링 적용 시 모든 마커를 포함하도록 지도 영역 자동 조정
- **카테고리별 마커**: 한식, 중식, 일식, 양식, 카페, 기타 카테고리별 맞춤 마커 아이콘 제공

### 3. JWT 인증 & 권한 관리 (Security & Auth)
- **로그인 및 회원가입 모달**: 탭 전환 및 비밀번호 표시/숨김 토글
- **폼 유효성 검증 (Validation)**: 이메일 형식, 비밀번호 길이 및 서버 검증 오류 발생 시 필드별 에러 텍스트와 하이라이트 표시
- **소유권 기반 접근 제어**: 본인이 등록한 맛집에 대해서만 수정 및 삭제 권한 허용

### 4. 사용자 편의 기능 & 보안 (Usability & Security)
- **커스텀 토스트 알림**: 알림 메시지(성공, 경고, 오류, 정보)를 토스트 형태로 출력
- **커스텀 확인 모달**: 데이터 삭제 시 삭제 확인 대화상자 제공
- **XSS 방어**: 사용자 입력 데이터(이름, 리뷰, 주소, 닉네임)에 대한 HTML 에스케이프 처리

---

## 기술 스택 (Tech Stack)

### Backend
- **Java 17** / **Spring Boot 3.2.0**
- **Spring Security** / **JWT (JSON Web Token)**
- **Spring Data JPA** / **MySQL 8** (Hibernate ORM)
- **BCrypt Password Encoder**
- **Swagger UI (OpenAPI 3.0)** - API 자동 문서화
- **Gradle**

### Frontend
- **HTML5 / CSS3** (Vanilla CSS, Custom Properties, Flexbox/Grid, Animations)
- **JavaScript (ES6+)** - 모듈화 구조
- **Kakao Map JavaScript SDK**
- **Thymeleaf**

---

## 환경 설정 및 실행 방법

### 1. 저장소 클론
```bash
git clone https://github.com/SeungJunS0ng/MOTMAP.git
cd MOTMAP
```

### 2. MySQL 데이터베이스 생성
```sql
CREATE DATABASE motmap CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. 설정 파일 확인 (`application.yml`)
`src/main/resources/application.yml` 파일에서 데이터베이스 및 JWT 설정 정보를 확인합니다:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/motmap?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Seoul
    username: admin
    password: YOUR_PASSWORD
```

### 4. 애플리케이션 빌드 및 실행
```bash
./gradlew bootRun
```

### 5. 서비스 접속 정보
- **메인 웹 서비스**: `http://localhost:8080`
- **Swagger API 문서**: `http://localhost:8080/swagger-ui.html`
- **테스트 계정**: `admin` / `admin1234`

---

## 프로젝트 구조 (Project Structure)

```
MOTMAP/
├── src/main/java/com/motmap/
│   ├── config/              # Security, JWT, Swagger 설정
│   ├── controller/          # REST API 및 View 컨트롤러
│   ├── dto/                 # Request/Response DTO
│   ├── entity/              # User, Restaurant, Category, Role 엔티티
│   ├── exception/           # 전역 예외 처리기 및 커스텀 예외
│   ├── repository/          # JPA 리포지토리 인터페이스
│   ├── service/             # 비즈니스 로직 처리 서비스
│   └── util/                # JwtTokenProvider, 위치 계산 유틸리티
│
├── src/main/resources/
│   ├── static/
│   │   ├── css/style.css    # 메인 스타일시트 (다크모드, 글래스모피즘)
│   │   └── js/
│   │       ├── api.js       # REST API 통신 및 토큰 관리
│   │       ├── map.js       # 카카오맵 제어 및 오버레이 관리
│   │       ├── restaurant.js# UI 카드 렌더링, 필터링 및 거리 계산
│   │       └── main.js      # 테마, 모달, 토스트 및 앱 초기화
│   └── templates/
│       └── index.html       # Thymeleaf 메인 템플릿
```

---

## REST API 명세 (API Endpoints)

| 분류 | 메서드 | 엔드포인트 | 설명 |
|------|--------|------------|------|
| **인증** | `POST` | `/api/auth/login` | 사용자 로그인 및 JWT 발급 |
| | `POST` | `/api/auth/signup` | 신규 회원가입 |
| | `GET` | `/api/auth/me` | 로그인한 사용자 정보 조회 |
| **맛집** | `GET` | `/api/restaurants` | 전체 맛집 목록 조회 |
| | `POST` | `/api/restaurants` | 신규 맛집 등록 |
| | `PUT` | `/api/restaurants/{id}` | 맛집 정보 수정 (작성자 전용) |
| | `DELETE` | `/api/restaurants/{id}` | 맛집 정보 삭제 (작성자 전용) |
| | `GET` | `/api/restaurants/search` | 키워드(이름, 주소, 리뷰) 검색 |
| | `GET` | `/api/restaurants/nearby` | 내 위치 반경 맛집 검색 |
| | `GET` | `/api/restaurants/high-rated` | 고평점(4점 이상) 맛집 검색 |

---

## 라이선스

본 프로젝트는 **MIT License**에 따라 자유롭게 이용할 수 있습니다.
