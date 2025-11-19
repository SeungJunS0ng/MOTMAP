# MySQL 데이터베이스 설정 가이드

## 📋 사전 요구사항

- **MySQL 8.0 이상** 설치 필요
- MySQL 서버가 실행 중이어야 함

## 🔧 1단계: MySQL 데이터베이스 생성

### Windows (PowerShell 또는 CMD):

```powershell
# MySQL 루트 계정으로 접속
mysql -u root -p

# MySQL 프롬프트에서 스크립트 실행
source C:/Coding/MOTMAP/scripts/create_mysql_db.sql;
exit;
```

### 또는 한 줄로 실행:

```powershell
mysql -u root -p < C:\Coding\MOTMAP\scripts\create_mysql_db.sql
```

## ✅ 2단계: 데이터베이스 생성 확인

```powershell
# admin 계정으로 접속 테스트
mysql -u admin -p
# 비밀번호 입력: 091122john

# MySQL 프롬프트에서 확인
USE motmap;
SHOW TABLES;
exit;
```

## 🚀 3단계: 애플리케이션 실행

### IntelliJ IDEA:
1. `MotmapApplication.java` 실행
2. 콘솔에서 MySQL 연결 로그 확인

### Gradle:
```powershell
.\gradlew.bat clean bootRun
```

## 📊 4단계: 실행 확인

### 성공 시 로그 메시지:
```
HikariPool-1 - Starting...
HikariPool-1 - Start completed.
Hibernate: create table restaurants (...)
Tomcat started on port 8080
Started MotmapApplication in X.XXX seconds
✅ 초기 맛집 데이터 5개가 성공적으로 생성되었습니다.
```

### 접속 URL:
- **메인 페이지**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **MySQL 직접 접속**: 
  ```
  mysql -u admin -p motmap
  ```

## 🔍 데이터베이스 확인

```sql
-- 테이블 목록 확인
SHOW TABLES;

-- 맛집 데이터 확인
SELECT * FROM restaurants;

-- 테이블 구조 확인
DESCRIBE restaurants;
```

## ⚙️ 현재 설정 정보

```yaml
데이터베이스: motmap
호스트: localhost
포트: 3306
사용자: admin
비밀번호: 091122john
문자셋: utf8mb4
타임존: Asia/Seoul
```

## 🛠️ 문제 해결

### 1. "Access denied for user 'admin'@'localhost'" 오류:

```sql
-- MySQL root로 접속하여 권한 재부여
mysql -u root -p
GRANT ALL PRIVILEGES ON motmap.* TO 'admin'@'localhost';
FLUSH PRIVILEGES;
exit;
```

### 2. "Unknown database 'motmap'" 오류:

```sql
-- 데이터베이스가 없는 경우 생성
mysql -u root -p
CREATE DATABASE motmap CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
exit;
```

### 3. MySQL 서버가 실행되지 않는 경우:

**Windows:**
```powershell
# 서비스 시작
net start MySQL80

# 또는 서비스 관리자에서 'MySQL80' 서비스 시작
```

### 4. 포트 3306이 사용 중인 경우:

```powershell
# 포트 사용 확인
netstat -ano | findstr :3306

# MySQL 서비스 재시작
net stop MySQL80
net start MySQL80
```

## 📝 데이터베이스 스키마

```sql
CREATE TABLE restaurants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    address VARCHAR(200) NOT NULL,
    category VARCHAR(255) NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    rating INT NOT NULL,
    review VARCHAR(1000),
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6),
    
    -- 인덱스
    INDEX idx_restaurant_category (category),
    INDEX idx_restaurant_rating (rating),
    INDEX idx_restaurant_location (latitude, longitude),
    INDEX idx_restaurant_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
```

## 🎯 초기 데이터

애플리케이션 시작 시 자동으로 5개의 테스트 맛집이 생성됩니다:
1. 명동교자 (한식) ⭐⭐⭐⭐
2. 전주중앙회관 (한식) ⭐⭐⭐⭐⭐
3. 스타벅스 명동점 (카페) ⭐⭐⭐⭐
4. 교동짬뽕 (중식) ⭐⭐⭐⭐
5. 긴자료코 (일식) ⭐⭐⭐⭐⭐

---

**설정 완료 후 애플리케이션을 실행하세요!** 🚀

