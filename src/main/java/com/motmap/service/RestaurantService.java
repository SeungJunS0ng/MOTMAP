package com.motmap.service;

import com.motmap.dto.RestaurantRequestDto;
import com.motmap.dto.RestaurantResponseDto;
import com.motmap.entity.Category;
import com.motmap.entity.Restaurant;
import com.motmap.exception.RestaurantNotFoundException;
import com.motmap.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    @Autowired
    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    // 모든 맛집 조회
    @Transactional(readOnly = true)
    public List<RestaurantResponseDto> getAllRestaurants() {
        return restaurantRepository.findAll().stream()
                .map(RestaurantResponseDto::new)
                .collect(Collectors.toList());
    }

    // ID로 맛집 조회
    @Transactional(readOnly = true)
    public RestaurantResponseDto getRestaurantById(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException(id));
        return new RestaurantResponseDto(restaurant);
    }

    // 맛집 추가
    public RestaurantResponseDto addRestaurant(RestaurantRequestDto requestDto) {
        Restaurant restaurant = new Restaurant(
                requestDto.getName(),
                requestDto.getAddress(),
                requestDto.getCategory(),
                requestDto.getRating(),
                requestDto.getReview(),
                requestDto.getLatitude(),
                requestDto.getLongitude()
        );

        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        return new RestaurantResponseDto(savedRestaurant);
    }

    // 맛집 수정
    public RestaurantResponseDto updateRestaurant(Long id, RestaurantRequestDto requestDto) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException(id));

        restaurant.setName(requestDto.getName());
        restaurant.setAddress(requestDto.getAddress());
        restaurant.setCategory(requestDto.getCategory());
        restaurant.setRating(requestDto.getRating());
        restaurant.setReview(requestDto.getReview());
        restaurant.setLatitude(requestDto.getLatitude());
        restaurant.setLongitude(requestDto.getLongitude());

        Restaurant updatedRestaurant = restaurantRepository.save(restaurant);
        return new RestaurantResponseDto(updatedRestaurant);
    }

    // 맛집 삭제
    public void deleteRestaurant(Long id) {
        if (!restaurantRepository.existsById(id)) {
            throw new RestaurantNotFoundException(id);
        }
        restaurantRepository.deleteById(id);
    }

    // 카테고리별 조회
    @Transactional(readOnly = true)
    public List<RestaurantResponseDto> getRestaurantsByCategory(Category category) {
        return restaurantRepository.findByCategory(category).stream()
                .map(RestaurantResponseDto::new)
                .collect(Collectors.toList());
    }

    // 평점별 조회
    @Transactional(readOnly = true)
    public List<RestaurantResponseDto> getRestaurantsByRating(Integer rating) {
        return restaurantRepository.findByRatingGreaterThanEqual(rating).stream()
                .map(RestaurantResponseDto::new)
                .collect(Collectors.toList());
    }

    // 키워드 검색
    @Transactional(readOnly = true)
    public List<RestaurantResponseDto> searchRestaurants(String keyword) {
        return restaurantRepository.searchByKeyword(keyword).stream()
                .map(RestaurantResponseDto::new)
                .collect(Collectors.toList());
    }

    // 평점순 정렬
    @Transactional(readOnly = true)
    public List<RestaurantResponseDto> getRestaurantsByRatingOrder() {
        return restaurantRepository.findAllByOrderByRatingDesc().stream()
                .map(RestaurantResponseDto::new)
                .collect(Collectors.toList());
    }

    // 최신순 정렬
    @Transactional(readOnly = true)
    public List<RestaurantResponseDto> getRestaurantsByDateOrder() {
        return restaurantRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(RestaurantResponseDto::new)
                .collect(Collectors.toList());
    }

    // 근처 맛집 검색 (반경 5km 기본)
    @Transactional(readOnly = true)
    public List<RestaurantResponseDto> getNearbyRestaurants(Double latitude, Double longitude, Double radius) {
        if (radius == null) {
            radius = 5.0; // 기본 5km
        }
        return restaurantRepository.findNearbyRestaurants(latitude, longitude, radius).stream()
                .map(RestaurantResponseDto::new)
                .collect(Collectors.toList());
    }
}
