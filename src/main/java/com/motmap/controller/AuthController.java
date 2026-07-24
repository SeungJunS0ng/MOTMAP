package com.motmap.controller;

import com.motmap.dto.AuthResponseDto;
import com.motmap.dto.LoginRequestDto;
import com.motmap.dto.SignupRequestDto;
import com.motmap.dto.UserResponseDto;
import com.motmap.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 컨트롤러
 * 로그인, 회원가입, 사용자 정보 조회 API
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "인증", description = "로그인, 회원가입 API")
public class AuthController {

    private final AuthService authService;

    /**
     * 로그인
     *
     * @param loginRequest 로그인 요청
     * @return 인증 응답 (JWT 토큰 포함)
     */
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "사용자명과 비밀번호로 로그인하여 JWT 토큰을 발급받습니다")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        log.info("로그인 요청: {}", loginRequest.getUsername());

        AuthResponseDto response = authService.login(loginRequest);

        log.info("로그인 성공: {}", loginRequest.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/kakao")
    @Operation(summary = "카카오 소셜 로그인", description = "카카오 소셜 회원가입/로그인으로 JWT 토큰을 발급받습니다")
    public ResponseEntity<AuthResponseDto> kakaoLogin(@Valid @RequestBody com.motmap.dto.KakaoLoginRequestDto kakaoRequest) {
        log.info("카카오 소셜 로그인 요청: {}", kakaoRequest.getKakaoId());
        AuthResponseDto response = authService.kakaoLogin(kakaoRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * 회원가입
     *
     * @param signupRequest 회원가입 요청
     * @return 생성된 사용자 정보
     */
    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다")
    public ResponseEntity<UserResponseDto> signup(@Valid @RequestBody SignupRequestDto signupRequest) {
        log.info("회원가입 요청: {}", signupRequest.getUsername());

        UserResponseDto response = authService.signup(signupRequest);

        log.info("회원가입 성공: {}", signupRequest.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 현재 로그인한 사용자 정보 조회
     *
     * @return 현재 사용자 정보
     */
    @GetMapping("/me")
    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다")
    public ResponseEntity<UserResponseDto> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        log.debug("현재 사용자 정보 조회: {}", username);

        UserResponseDto response = UserResponseDto.from(
                authService.getUserByUsername(username)
        );

        return ResponseEntity.ok(response);
    }

    /**
     * 토큰 유효성 검증
     *
     * @return 유효한 토큰이면 200 OK
     */
    @GetMapping("/validate")
    @Operation(summary = "토큰 검증", description = "JWT 토큰의 유효성을 검증합니다")
    public ResponseEntity<Void> validateToken() {
        // JWT 필터에서 이미 검증되었으므로, 이 엔드포인트에 도달했다면 유효한 토큰
        return ResponseEntity.ok().build();
    }
}

