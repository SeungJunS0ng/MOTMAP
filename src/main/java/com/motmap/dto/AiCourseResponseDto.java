package com.motmap.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "AI 맛집 추천 코스 응답 DTO")
public class AiCourseResponseDto {

    @Schema(description = "코스 제목", example = "성수동 힐링 식도락 & 디저트 2차 코스")
    private String courseTitle;

    @Schema(description = "총 이동 거리", example = "도보 약 520m")
    private String totalDistanceText;

    @Schema(description = "총 이동 시간", example = "도보 약 7분")
    private String totalEstimatedTimeText;

    @Schema(description = "AI 코스 단계 목록")
    private List<CourseStepDto> steps;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CourseStepDto {
        private Integer stepNumber;
        private String stepTag;
        private RestaurantResponseDto restaurant;
        private String reason;
        private String walkTime;
    }
}
