package com.motmap.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "restaurants", indexes = {
    @Index(name = "idx_restaurant_category", columnList = "category"),
    @Index(name = "idx_restaurant_rating", columnList = "rating"),
    @Index(name = "idx_restaurant_location", columnList = "latitude, longitude"),
    @Index(name = "idx_restaurant_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"createdAt", "updatedAt"})
@EqualsAndHashCode(of = {"name", "address"})
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "맛집 이름은 필수입니다")
    private String name;

    @Column(nullable = false)
    @NotBlank(message = "주소는 필수입니다")
    private String address;

    @Column(nullable = false)
    @NotNull(message = "카테고리는 필수입니다")
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(nullable = false)
    @Min(value = 1, message = "평점은 1점 이상이어야 합니다")
    @Max(value = 5, message = "평점은 5점 이하여야 합니다")
    private Integer rating;

    @Column(length = 1000)
    private String review;

    @Column(nullable = false)
    @NotNull(message = "위도는 필수입니다")
    @DecimalMin(value = "-90.0", message = "위도는 -90도 이상이어야 합니다")
    @DecimalMax(value = "90.0", message = "위도는 90도 이하여야 합니다")
    private Double latitude;

    @Column(nullable = false)
    @NotNull(message = "경도는 필수입니다")
    @DecimalMin(value = "-180.0", message = "경도는 -180도 이상이어야 합니다")
    @DecimalMax(value = "180.0", message = "경도는 180도 이하여야 합니다")
    private Double longitude;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 편의 생성자
    public Restaurant(String name, String address, Category category, Integer rating,
                     String review, Double latitude, Double longitude) {
        this.name = name;
        this.address = address;
        this.category = category;
        this.rating = rating;
        this.review = review;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // 비즈니스 로직 메소드
    public void updateRestaurantInfo(String name, String address, Category category,
                                    Integer rating, String review, Double latitude, Double longitude) {
        this.name = name;
        this.address = address;
        this.category = category;
        this.rating = rating;
        this.review = review;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public boolean isHighRated() {
        return this.rating >= 4;
    }
}
