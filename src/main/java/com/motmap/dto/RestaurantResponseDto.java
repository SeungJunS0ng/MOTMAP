package com.motmap.dto;

import com.motmap.entity.Category;
import com.motmap.entity.Restaurant;
import java.time.LocalDateTime;

public class RestaurantResponseDto {

    private Long id;
    private String name;
    private String address;
    private Category category;
    private String categoryDisplayName;
    private Integer rating;
    private String review;
    private Double latitude;
    private Double longitude;
    private LocalDateTime createdAt;
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
