package com.motmap.util;

import com.motmap.exception.InvalidTokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT 토큰 유틸리티 클래스
 * 토큰 생성, 검증, 정보 추출 기능 제공
 */
@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * Secret Key 생성
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * JWT 토큰 생성
     *
     * @param userDetails 사용자 정보
     * @return JWT 토큰
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", userDetails.getUsername());
        claims.put("authorities", userDetails.getAuthorities());

        return createToken(claims, userDetails.getUsername());
    }

    /**
     * 토큰 생성 로직
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 토큰에서 사용자명 추출
     *
     * @param token JWT 토큰
     * @return 사용자명
     */
    public String extractUsername(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (Exception e) {
            log.error("Failed to extract username from token", e);
            throw new InvalidTokenException("토큰에서 사용자 정보를 추출할 수 없습니다");
        }
    }

    /**
     * 토큰에서 만료 시간 추출
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * 토큰에서 특정 Claim 추출
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 토큰에서 모든 Claims 추출
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.error("Token expired: {}", e.getMessage());
            throw new InvalidTokenException("만료된 토큰입니다");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported token: {}", e.getMessage());
            throw new InvalidTokenException("지원하지 않는 토큰 형식입니다");
        } catch (MalformedJwtException e) {
            log.error("Malformed token: {}", e.getMessage());
            throw new InvalidTokenException("잘못된 형식의 토큰입니다");
        } catch (SecurityException | IllegalArgumentException e) {
            log.error("Invalid token: {}", e.getMessage());
            throw new InvalidTokenException("유효하지 않은 토큰입니다");
        }
    }

    /**
     * 토큰 만료 여부 확인
     */
    private Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (InvalidTokenException e) {
            return true;
        }
    }

    /**
     * 토큰 유효성 검증
     *
     * @param token       JWT 토큰
     * @param userDetails 사용자 정보
     * @return 유효 여부
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (InvalidTokenException e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 토큰 유효성 검증 (UserDetails 없이)
     *
     * @param token JWT 토큰
     * @return 유효 여부
     */
    public Boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (InvalidTokenException e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }
}

