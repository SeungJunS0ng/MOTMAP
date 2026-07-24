# MOTMAP — 나만의 맛집 지도 서비스 🍽️

> **Spring Boot 3.2**와 **카카오맵 API** 기반의 프리미엄 맛집 저장 및 리뷰 웹 애플리케이션입니다.

---

## 🌟 주요 기능 (Features)

### 🎨 1. 프리미엄 디자인 & 다크 모드 (Modern Design System)
- **글래스모피즘 (Glassmorphism)**: Frosted glass 효과가 적용된 헤더 및 모달 UI
- **다크 / 라이트 테마**: 토글 버튼 + 시스템 설정 자동 감지 + `localStorage` 테마 유지
- **마이크로 애니메이션**: 카드 호버, 스켈레톤 로딩, 모달 슬라이드, 마커 바운스 효과

### 🗺️ 2. 위치 & 카카오맵 고도화 (Location & Map UX)
- **📍 내 위치 맥박(Pulse) 마커**: 현재 위치 버튼 클릭 시 GPS 위치에 파란색 맥박 애니메이션 마커 렌더링
- **📌 신규 등록 드래프트 핀**: 지도 클릭 시 통통 튀는 등록 예정 위치 핀 표시
- **📏 실시간 거리 계산 배지**: Haversine 공식을 적용하여 현재 위치 기준 맛집 거리(`📍 350m`, `📍 1.2km`) 카드에 실시간 표시
- **🔍 주소/장소 직접 검색**: 등록 폼에서 건물명/도로명 주소 입력 후 검색 시 지오코딩으로 위치 자동 이동 및 핀 생성
- **📐 지도 영역 자동 맞춤 (`fitBounds`)**: 검색 및 카테고리 필터링 시 모든 마커가 화면에 보이도록 축척/중심 자동 조절
- **🍚 카테고리 이모지 커스텀 마커**: 한식(🍚), 중식(🥟), 일식(🍣), 양식(🍝), 카페(☕), 기타(🍴) 눈물 모양 커스텀 마커

### 🔐 3. JWT 인증 & 회원 관리 (Authentication)
- **로그인 & 회원가입 탭 모달**: 패스워드 비밀 보기(👁️/🙈) 토글 기능
- **실시간/서버 필드별 에러 처리**: 이메일 양식, 아이디/비밀번호 길이에 따른 하단 에러 텍스트 & 빨간 테두리 하이라이트
- **소유권 관리**: 본인이 등록한 맛집만 수정/삭제 권한 부여

### 🔔 4. 사용자 편의성 (Usability & Safety)
- **토스트 알림 시스템**: `alert()` 대신 4가지(성공/경고/에러/정보) 커스텀 토스트 알림
- **커스텀 확인 모달**: `confirm()` 대신 커스텀 모달팝업 연동
- **XSS 보안 방어**: 사용자 입력값(`name`, `review`, `address`, `nickname`) 자동 HTML 에스케이프 처리

---

## 🛠️ 기술 스택 (Tech Stack)

### Backend
- **Java 17** / **Spring Boot 3.2.0**
- **Spring Security** + **JWT** (JSON Web Token)
- **Spring Data JPA** / **MySQL 8** (Hibernate ORM)
- **BCrypt Password Encoder**
- **Swagger UI (OpenAPI 3.0)** — API 문서화
- **Gradle**

### Frontend
- **HTML5 / CSS3** (Vanilla CSS Variables, Flexbox/Grid, Glassmorphism, Animations)
- **JavaScript (ES6+)** — Modular Structure
- **Kakao Map JavaScript SDK**
- **Thymeleaf**

---

## 🚀 설치 및 실행 방법

### 1. Repository Clone
```bash
git clone https://github.com/SeungJunS0ng/MOTMAP.git
cd MOTMAP
```

### 2. MySQL 데이터베이스 생성
```sql
CREATE DATABASE motmap CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. application.yml 설정 확인
`src/main/resources/application.yml` 파일에서 MySQL 접속 정보 및 JWT Secret Key 확인:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/motmap?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Seoul
    username: admin
    password: YOUR_PASSWORD
```

### 4. 애플리케이션 실행
```bash
./gradlew bootRun
```

### 5. 서비스 접속
- **메인 웹 페이지**: `http://localhost:8080`
- **Swagger API 문서**: `http://localhost:8080/swagger-ui.html`
- **테스트 계정**: `admin` / `admin1234`

---

## 📁 프로젝트 구조

```
MOTMAP/
├── 🏗️ src/main/java/com/motmap/
│   ├── config/              # Security, JWT, Swagger 설정
│   ├── controller/          # AuthController, RestaurantController, ViewController
│   ├── dto/                 # Request/Response DTO
│   ├── entity/              # User, Restaurant, Category, Role
│   ├── exception/           # GlobalExceptionHandler, BusinessException
│   ├── repository/          # UserRepository, RestaurantRepository
│   ├── service/             # AuthService, RestaurantService, CustomUserDetailsService
│   └── util/                # JwtTokenProvider, LocationUtils
│
├── 🎨 src/main/resources/
│   ├── static/
│   │   ├── css/style.css    # 프리미엄 디자인 시스템 & 다크모드
│   │   └── js/
│   │       ├── api.js       # JWT REST API 통신 모듈
│   │       ├── map.js       # 카카오맵, 마커, 오버레이, 위치 모듈
│   │       ├── restaurant.js# 카드 UI, 상세 모달, 거리 계산, 필터 모듈
│   │       └── main.js      # 테마, 토스트, 확인 모달, 인증 컨트롤러
│   └── templates/
│       └── index.html       # 메인 Thymeleaf 레이아웃
```

---

## 🔧 REST API 주요 엔드포인트

| 분류 | 메서드 | 엔드포인트 | 설명 |
|------|--------|------------|------|
| **인증** | `POST` | `/api/auth/login` | 로그인 및 JWT 토큰 발급 |
| | `POST` | `/api/auth/signup` | 회원가입 |
| | `GET` | `/api/auth/me` | 현재 사용자 정보 조회 |
| **맛집** | `GET` | `/api/restaurants` | 전체 맛집 목록 조회 |
| | `POST` | `/api/restaurants` | 새 맛집 등록 |
| | `PUT` | `/api/restaurants/{id}` | 맛집 수정 (본인 소유만) |
| | `DELETE` | `/api/restaurants/{id}` | 맛집 삭제 (본인 소유만) |
| | `GET` | `/api/restaurants/search` | 이름/주소/리뷰 키워드 검색 |
| | `GET` | `/api/restaurants/nearby` | 내 위치 기반 근처 맛집 검색 |
| | `GET` | `/api/restaurants/high-rated` | 고평점(4점 이상) 맛집 검색 |

---

## 📄 라이선스

이 프로젝트는 **MIT License**를 따릅니다.
