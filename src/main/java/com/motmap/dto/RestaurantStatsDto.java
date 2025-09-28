package com.motmap.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "맛집 통계 정보 DTO")
public class RestaurantStatsDto {

    @Schema(description = "전체 맛집 수", example = "100")
    private long totalRestaurants;

    @Schema(description = "카테고리별 맛집 수")
    private Map<String, Long> categoryStats;

    @Schema(description = "평점별 맛집 수")
    private Map<Integer, Long> ratingStats;

    @Schema(description = "평균 평점", example = "4.2")
    private double averageRating;

    @Schema(description = "고평점 맛집 수 (4점 이상)", example = "45")
    private long highRatedCount;

    @Schema(description = "최신 등록 맛집 수 (최근 30일)", example = "12")
    private long recentRestaurantsCount;
}
