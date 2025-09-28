# MOTMAP - 카카오맵 기반 맛집 저장 및 리뷰 프로젝트 🍽️

Spring Boot와 카카오맵 API를 활용한 나만의 맛집 지도 서비스입니다.

## 🌟 주요 기능

- **카카오맵 연동**: 실제 지도에서 맛집 위치 확인 및 추가
- **맛집 관리**: CRUD 기능으로 맛집 정보 관리
- **리뷰 시스템**: 5점 만점 별점 평가 및 리뷰 작성
- **검색 기능**: 이름, 주소, 리뷰 내용으로 통합 검색
- **카테고리 분류**: 한식, 중식, 일식, 양식, 카페, 기타로 분류
- **위치 기반 검색**: 현재 위치 기준 근처 맛집 검색
- **반응형 디자인**: 모바일/데스크톱 환경 지원

## 🛠️ 기술 스택

### Backend
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **H2 Database** (인메모리)
- **Maven**

### Frontend
- **HTML5 / CSS3 / JavaScript (ES6+)**
- **Kakao Map API**
- **Thymeleaf**

## 📋 사전 요구사항

1. **Java 17** 이상
2. **Maven** (또는 IDE의 내장 Maven)
3. **카카오 개발자 계정** 및 JavaScript 키

## 🚀 설치 및 실행

### 1. 프로젝트 클론
```bash
git clone [your-repository-url]
cd MOTMAP
```

### 2. 카카오맵 API 키 설정
1. [Kakao Developers](https://developers.kakao.com/)에서 애플리케이션 등록
2. JavaScript 키 발급
3. `src/main/resources/templates/index.html` 파일에서 `YOUR_KAKAO_API_KEY`를 실제 키로 교체:
```html
<script type="text/javascript" src="//dapi.kakao.com/v2/maps/sdk.js?appkey=실제_발급받은_키&libraries=services"></script>
```

### 3. 애플리케이션 실행

#### Maven 사용
```bash
mvn spring-boot:run
```

#### IDE 사용 (IntelliJ IDEA / Eclipse)
- `MotmapApplication.java` 파일을 열고 실행

### 4. 웹 브라우저에서 확인
```
http://localhost:8080
```

## 📁 프로젝트 구조

```
MOTMAP/
├── src/main/java/com/motmap/
│   ├── MotmapApplication.java          # 메인 애플리케이션
│   ├── controller/
│   │   ├── RestaurantController.java  # REST API 컨트롤러
│   │   └── ViewController.java         # 뷰 컨트롤러
│   ├── service/
│   │   └── RestaurantService.java      # 비즈니스 로직
│   ├── repository/
│   │   └── RestaurantRepository.java   # 데이터 액세스
│   ├── entity/
│   │   └── Restaurant.java             # 엔티티 클래스
│   └── dto/
│       ├── RestaurantRequestDto.java   # 요청 DTO
│       └── RestaurantResponseDto.java  # 응답 DTO
├── src/main/resources/
│   ├── application.yml                 # 설정 파일
│   ├── templates/
│   │   └── index.html                  # 메인 페이지
│   └── static/
│       ├── css/style.css               # 스타일시트
│       └── js/                         # JavaScript 파일들
└── pom.xml                             # Maven 설정
```

## 🔧 API 엔드포인트

### 맛집 관리
- `GET /api/restaurants` - 전체 맛집 조회
- `GET /api/restaurants/{id}` - 특정 맛집 조회
- `POST /api/restaurants` - 맛집 추가
- `PUT /api/restaurants/{id}` - 맛집 수정
- `DELETE /api/restaurants/{id}` - 맛집 삭제

### 검색 및 필터링
- `GET /api/restaurants/search?keyword={keyword}` - 키워드 검색
- `GET /api/restaurants/category/{category}` - 카테고리별 조회
- `GET /api/restaurants/sorted/rating` - 평점순 정렬
- `GET /api/restaurants/sorted/date` - 최신순 정렬
- `GET /api/restaurants/nearby?lat={lat}&lng={lng}&radius={radius}` - 근처 맛집 검색

## 💾 데이터베이스

개발 환경에서는 H2 인메모리 데이터베이스를 사용합니다.
- **H2 콘솔**: `http://localhost:8080/h2-console`
- **JDBC URL**: `jdbc:h2:mem:motmap`
- **사용자명**: `sa`
- **패스워드**: (없음)

## 🎯 사용 방법

1. **맛집 추가**: 지도에서 원하는 위치 클릭 → 폼 작성 → 저장
2. **맛집 검색**: 검색창에 키워드 입력 또는 카테고리 필터 사용
3. **맛집 보기**: 목록에서 "지도에서 보기" 버튼 클릭
4. **맛집 삭제**: 목록에서 "삭제" 버튼 클릭

## 🔮 향후 개선 계획

- [ ] 사용자 인증/권한 관리
- [ ] 이미지 업로드 기능
- [ ] 맛집 공유 기능
- [ ] 즐겨찾기 기능
- [ ] 리뷰 댓글 시스템
- [ ] 실제 데이터베이스 연동 (MySQL/PostgreSQL)

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch
3. Commit your Changes
4. Push to the Branch
5. Open a Pull Request

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 있습니다.

## 📞 문의

프로젝트에 대한 문의사항이 있으시면 이슈를 생성해주세요.

---
Made with ❤️ using Spring Boot & Kakao Map API
