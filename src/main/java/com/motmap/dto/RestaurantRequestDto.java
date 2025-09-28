package com.motmap.dto;

import com.motmap.entity.Category;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "맛집 등록/수정 요청 DTO")
public class RestaurantRequestDto {

    @Schema(description = "맛집 이름", example = "명동교자", required = true)
    @NotBlank(message = "맛집 이름은 필수입니다")
    private String name;

    @Schema(description = "맛집 주소", example = "서울특별시 중구 명동2가 25-2", required = true)
    @NotBlank(message = "주소는 필수입니다")
    private String address;

    @Schema(description = "맛집 카테고리", example = "KOREAN", required = true)
    @NotNull(message = "카테고리는 필수입니다")
    private Category category;

    @Schema(description = "평점 (1-5점)", example = "4", minimum = "1", maximum = "5", required = true)
    @NotNull(message = "평점은 필수입니다")
    @Min(value = 1, message = "평점은 1점 이상이어야 합니다")
    @Max(value = 5, message = "평점은 5점 이하여야 합니다")
    private Integer rating;

    @Schema(description = "리뷰 내용", example = "맛있는 만두집입니다!")
    private String review;

    @Schema(description = "위도", example = "37.563692", required = true)
    @NotNull(message = "위도는 필수입니다")
    @DecimalMin(value = "-90.0", message = "위도는 -90도 이상이어야 합니다")
    @DecimalMax(value = "90.0", message = "위도는 90도 이하여야 합니다")
    private Double latitude;

    @Schema(description = "경도", example = "126.982814", required = true)
    @NotNull(message = "경도는 필수입니다")
    @DecimalMin(value = "-180.0", message = "경도는 -180도 이상이어야 합니다")
    @DecimalMax(value = "180.0", message = "경도는 180도 이하여야 합니다")
    private Double longitude;

    // 기본 생성자
    public RestaurantRequestDto() {}

    // 생성자
    public RestaurantRequestDto(String name, String address, Category category,
                               Integer rating, String review, Double latitude, Double longitude) {
        this.name = name;
        this.address = address;
        this.category = category;
        this.rating = rating;
        this.review = review;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getReview() { return review; }
    public void setReview(String review) { this.review = review; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}
