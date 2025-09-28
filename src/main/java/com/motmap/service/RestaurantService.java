package com.motmap.service;

import com.motmap.dto.RestaurantRequestDto;
import com.motmap.dto.RestaurantResponseDto;
import com.motmap.entity.Category;
import com.motmap.entity.Restaurant;
import com.motmap.exception.RestaurantNotFoundException;
import com.motmap.exception.DuplicateRestaurantException;
import com.motmap.exception.InvalidLocationException;
import com.motmap.repository.RestaurantRepository;
import com.motmap.util.LocationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    // 모든 맛집 조회
    @Transactional(readOnly = true)
    public List<RestaurantResponseDto> getAllRestaurants() {
        log.debug("전체 맛집 목록 조회 시작");
        List<RestaurantResponseDto> restaurants = restaurantRepository.findAll().stream()
                .map(RestaurantResponseDto::from)
                .toList();
        log.debug("전체 맛집 {}개 조회 완료", restaurants.size());
        return restaurants;
    }

    // ID로 맛집 조회
    @Transactional(readOnly = true)
    public RestaurantResponseDto getRestaurantById(Long id) {
        log.debug("맛집 조회 시작 - ID: {}", id);
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException(id));
        log.debug("맛집 조회 완료 - 이름: {}", restaurant.getName());
        return RestaurantResponseDto.from(restaurant);
    }

    // 맛집 추가
    public RestaurantResponseDto addRestaurant(RestaurantRequestDto requestDto) {
        log.debug("맛집 추가 시작 - 이름: {}, 주소: {}", requestDto.getName(), requestDto.getAddress());

        // 위치 유효성 검증
        if (!LocationUtils.isValidLocation(requestDto.getLatitude(), requestDto.getLongitude())) {
            throw new InvalidLocationException(requestDto.getLatitude(), requestDto.getLongitude());
        }

        // 중복 검사
        if (restaurantRepository.existsByNameAndAddress(requestDto.getName(), requestDto.getAddress())) {
            log.warn("중복된 맛집 등록 시도 - 이름: {}, 주소: {}", requestDto.getName(), requestDto.getAddress());
            throw new DuplicateRestaurantException(requestDto.getName(), requestDto.getAddress());
        }

        Restaurant restaurant = createRestaurantEntity(requestDto);
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        log.info("맛집 추가 완료 - ID: {}, 이름: {}", savedRestaurant.getId(), savedRestaurant.getName());
        return RestaurantResponseDto.from(savedRestaurant);
    }

    // Restaurant 엔티티 생성 헬퍼 메소드
    private Restaurant createRestaurantEntity(RestaurantRequestDto requestDto) {
        return Restaurant.builder()
                .name(requestDto.getName())
                .address(requestDto.getAddress())
                .category(requestDto.getCategory())
                .rating(requestDto.getRating())
                .review(requestDto.getReview())
                .latitude(requestDto.getLatitude())
                .longitude(requestDto.getLongitude())
                .build();
    }

    // 맛집 수정
    public RestaurantResponseDto updateRestaurant(Long id, RestaurantRequestDto requestDto) {
        log.debug("맛집 수정 시작 - ID: {}, 이름: {}", id, requestDto.getName());

        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException(id));

        // 엔티티의 비즈니스 메소드 활용
        restaurant.updateRestaurantInfo(
                requestDto.getName(),
                requestDto.getAddress(),
                requestDto.getCategory(),
                requestDto.getRating(),
                requestDto.getReview(),
                requestDto.getLatitude(),
                requestDto.getLongitude()
        );

        Restaurant updatedRestaurant = restaurantRepository.save(restaurant);
        log.info("맛집 수정 완료 - ID: {}, 이름: {}", updatedRestaurant.getId(), updatedRestaurant.getName());
        return RestaurantResponseDto.from(updatedRestaurant);
    }

    // 맛집 삭제
    public void deleteRestaurant(Long id) {
        log.debug("맛집 삭제 시작 - ID: {}", id);

        if (!restaurantRepository.existsById(id)) {
            log.warn("삭제할 맛집을 찾을 수 없음 - ID: {}", id);
            throw new RestaurantNotFoundException(id);
        }

        restaurantRepository.deleteById(id);
        log.info("맛집 삭제 완료 - ID: {}", id);
    }

    // 카테고리별 조회
    @Transactional(readOnly = true)
    public List<RestaurantResponseDto> getRestaurantsByCategory(Category category) {
        log.debug("카테고리별 맛집 조회 - 카테고리: {}", category);
        return restaurantRepository.findByCategory(category).stream()
                .map(RestaurantResponseDto::from)
                .toList();
    }

    // 평점별 조회
    @Transactional(readOnly = true)
    public List<RestaurantResponseDto> getRestaurantsByRating(Integer rating) {
        log.debug("평점 {}점 이상 맛집 조회", rating);
        return restaurantRepository.findByRatingGreaterThanEqual(rating).stream()
                .map(RestaurantResponseDto::from)
                .toList();
    }

    // 키워드 검색
    @Transactional(readOnly = true)
    public List<RestaurantResponseDto> searchRestaurants(String keyword) {
        log.debug("키워드 검색 - 키워드: {}", keyword);
        return restaurantRepository.searchByKeyword(keyword).stream()
                .map(RestaurantResponseDto::from)
                .toList();
    }

    // 평점순 정렬
    @Transactional(readOnly = true)
    public List<RestaurantResponseDto> getRestaurantsByRatingOrder() {
        log.debug("평점순 맛집 정렬 조회");
        return restaurantRepository.findAllByOrderByRatingDesc().stream()
                .map(RestaurantResponseDto::from)
                .toList();
    }

    // 최신순 정렬
    @Transactional(readOnly = true)
    public List<RestaurantResponseDto> getRestaurantsByDateOrder() {
        log.debug("최신순 맛집 정렬 조회");
        return restaurantRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(RestaurantResponseDto::from)
                .toList();
    }

    // 근처 맛집 검색 (반경 5km 기본)
    @Transactional(readOnly = true)
    public List<RestaurantResponseDto> getNearbyRestaurants(Double latitude, Double longitude, Double radius) {
        if (radius == null) {
            radius = 5.0; // 기본 5km
        }
        log.debug("근처 맛집 검색 - 위도: {}, 경도: {}, 반경: {}km", latitude, longitude, radius);
        return restaurantRepository.findNearbyRestaurants(latitude, longitude, radius).stream()
                .map(RestaurantResponseDto::from)
                .toList();
    }

    // 고평점 맛집 조회 (새로운 기능)
    @Transactional(readOnly = true)
    public List<RestaurantResponseDto> getHighRatedRestaurants() {
        log.debug("고평점 맛집 조회 시작 (4점 이상)");

        List<RestaurantResponseDto> highRatedRestaurants = restaurantRepository.findByRatingGreaterThanEqual(4).stream()
                .map(RestaurantResponseDto::from)
                .toList();

        log.debug("고평점 맛집 {}개 조회 완료", highRatedRestaurants.size());
        return highRatedRestaurants;
    }
}
