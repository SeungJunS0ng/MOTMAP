package com.motmap.service;

import com.motmap.dto.AuthResponseDto;
import com.motmap.dto.LoginRequestDto;
import com.motmap.dto.SignupRequestDto;
import com.motmap.dto.UserResponseDto;
import com.motmap.entity.Role;
import com.motmap.entity.User;
import com.motmap.exception.AuthenticationFailedException;
import com.motmap.exception.DuplicateUserException;
import com.motmap.exception.UserNotFoundException;
import com.motmap.repository.UserRepository;
import com.motmap.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 인증 서비스
 * 로그인, 회원가입, 토큰 검증 등 인증 관련 비즈니스 로직 처리
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    /**
     * 로그인 처리
     *
     * @param loginRequest 로그인 요청 정보
     * @return 인증 응답 (JWT 토큰 포함)
     * @throws AuthenticationFailedException 인증 실패 시
     */
    @Transactional
    public AuthResponseDto login(LoginRequestDto loginRequest) {
        log.info("로그인 시도: {}", loginRequest.getUsername());

        try {
            // Spring Security 인증 처리
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // 인증 성공 후 사용자 정보 조회
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다"));

            // 마지막 로그인 시간 업데이트
            user.updateLastLoginAt();
            userRepository.save(user);

            // JWT 토큰 생성
            String token = jwtUtil.generateToken(userDetails);

            log.info("로그인 성공: {}", user.getUsername());

            return AuthResponseDto.builder()
                    .token(token)
                    .type("Bearer")
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .role(user.getRole().name())
                    .build();

        } catch (BadCredentialsException e) {
            log.warn("로그인 실패 - 잘못된 인증 정보: {}", loginRequest.getUsername());
            throw new AuthenticationFailedException("아이디 또는 비밀번호가 올바르지 않습니다");
        } catch (Exception e) {
            log.error("로그인 처리 중 오류 발생", e);
            throw new AuthenticationFailedException("로그인 처리 중 오류가 발생했습니다");
        }
    }

    /**
     * 회원가입 처리
     *
     * @param signupRequest 회원가입 요청 정보
     * @return 생성된 사용자 정보
     * @throws DuplicateUserException 중복된 사용자명/이메일 시
     */
    @Transactional
    public UserResponseDto signup(SignupRequestDto signupRequest) {
        log.info("회원가입 시도: {}", signupRequest.getUsername());

        // 중복 확인
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            log.warn("중복된 사용자명: {}", signupRequest.getUsername());
            throw new DuplicateUserException("이미 사용중인 사용자명입니다: " + signupRequest.getUsername());
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            log.warn("중복된 이메일: {}", signupRequest.getEmail());
            throw new DuplicateUserException("이미 사용중인 이메일입니다: " + signupRequest.getEmail());
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

        // 사용자 생성
        User user = User.builder()
                .username(signupRequest.getUsername())
                .email(signupRequest.getEmail())
                .password(encodedPassword)
                .nickname(signupRequest.getNickname())
                .role(Role.USER)
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);
        log.info("회원가입 성공: {}", savedUser.getUsername());

        return UserResponseDto.from(savedUser);
    }

    /**
     * 토큰으로 사용자 정보 조회
     *
     * @param token JWT 토큰
     * @return 사용자 정보
     */
    @Transactional(readOnly = true)
    public UserResponseDto getUserFromToken(String token) {
        String username = jwtUtil.extractUsername(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + username));

        return UserResponseDto.from(user);
    }

    /**
     * 사용자명으로 사용자 정보 조회
     */
    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + username));
    }
}

