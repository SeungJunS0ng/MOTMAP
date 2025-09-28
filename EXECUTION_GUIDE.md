# 🚀 MOTMAP 실행 가이드

## 1. 카카오맵 API 설정 완료 ✅
- JavaScript 키 적용 완료: `3ecea445ce0565e4d9cc34bb5cd216b2`

## 2. 필요한 플랫폼 설정
카카오 개발자 콘솔에서 다음 작업을 완료해주세요:

1. **앱 설정** → **플랫폼** 메뉴 이동
2. **"Web 플랫폼 등록"** 클릭
3. **사이트 도메인**: `http://localhost:8080` 입력 후 저장

## 3. 애플리케이션 실행 방법

### IDE에서 실행 (IntelliJ IDEA 권장)
1. IntelliJ IDEA에서 프로젝트 열기
2. `src/main/java/com/motmap/MotmapApplication.java` 파일 열기
3. `main` 메소드 옆의 실행 버튼(▶️) 클릭
4. 또는 `Ctrl+Shift+F10` (Windows) / `Cmd+Shift+R` (Mac)

### Gradle 사용 (대안)
```bash
# Gradle Wrapper 사용
./gradlew bootRun
```

## 4. 접속 확인
애플리케이션이 성공적으로 시작되면:
- 웹 브라우저에서 `http://localhost:8080` 접속
- 카카오맵이 정상적으로 로드되는지 확인

## 5. 기능 테스트
1. **지도 표시**: 서울시청 중심으로 지도 로드
2. **현재 위치**: 📍 버튼으로 현재 위치 이동
3. **맛집 추가**: 지도 클릭 → 폼 작성 → 저장
4. **검색 기능**: 키워드 검색 및 필터링
5. **H2 데이터베이스**: `http://localhost:8080/h2-console` 접속 가능

## 트러블슈팅
- **지도가 안 보이는 경우**: 브라우저 콘솔(F12)에서 API 키 관련 오류 확인
- **플랫폼 오류**: 카카오 개발자 콘솔에서 Web 플랫폼 등록 확인
- **포트 충돌**: `application.yml`에서 포트 변경 가능
