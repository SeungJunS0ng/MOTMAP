package com.motmap.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "맛집 카테고리")
public enum Category {
    @Schema(description = "한식")
    KOREAN("한식"),

    @Schema(description = "중식")
    CHINESE("중식"),

    @Schema(description = "일식")
    JAPANESE("일식"),

    @Schema(description = "양식")
    WESTERN("양식"),

    @Schema(description = "카페")
    CAFE("카페"),

    @Schema(description = "기타")
    ETC("기타");
    
    private final String displayName;
    
    Category(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
