package com.motmap.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "카카오 소셜 로그인 요청 DTO")
public class KakaoLoginRequestDto {

    @Schema(description = "카카오 고유 사용자 ID", example = "38291048", required = true)
    @NotBlank(message = "카카오 ID는 필수입니다")
    private String kakaoId;

    @Schema(description = "카카오 계정 닉네임", example = "카카오라이언")
    private String nickname;

    @Schema(description = "카카오 계정 이메일", example = "kakao_user@kakao.com")
    private String email;

    @Schema(description = "프로필 이미지 URL", example = "http://k.kakaocdn.net/...")
    private String profileImageUrl;
}
