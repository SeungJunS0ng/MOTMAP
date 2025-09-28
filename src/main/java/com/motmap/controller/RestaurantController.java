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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;

@Tag(name = "맛집 관리", description = "맛집 CRUD 및 검색 API")
@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @Autowired
    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @Operation(summary = "전체 맛집 조회", description = "등록된 모든 맛집 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping
    public ResponseEntity<List<RestaurantResponseDto>> getAllRestaurants() {
        List<RestaurantResponseDto> restaurants = restaurantService.getAllRestaurants();
        return ResponseEntity.ok(restaurants);
    }

    @Operation(summary = "맛집 상세 조회", description = "ID로 특정 맛집의 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponseDto> getRestaurantById(
            @Parameter(description = "맛집 ID", example = "1") @PathVariable Long id) {
        RestaurantResponseDto restaurant = restaurantService.getRestaurantById(id);
        return ResponseEntity.ok(restaurant);
    }

    @Operation(summary = "맛집 등록", description = "새로운 맛집을 등록합니다.")
    @PostMapping
    public ResponseEntity<RestaurantResponseDto> addRestaurant(@Valid @RequestBody RestaurantRequestDto requestDto) {
        RestaurantResponseDto restaurant = restaurantService.addRestaurant(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurant);
    }

    @Operation(summary = "맛집 수정", description = "기존 맛집 정보를 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<RestaurantResponseDto> updateRestaurant(
            @PathVariable Long id,
            @Valid @RequestBody RestaurantRequestDto requestDto) {
        RestaurantResponseDto restaurant = restaurantService.updateRestaurant(id, requestDto);
        return ResponseEntity.ok(restaurant);
    }

    @Operation(summary = "맛집 삭제", description = "ID로 특정 맛집 정보를 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Long id) {
        restaurantService.deleteRestaurant(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "카테고리별 맛집 조회", description = "특정 카테고리에 속한 맛집 목록을 조회합니다.")
    @GetMapping("/category/{category}")
    public ResponseEntity<List<RestaurantResponseDto>> getRestaurantsByCategory(@PathVariable Category category) {
        List<RestaurantResponseDto> restaurants = restaurantService.getRestaurantsByCategory(category);
        return ResponseEntity.ok(restaurants);
    }

    @Operation(summary = "평점별 맛집 조회", description = "특정 평점 이상의 맛집 목록을 조회합니다.")
    @GetMapping("/rating/{rating}")
    public ResponseEntity<List<RestaurantResponseDto>> getRestaurantsByRating(@PathVariable Integer rating) {
        List<RestaurantResponseDto> restaurants = restaurantService.getRestaurantsByRating(rating);
        return ResponseEntity.ok(restaurants);
    }

    @Operation(summary = "키워드로 맛집 검색", description = "이름이나 설명에 키워드가 포함된 맛집을 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<List<RestaurantResponseDto>> searchRestaurants(@RequestParam String keyword) {
        List<RestaurantResponseDto> restaurants = restaurantService.searchRestaurants(keyword);
        return ResponseEntity.ok(restaurants);
    }

    @Operation(summary = "평점순 맛집 정렬", description = "평점이 높은 순서대로 맛집 목록을 정렬합니다.")
    @GetMapping("/sorted/rating")
    public ResponseEntity<List<RestaurantResponseDto>> getRestaurantsByRatingOrder() {
        List<RestaurantResponseDto> restaurants = restaurantService.getRestaurantsByRatingOrder();
        return ResponseEntity.ok(restaurants);
    }

    @Operation(summary = "최신순 맛집 정렬", description = "등록일이 최신인 순서대로 맛집 목록을 정렬합니다.")
    @GetMapping("/sorted/date")
    public ResponseEntity<List<RestaurantResponseDto>> getRestaurantsByDateOrder() {
        List<RestaurantResponseDto> restaurants = restaurantService.getRestaurantsByDateOrder();
        return ResponseEntity.ok(restaurants);
    }

    @Operation(summary = "근처 맛집 검색", description = "현재 위치를 기준으로 근처의 맛집 목록을 검색합니다.")
    @GetMapping("/nearby")
    public ResponseEntity<List<RestaurantResponseDto>> getNearbyRestaurants(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(required = false) Double radius) {
        List<RestaurantResponseDto> restaurants = restaurantService.getNearbyRestaurants(latitude, longitude, radius);
        return ResponseEntity.ok(restaurants);
    }
}
