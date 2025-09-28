package com.motmap.dto;

import com.motmap.entity.Category;
import jakarta.validation.constraints.*;

public class RestaurantRequestDto {

    @NotBlank(message = "맛집 이름은 필수입니다")
    private String name;

    @NotBlank(message = "주소는 필수입니다")
    private String address;

    @NotNull(message = "카테고리는 필수입니다")
    private Category category;

    @NotNull(message = "평점은 필수입니다")
    @Min(value = 1, message = "평점은 1점 이상이어야 합니다")
    @Max(value = 5, message = "평점은 5점 이하여야 합니다")
    private Integer rating;

    private String review;

    @NotNull(message = "위도는 필수입니다")
    private Double latitude;

    @NotNull(message = "경도는 필수입니다")
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
