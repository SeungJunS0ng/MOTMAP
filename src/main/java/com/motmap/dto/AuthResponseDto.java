package com.motmap.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 인증 응답 DTO (JWT 토큰 포함)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDto {

    private String token;
    private String type = "Bearer";
    private String username;
    private String email;
    private String nickname;
    private String role;

    public AuthResponseDto(String token, String username, String email, String nickname, String role) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.nickname = nickname;
        this.role = role;
    }
}

