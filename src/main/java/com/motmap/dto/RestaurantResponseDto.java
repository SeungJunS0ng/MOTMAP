package com.motmap.dto;

import com.motmap.entity.Category;
import com.motmap.entity.Restaurant;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "맛집 조회 응답 DTO")
public class RestaurantResponseDto {

    @Schema(description = "맛집 ID", example = "1")
    private Long id;

    @Schema(description = "맛집 이름", example = "명동교자")
    private String name;

    @Schema(description = "맛집 주소", example = "서울특별시 중구 명동2가 25-2")
    private String address;

    @Schema(description = "맛집 카테고리 코드", example = "KOREAN")
    private Category category;

    @Schema(description = "맛집 카테고리 한글명", example = "한식")
    private String categoryDisplayName;

    @Schema(description = "평점", example = "4", minimum = "1", maximum = "5")
    private Integer rating;

    @Schema(description = "리뷰 내용", example = "맛있는 만두집입니다!")
    private String review;

    @Schema(description = "위도", example = "37.563692")
    private Double latitude;

    @Schema(description = "경도", example = "126.982814")
    private Double longitude;

    @Schema(description = "생성일시", example = "2025-09-29T00:45:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시", example = "2025-09-29T12:30:00")
    private LocalDateTime updatedAt;

    // 기본 생성자
    public RestaurantResponseDto() {}

    // Entity에서 DTO로 변환하는 생성자
    public RestaurantResponseDto(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.address = restaurant.getAddress();
        this.category = restaurant.getCategory();
        this.categoryDisplayName = restaurant.getCategory().getDisplayName();
        this.rating = restaurant.getRating();
        this.review = restaurant.getReview();
        this.latitude = restaurant.getLatitude();
        this.longitude = restaurant.getLongitude();
        this.createdAt = restaurant.getCreatedAt();
        this.updatedAt = restaurant.getUpdatedAt();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public String getCategoryDisplayName() { return categoryDisplayName; }
    public void setCategoryDisplayName(String categoryDisplayName) { this.categoryDisplayName = categoryDisplayName; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getReview() { return review; }
    public void setReview(String review) { this.review = review; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
