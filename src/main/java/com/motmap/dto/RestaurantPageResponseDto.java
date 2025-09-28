package com.motmap.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "페이징된 맛집 목록 응답 DTO")
public class RestaurantPageResponseDto {

    @Schema(description = "맛집 목록")
    private List<RestaurantResponseDto> restaurants;

    @Schema(description = "현재 페이지 번호", example = "0")
    private int pageNumber;

    @Schema(description = "페이지 크기", example = "10")
    private int pageSize;

    @Schema(description = "전체 요소 개수", example = "50")
    private long totalElements;

    @Schema(description = "전체 페이지 개수", example = "5")
    private int totalPages;

    @Schema(description = "첫 번째 페이지 여부", example = "true")
    private boolean first;

    @Schema(description = "마지막 페이지 여부", example = "false")
    private boolean last;
}
