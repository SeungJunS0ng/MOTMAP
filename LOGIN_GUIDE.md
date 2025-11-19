# 🔐 MOTMAP 로그인 & 인증 가이드

## 📋 목차
1. [기능 개요](#기능-개요)
2. [API 엔드포인트](#api-엔드포인트)
3. [테스트 계정](#테스트-계정)
4. [사용 예시](#사용-예시)
5. [에러 코드](#에러-코드)

---

## 🎯 기능 개요

### 구현된 기능
- ✅ JWT 기반 인증 시스템 (Stateless)
- ✅ 회원가입 및 로그인
- ✅ BCrypt 비밀번호 암호화
- ✅ Role 기반 접근 제어 (USER, ADMIN)
- ✅ **맛집 소유권 관리** - 본인이 등록한 맛집만 수정/삭제 가능
- ✅ JWT 토큰 자동 검증 (24시간 유효)
- ✅ 20명의 더미 사용자 자동 생성

### 보안 기능
- 🔒 비밀번호 BCrypt 암호화 (SHA-256)
- 🔒 JWT 토큰 서명 검증
- 🔒 토큰 만료 시간 체크
- 🔒 리소스 소유권 검증
- 🔒 중복 사용자명/이메일 검증

---

## 📡 API 엔드포인트

### 1️⃣ 회원가입
```http
POST /api/auth/signup
Content-Type: application/json

{
  "username": "newuser",
  "email": "newuser@example.com",
  "password": "password123",
  "nickname": "새로운유저"
}
```

**응답 (201 Created):**
```json
{
  "id": 21,
  "username": "newuser",
  "email": "newuser@example.com",
  "nickname": "새로운유저",
  "role": "USER",
  "enabled": true,
  "createdAt": "2025-11-19T10:30:00",
  "lastLoginAt": null
}
```

### 2️⃣ 로그인
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "john",
  "password": "john1234"
}
```

**응답 (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6ImpvaG4iLCJhdXRob3JpdGllcyI6W3siYXV0aG9yaXR5IjoiUk9MRV9VU0VSIn1dLCJzdWIiOiJqb2huIiwiaWF0IjoxNzMyMDA0NDAwLCJleHAiOjE3MzIwOTA4MDB9.signature",
  "type": "Bearer",
  "username": "john",
  "email": "john@example.com",
  "nickname": "존",
  "role": "USER"
}
```

### 3️⃣ 내 정보 조회
```http
GET /api/auth/me
Authorization: Bearer {token}
```

**응답 (200 OK):**
```json
{
  "id": 2,
  "username": "john",
  "email": "john@example.com",
  "nickname": "존",
  "role": "USER",
  "enabled": true,
  "createdAt": "2025-11-19T10:00:00",
  "lastLoginAt": "2025-11-19T10:30:00"
}
```

### 4️⃣ 토큰 검증
```http
GET /api/auth/validate
Authorization: Bearer {token}
```

**응답:** 200 OK (유효) / 401 Unauthorized (무효)

---

## 👥 테스트 계정

### 관리자 계정
```
사용자명: admin
비밀번호: admin1234
권한: ADMIN
```

### 일반 사용자 (영문)
| 사용자명 | 비밀번호 | 닉네임 |
|---------|---------|--------|
| john | john1234 | 존 |
| emily | emily1234 | 에밀리 |
| david | david1234 | 데이비드 |
| sarah | sarah1234 | 사라 |
| michael | michael1234 | 마이클 |

### 일반 사용자 (한국)
| 사용자명 | 비밀번호 | 닉네임 |
|---------|---------|--------|
| kim | kim1234 | 김철수 |
| lee | lee1234 | 이영희 |
| park | park1234 | 박민수 |
| choi | choi1234 | 최지원 |
| jung | jung1234 | 정수민 |
| kang | kang1234 | 강준호 |
| han | han1234 | 한서연 |
| song | song1234 | 송민재 |
| yoon | yoon1234 | 윤지아 |

### 맛집 리뷰어
| 사용자명 | 비밀번호 | 닉네임 |
|---------|---------|--------|
| foodlover | food1234 | 맛집탐험가 |
| gourmet | gourmet1234 | 미식가 |
| foodie | foodie1234 | 푸디 |
| tastyhunter | tasty1234 | 맛헌터 |
| restaurant_lover | rest1234 | 식당러버 |

---

## 💡 사용 예시

### 시나리오 1: 회원가입 → 로그인 → 맛집 등록

#### 1단계: 회원가입
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "test1234",
    "nickname": "테스트유저"
  }'
```

#### 2단계: 로그인 (JWT 토큰 받기)
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "test1234"
  }'
```

**응답에서 token 값을 복사합니다.**

#### 3단계: 맛집 등록 (JWT 토큰 사용)
```bash
curl -X POST http://localhost:8080/api/restaurants \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {복사한_토큰}" \
  -d '{
    "name": "맛있는 한식당",
    "address": "서울특별시 강남구",
    "category": "KOREAN",
    "rating": 5,
    "review": "정말 맛있어요!",
    "latitude": 37.5,
    "longitude": 127.0
  }'
```

**응답:**
```json
{
  "id": 6,
  "name": "맛있는 한식당",
  "address": "서울특별시 강남구",
  "category": "KOREAN",
  "categoryDisplayName": "한식",
  "rating": 5,
  "review": "정말 맛있어요!",
  "latitude": 37.5,
  "longitude": 127.0,
  "createdAt": "2025-11-19T10:45:00",
  "updatedAt": "2025-11-19T10:45:00",
  "createdBy": "testuser",
  "createdByNickname": "테스트유저"
}
```

### 시나리오 2: 본인 맛집 수정 (성공)

```bash
curl -X PUT http://localhost:8080/api/restaurants/6 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {토큰}" \
  -d '{
    "name": "더 맛있는 한식당",
    "address": "서울특별시 강남구",
    "category": "KOREAN",
    "rating": 5,
    "review": "수정된 리뷰!",
    "latitude": 37.5,
    "longitude": 127.0
  }'
```

✅ **성공**: 200 OK

### 시나리오 3: 다른 사람 맛집 삭제 시도 (실패)

```bash
# john 계정으로 로그인한 상태에서
# emily가 등록한 맛집(ID: 7) 삭제 시도

curl -X DELETE http://localhost:8080/api/restaurants/7 \
  -H "Authorization: Bearer {john의_토큰}"
```

❌ **실패**: 403 Forbidden
```json
{
  "code": "A004",
  "message": "본인이 등록한 맛집만 삭제할 수 있습니다",
  "status": 403,
  "timestamp": "2025-11-19T10:50:00"
}
```

---

## 🔒 권한 체계

### Public (인증 불필요)
- ✅ GET `/` - 메인 페이지
- ✅ POST `/api/auth/login` - 로그인
- ✅ POST `/api/auth/signup` - 회원가입
- ✅ GET `/css/**`, `/js/**` - 정적 리소스
- ✅ GET `/swagger-ui/**` - API 문서

### Authenticated (JWT 토큰 필요)
- 🔐 GET `/api/auth/me` - 내 정보 조회
- 🔐 GET `/api/auth/validate` - 토큰 검증
- 🔐 GET `/api/restaurants` - 맛집 목록 조회
- 🔐 POST `/api/restaurants` - 맛집 등록
- 🔐 PUT `/api/restaurants/{id}` - 맛집 수정 (본인 것만)
- 🔐 DELETE `/api/restaurants/{id}` - 맛집 삭제 (본인 것만)

### Admin Only (관리자 전용)
- 👑 `/api/admin/**` - 관리자 API

---

## ⚠️ 에러 코드

### 인증/인가 관련
| 코드 | 상태 | 메시지 | 원인 |
|------|------|--------|------|
| A001 | 401 | 인증에 실패했습니다 | 잘못된 사용자명/비밀번호 |
| A002 | 401 | 유효하지 않은 토큰입니다 | 토큰 서명 검증 실패 |
| A003 | 401 | 만료된 토큰입니다 | 토큰 유효기간(24시간) 초과 |
| A004 | 403 | 접근 권한이 없습니다 | 리소스 소유자 아님 |

### 사용자 관련
| 코드 | 상태 | 메시지 | 원인 |
|------|------|--------|------|
| U001 | 404 | 사용자를 찾을 수 없습니다 | 존재하지 않는 사용자 |
| U002 | 409 | 이미 존재하는 사용자입니다 | 중복된 사용자명 |
| U003 | 409 | 이미 사용중인 이메일입니다 | 중복된 이메일 |
| U004 | 400 | 비밀번호가 일치하지 않습니다 | 비밀번호 불일치 |

### 맛집 관련
| 코드 | 상태 | 메시지 | 원인 |
|------|------|--------|------|
| R001 | 404 | 맛집을 찾을 수 없습니다 | 존재하지 않는 맛집 |
| R002 | 409 | 이미 등록된 맛집입니다 | 중복된 맛집 |

---

## 🧪 Swagger UI 테스트

### 1. Swagger UI 접속
```
http://localhost:8080/swagger-ui.html
```

### 2. 로그인 API 테스트
1. **인증** 섹션 펼치기
2. **POST /api/auth/login** 선택
3. "Try it out" 클릭
4. Request body 입력:
```json
{
  "username": "john",
  "password": "john1234"
}
```
5. "Execute" 클릭
6. **응답에서 token 값 복사**

### 3. JWT 토큰으로 인증하기
1. Swagger UI 우측 상단 **"Authorize"** 버튼 클릭
2. Value 입력란에 `Bearer {복사한_토큰}` 입력
3. "Authorize" 클릭
4. 이제 모든 보호된 API를 테스트할 수 있습니다!

### 4. 맛집 등록 테스트
1. **맛집 관리** 섹션의 **POST /api/restaurants** 선택
2. "Try it out" 클릭
3. Request body 입력
4. "Execute" 클릭
5. ✅ 201 Created 응답 확인

### 5. 본인 맛집 삭제 테스트
1. 위에서 생성한 맛집 ID 기억
2. **DELETE /api/restaurants/{id}** 선택
3. "Try it out" 클릭
4. id 파라미터에 맛집 ID 입력
5. "Execute" 클릭
6. ✅ 204 No Content 응답 확인

### 6. 다른 사용자로 로그인 후 삭제 시도
1. 다른 계정(예: emily)으로 로그인
2. 새 토큰으로 Authorize
3. john이 만든 맛집 삭제 시도
4. ❌ 403 Forbidden 확인 (본인 것만 삭제 가능)

---

## 🔧 Postman 테스트

### Collection 구성
```
MOTMAP API
├── Auth
│   ├── 회원가입 (POST /api/auth/signup)
│   ├── 로그인 (POST /api/auth/login)
│   ├── 내 정보 조회 (GET /api/auth/me)
│   └── 토큰 검증 (GET /api/auth/validate)
└── Restaurants
    ├── 맛집 목록 (GET /api/restaurants)
    ├── 맛집 등록 (POST /api/restaurants)
    ├── 맛집 수정 (PUT /api/restaurants/{id})
    └── 맛집 삭제 (DELETE /api/restaurants/{id})
```

### 환경 변수 설정
```
baseUrl: http://localhost:8080
token: (로그인 후 자동 저장)
```

### Pre-request Script (로그인 후 토큰 자동 저장)
```javascript
// 로그인 API의 Tests 탭에 추가
pm.test("Save token", function () {
    var jsonData = pm.response.json();
    pm.environment.set("token", jsonData.token);
});
```

### Authorization 설정
- Type: Bearer Token
- Token: `{{token}}`

---

## 📊 데이터베이스 확인

### MySQL 접속
```bash
mysql -u admin -p motmap
# 비밀번호: 091122john
```

### 사용자 조회
```sql
-- 전체 사용자 목록
SELECT id, username, email, nickname, role, enabled, created_at 
FROM users;

-- 최근 로그인한 사용자
SELECT username, nickname, last_login_at 
FROM users 
WHERE last_login_at IS NOT NULL
ORDER BY last_login_at DESC;
```

### 맛집과 작성자 함께 조회
```sql
-- 맛집과 작성자 정보
SELECT 
    r.id,
    r.name AS restaurant_name,
    r.category,
    r.rating,
    u.username,
    u.nickname,
    r.created_at
FROM restaurants r
LEFT JOIN users u ON r.user_id = u.id
ORDER BY r.created_at DESC;
```

### 사용자별 맛집 개수
```sql
SELECT 
    u.username,
    u.nickname,
    COUNT(r.id) AS restaurant_count
FROM users u
LEFT JOIN restaurants r ON u.id = r.user_id
GROUP BY u.id
ORDER BY restaurant_count DESC;
```

---

## 🎓 토큰 디코딩 (디버깅용)

### JWT.io에서 토큰 확인
1. https://jwt.io 접속
2. 토큰을 Encoded 란에 붙여넣기
3. Decoded 정보 확인:

```json
{
  "username": "john",
  "authorities": [
    {
      "authority": "ROLE_USER"
    }
  ],
  "sub": "john",
  "iat": 1732004400,
  "exp": 1732090800
}
```

### 토큰 구조
- **Header**: 알고리즘 (HS256)
- **Payload**: 사용자명, 권한, 발급/만료 시간
- **Signature**: 서버의 비밀키로 서명

---

## 💻 개발 팁

### 1. 토큰 만료 시간 변경
```yaml
# application.yml
jwt:
  expiration: 3600000  # 1시간 (밀리초)
```

### 2. 로그에서 인증 정보 확인
```java
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
log.info("Current user: {}", auth.getName());
log.info("Authorities: {}", auth.getAuthorities());
```

### 3. 커스텀 인증 체크
```java
@PreAuthorize("hasRole('ADMIN')")
public void adminOnlyMethod() {
    // 관리자만 실행 가능
}

@PreAuthorize("@restaurantService.isOwner(#id, authentication.name)")
public void ownerOnlyMethod(Long id) {
    // 소유자만 실행 가능
}
```

---

## 🚀 프로덕션 체크리스트

배포 전 확인사항:

- [ ] JWT Secret 키를 환경 변수로 변경
- [ ] 비밀번호 정책 강화 (최소 8자, 특수문자 포함)
- [ ] HTTPS 적용
- [ ] CORS 정책 설정
- [ ] Rate Limiting 적용
- [ ] 로그 민감정보 마스킹
- [ ] 비밀번호 찾기 기능 추가
- [ ] 이메일 인증 추가
- [ ] 계정 잠금 정책 (로그인 실패 5회)
- [ ] 리프레시 토큰 구현

---

**구현 완료일**: 2025-11-19  
**버전**: 1.0.0  
**작성자**: MOTMAP Development Team

