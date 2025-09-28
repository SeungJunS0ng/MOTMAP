package com.motmap.repository;

import com.motmap.entity.Restaurant;
import com.motmap.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    // 카테고리별 조회
    List<Restaurant> findByCategory(Category category);

    // 평점별 조회
    List<Restaurant> findByRating(Integer rating);

    // 평점 이상 조회
    List<Restaurant> findByRatingGreaterThanEqual(Integer rating);

    // 이름으로 검색 (대소문자 무시)
    List<Restaurant> findByNameContainingIgnoreCase(String name);

    // 주소로 검색 (대소문자 무시)
    List<Restaurant> findByAddressContainingIgnoreCase(String address);

    // 리뷰로 검색 (대소문자 무시)
    List<Restaurant> findByReviewContainingIgnoreCase(String review);

    // 통합 검색 (이름, 주소, 리뷰에서 검색)
    @Query("SELECT r FROM Restaurant r WHERE " +
           "LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.address) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.review) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Restaurant> searchByKeyword(@Param("keyword") String keyword);

    // 평점순 정렬 (높은 순)
    List<Restaurant> findAllByOrderByRatingDesc();

    // 최신순 정렬
    List<Restaurant> findAllByOrderByCreatedAtDesc();

    // 이름과 주소로 중복 검사
    boolean existsByNameAndAddress(String name, String address);

    // 위치 기반 검색 (반경 내 검색)
    @Query(value = "SELECT * FROM restaurants WHERE " +
           "(6371 * acos(cos(radians(:lat)) * cos(radians(latitude)) * " +
           "cos(radians(longitude) - radians(:lng)) + sin(radians(:lat)) * " +
           "sin(radians(latitude)))) < :radius", nativeQuery = true)
    List<Restaurant> findNearbyRestaurants(@Param("lat") Double latitude,
                                         @Param("lng") Double longitude,
                                         @Param("radius") Double radius);
}
