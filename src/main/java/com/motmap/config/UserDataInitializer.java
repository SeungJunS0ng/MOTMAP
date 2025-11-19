package com.motmap.config;

import com.motmap.entity.Role;
import com.motmap.entity.User;
import com.motmap.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 더미 사용자 데이터 초기화
 * 테스트용 사용자 계정 생성
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class UserDataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initUserData() {
        return args -> {
            // 이미 사용자가 있으면 스킵
            if (userRepository.count() > 0) {
                log.info("사용자 데이터가 이미 존재합니다. 초기화를 스킵합니다.");
                return;
            }

            log.info("더미 사용자 데이터 생성을 시작합니다...");

            // 관리자 계정
            createUser("admin", "admin@motmap.com", "admin1234", "관리자", Role.ADMIN);

            // 일반 사용자 계정들
            createUser("john", "john@example.com", "john1234", "존", Role.USER);
            createUser("emily", "emily@example.com", "emily1234", "에밀리", Role.USER);
            createUser("david", "david@example.com", "david1234", "데이비드", Role.USER);
            createUser("sarah", "sarah@example.com", "sarah1234", "사라", Role.USER);
            createUser("michael", "michael@example.com", "michael1234", "마이클", Role.USER);

            // 한국 이름 사용자들
            createUser("kim", "kim@example.com", "kim1234", "김철수", Role.USER);
            createUser("lee", "lee@example.com", "lee1234", "이영희", Role.USER);
            createUser("park", "park@example.com", "park1234", "박민수", Role.USER);
            createUser("choi", "choi@example.com", "choi1234", "최지원", Role.USER);
            createUser("jung", "jung@example.com", "jung1234", "정수민", Role.USER);
            createUser("kang", "kang@example.com", "kang1234", "강준호", Role.USER);
            createUser("han", "han@example.com", "han1234", "한서연", Role.USER);
            createUser("song", "song@example.com", "song1234", "송민재", Role.USER);
            createUser("yoon", "yoon@example.com", "yoon1234", "윤지아", Role.USER);

            // 맛집 리뷰어 계정들
            createUser("foodlover", "foodlover@example.com", "food1234", "맛집탐험가", Role.USER);
            createUser("gourmet", "gourmet@example.com", "gourmet1234", "미식가", Role.USER);
            createUser("foodie", "foodie@example.com", "foodie1234", "푸디", Role.USER);
            createUser("tastyhunter", "tasty@example.com", "tasty1234", "맛헌터", Role.USER);
            createUser("restaurant_lover", "restaurant@example.com", "rest1234", "식당러버", Role.USER);

            log.info("✅ 더미 사용자 데이터 {}명 생성 완료!", userRepository.count());

            // 생성된 사용자 목록 로깅
            log.info("=== 생성된 사용자 목록 ===");
            log.info("관리자: admin / admin1234");
            log.info("일반 사용자: john, emily, david, sarah, michael / 비밀번호: [username]1234");
            log.info("한국 사용자: kim, lee, park, choi, jung, kang, han, song, yoon / 비밀번호: [username]1234");
            log.info("리뷰어: foodlover, gourmet, foodie, tastyhunter, restaurant_lover / 비밀번호: [username]1234");
            log.info("========================");
        };
    }

    /**
     * 사용자 생성 헬퍼 메서드
     */
    private void createUser(String username, String email, String password, String nickname, Role role) {
        try {
            User user = User.builder()
                    .username(username)
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .nickname(nickname)
                    .role(role)
                    .enabled(true)
                    .build();

            userRepository.save(user);
            log.debug("사용자 생성: {} ({})", username, nickname);
        } catch (Exception e) {
            log.error("사용자 생성 실패: {}", username, e);
        }
    }
}

