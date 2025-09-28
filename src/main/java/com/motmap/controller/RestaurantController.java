package com.motmap.controller;

import com.motmap.dto.RestaurantRequestDto;
import com.motmap.dto.RestaurantResponseDto;
import com.motmap.entity.Category;
import com.motmap.service.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @Autowired
    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    // 모든 맛집 조회
    @GetMapping
    public ResponseEntity<List<RestaurantResponseDto>> getAllRestaurants() {
        List<RestaurantResponseDto> restaurants = restaurantService.getAllRestaurants();
        return ResponseEntity.ok(restaurants);
    }

    // 특정 맛집 조회
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponseDto> getRestaurantById(@PathVariable Long id) {
        RestaurantResponseDto restaurant = restaurantService.getRestaurantById(id);
        return ResponseEntity.ok(restaurant);
    }

    // 맛집 추가
    @PostMapping
    public ResponseEntity<RestaurantResponseDto> addRestaurant(@Valid @RequestBody RestaurantRequestDto requestDto) {
        RestaurantResponseDto restaurant = restaurantService.addRestaurant(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurant);
    }

    // 맛집 수정
    @PutMapping("/{id}")
    public ResponseEntity<RestaurantResponseDto> updateRestaurant(
            @PathVariable Long id,
            @Valid @RequestBody RestaurantRequestDto requestDto) {
        RestaurantResponseDto restaurant = restaurantService.updateRestaurant(id, requestDto);
        return ResponseEntity.ok(restaurant);
    }

    // 맛집 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Long id) {
        restaurantService.deleteRestaurant(id);
        return ResponseEntity.noContent().build();
    }

    // 카테고리별 조회
    @GetMapping("/category/{category}")
    public ResponseEntity<List<RestaurantResponseDto>> getRestaurantsByCategory(@PathVariable Category category) {
        List<RestaurantResponseDto> restaurants = restaurantService.getRestaurantsByCategory(category);
        return ResponseEntity.ok(restaurants);
    }

    // 평점별 조회
    @GetMapping("/rating/{rating}")
    public ResponseEntity<List<RestaurantResponseDto>> getRestaurantsByRating(@PathVariable Integer rating) {
        List<RestaurantResponseDto> restaurants = restaurantService.getRestaurantsByRating(rating);
        return ResponseEntity.ok(restaurants);
    }

    // 키워드 검색
    @GetMapping("/search")
    public ResponseEntity<List<RestaurantResponseDto>> searchRestaurants(@RequestParam String keyword) {
        List<RestaurantResponseDto> restaurants = restaurantService.searchRestaurants(keyword);
        return ResponseEntity.ok(restaurants);
    }

    // 평점순 정렬
    @GetMapping("/sorted/rating")
    public ResponseEntity<List<RestaurantResponseDto>> getRestaurantsByRatingOrder() {
        List<RestaurantResponseDto> restaurants = restaurantService.getRestaurantsByRatingOrder();
        return ResponseEntity.ok(restaurants);
    }

    // 최신순 정렬
    @GetMapping("/sorted/date")
    public ResponseEntity<List<RestaurantResponseDto>> getRestaurantsByDateOrder() {
        List<RestaurantResponseDto> restaurants = restaurantService.getRestaurantsByDateOrder();
        return ResponseEntity.ok(restaurants);
    }

    // 근처 맛집 검색
    @GetMapping("/nearby")
    public ResponseEntity<List<RestaurantResponseDto>> getNearbyRestaurants(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(required = false) Double radius) {
        List<RestaurantResponseDto> restaurants = restaurantService.getNearbyRestaurants(latitude, longitude, radius);
        return ResponseEntity.ok(restaurants);
    }
}
