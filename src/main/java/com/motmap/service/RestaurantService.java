package com.motmap.service;

import com.motmap.dto.RestaurantRequestDto;
import com.motmap.dto.RestaurantResponseDto;
import com.motmap.dto.RestaurantPageResponseDto;
import com.motmap.dto.RestaurantStatsDto;
import com.motmap.entity.Category;
import com.motmap.entity.Restaurant;
import com.motmap.entity.User;
import com.motmap.exception.RestaurantNotFoundException;
import com.motmap.exception.DuplicateRestaurantException;
import com.motmap.exception.InvalidLocationException;
import com.motmap.exception.BusinessException;
import com.motmap.exception.ErrorCode;
import com.motmap.exception.UserNotFoundException;
import com.motmap.repository.RestaurantRepository;
import com.motmap.repository.UserRepository;
import com.motmap.util.LocationUtils;
import com.motmap.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.motmap.exception.ErrorCode.INVALID_REQUEST;
import static com.motmap.exception.ErrorCode.INVALID_RATING;
import static com.motmap.exception.ErrorCode.UNAUTHORIZED;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    // 모든 맛집 조회 (캐싱 적용)
    @Cacheable(value = "restaurants", key = "'all'")
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

    // 맛집 추가 (캐시 무효화)
    @CacheEvict(value = {"restaurants", "categoryRestaurants", "highRatedRestaurants", "restaurantStats"}, allEntries = true)
    public RestaurantResponseDto addRestaurant(RestaurantRequestDto requestDto) {
        log.debug("맛집 추가 시작 - 이름: {}, 주소: {}", requestDto.getName(), requestDto.getAddress());

        // 추가 입력값 검증
        validateRestaurantData(requestDto);

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

    // 입력값 검증 헬퍼 메소드
    private void validateRestaurantData(RestaurantRequestDto requestDto) {
        if (!ValidationUtils.isValidRestaurantName(requestDto.getName())) {
            throw new BusinessException(INVALID_REQUEST, "올바르지 않은 맛집 이름입니다");
        }

        if (!ValidationUtils.isValidAddress(requestDto.getAddress())) {
            throw new BusinessException(INVALID_REQUEST, "올바르지 않은 주소입니다");
        }

        if (!ValidationUtils.isValidRating(requestDto.getRating())) {
            throw new BusinessException(INVALID_RATING, "평점은 1-5점 사이여야 합니다");
        }

        if (!ValidationUtils.isValidReview(requestDto.getReview())) {
            throw new BusinessException(INVALID_REQUEST, "리뷰가 너무 깁니다");
        }
    }

    // Restaurant 엔티티 생성 헬퍼 메소드
    private Restaurant createRestaurantEntity(RestaurantRequestDto requestDto) {
        // 현재 로그인한 사용자 가져오기
        User currentUser = getCurrentUser();

        return Restaurant.builder()
                .name(requestDto.getName())
                .address(requestDto.getAddress())
                .category(requestDto.getCategory())
                .rating(requestDto.getRating())
                .review(requestDto.getReview())
                .imageUrl(requestDto.getImageUrl())
                .latitude(requestDto.getLatitude())
                .longitude(requestDto.getLongitude())
                .user(currentUser)
                .build();
    }

    /**
     * 현재 로그인한 사용자 조회
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("인증되지 않은 요청");
            throw new BusinessException(ErrorCode.AUTHENTICATION_FAILED, "로그인이 필요합니다");
        }

        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + username));
    }

    // 맛집 수정 (캐시 무효화) - 본인 것만 수정 가능
    @CacheEvict(value = {"restaurants", "categoryRestaurants", "highRatedRestaurants", "restaurantStats"}, allEntries = true)
    public RestaurantResponseDto updateRestaurant(Long id, RestaurantRequestDto requestDto) {
        log.debug("맛집 수정 시작 - ID: {}, 이름: {}", id, requestDto.getName());

        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException(id));

        // 현재 로그인한 사용자 확인
        User currentUser = getCurrentUser();

        // 소유자 확인 (본인 것만 수정 가능)
        if (!restaurant.isOwnedBy(currentUser)) {
            log.warn("권한 없는 수정 시도 - 사용자: {}, 맛집 ID: {}", currentUser.getUsername(), id);
            throw new BusinessException(UNAUTHORIZED, "본인이 등록한 맛집만 수정할 수 있습니다");
        }

        // 엔티티의 비즈니스 메소드 활용
        restaurant.updateRestaurantInfo(
                requestDto.getName(),
                requestDto.getAddress(),
                requestDto.getCategory(),
                requestDto.getRating(),
                requestDto.getReview(),
                requestDto.getLatitude(),
                requestDto.getLongitude(),
                requestDto.getImageUrl()
        );

        Restaurant updatedRestaurant = restaurantRepository.save(restaurant);
        log.info("맛집 수정 완료 - ID: {}, 이름: {}", updatedRestaurant.getId(), updatedRestaurant.getName());
        return RestaurantResponseDto.from(updatedRestaurant);
    }

    // 맛집 삭제 (캐시 무효화) - 본인 것만 삭제 가능
    @CacheEvict(value = {"restaurants", "categoryRestaurants", "highRatedRestaurants", "restaurantStats"}, allEntries = true)
    public void deleteRestaurant(Long id) {
        log.debug("맛집 삭제 시작 - ID: {}", id);

        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException(id));

        // 현재 로그인한 사용자 확인
        User currentUser = getCurrentUser();

        // 소유자 확인 (본인 것만 삭제 가능)
        if (!restaurant.isOwnedBy(currentUser)) {
            log.warn("권한 없는 삭제 시도 - 사용자: {}, 맛집 ID: {}", currentUser.getUsername(), id);
            throw new BusinessException(UNAUTHORIZED, "본인이 등록한 맛집만 삭제할 수 있습니다");
        }

        if (!restaurantRepository.existsById(id)) {
            log.warn("삭제할 맛집을 찾을 수 없음 - ID: {}", id);
            throw new RestaurantNotFoundException(id);
        }

        restaurantRepository.deleteById(id);
        log.info("맛집 삭제 완료 - ID: {}", id);
    }

    // 카테고리별 조회 (캐싱 적용)
    @Cacheable(value = "categoryRestaurants", key = "#category")
    @Transactional(readOnly = true)
    public List<RestaurantResponseDto> getRestaurantsByCategory(Category category) {
        log.debug("카테고리별 맛집 조회 - 카테고리: {}", category);
        return restaurantRepository.findByCategory(category).stream()
                .map(RestaurantResponseDto::from)
                .toList();
    }

    // 유저별 맛집 목록 조회
    @Transactional(readOnly = true)
    public List<RestaurantResponseDto> getRestaurantsByUsername(String username) {
        log.debug("유저별 맛집 목록 조회 - 유저: {}", username);
        return restaurantRepository.findByUserUsername(username).stream()
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

    // 고평점 맛집 조회 (캐싱 적용)
    @Cacheable(value = "highRatedRestaurants", key = "'highRated'")
    @Transactional(readOnly = true)
    public List<RestaurantResponseDto> getHighRatedRestaurants() {
        log.debug("고평점 맛집 조회 시작 (4점 이상)");

        List<RestaurantResponseDto> highRatedRestaurants = restaurantRepository.findByRatingGreaterThanEqual(4).stream()
                .map(RestaurantResponseDto::from)
                .toList();

        log.debug("고평점 맛집 {}개 조회 완료", highRatedRestaurants.size());
        return highRatedRestaurants;
    }

    // 페이징된 맛집 조회 (새로운 기능)
    @Transactional(readOnly = true)
    public RestaurantPageResponseDto getRestaurantsWithPaging(int page, int size, String sortBy, String sortDir) {
        log.debug("페이징된 맛집 조회 - 페이지: {}, 크기: {}, 정렬: {} {}", page, size, sortBy, sortDir);

        // 정렬 방향 설정
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);

        // 페이지 요청 생성
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Restaurant> restaurantPage = restaurantRepository.findAll(pageable);

        // DTO 변환
        List<RestaurantResponseDto> restaurantDtos = restaurantPage.getContent().stream()
                .map(RestaurantResponseDto::from)
                .toList();

        RestaurantPageResponseDto response = RestaurantPageResponseDto.builder()
                .restaurants(restaurantDtos)
                .pageNumber(restaurantPage.getNumber())
                .pageSize(restaurantPage.getSize())
                .totalElements(restaurantPage.getTotalElements())
                .totalPages(restaurantPage.getTotalPages())
                .first(restaurantPage.isFirst())
                .last(restaurantPage.isLast())
                .build();

        log.debug("페이징 조회 완료 - 총 {}개 중 {}번째 페이지", restaurantPage.getTotalElements(), page + 1);
        return response;
    }

    // 맛집 통계 조회 (캐싱 적용)
    @Cacheable(value = "restaurantStats", key = "'stats'")
    @Transactional(readOnly = true)
    public RestaurantStatsDto getRestaurantStatistics() {
        log.debug("맛집 통계 조회 시작");

        // 기본 통계
        long totalRestaurants = restaurantRepository.count();
        Double averageRating = restaurantRepository.findAverageRating();
        long highRatedCount = restaurantRepository.findByRatingGreaterThanEqual(4).size();

        // 최근 30일 등록 맛집 수
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        Long recentCount = restaurantRepository.countRestaurantsCreatedAfter(thirtyDaysAgo);

        // 카테고리별 통계
        Map<String, Long> categoryStats = new HashMap<>();
        List<Object[]> categoryData = restaurantRepository.findCategoryStatistics();
        for (Object[] row : categoryData) {
            Category category = (Category) row[0];
            Long count = (Long) row[1];
            categoryStats.put(category.getDisplayName(), count);
        }

        // 평점별 통계
        Map<Integer, Long> ratingStats = new HashMap<>();
        List<Object[]> ratingData = restaurantRepository.findRatingStatistics();
        for (Object[] row : ratingData) {
            Integer rating = (Integer) row[0];
            Long count = (Long) row[1];
            ratingStats.put(rating, count);
        }

        RestaurantStatsDto stats = RestaurantStatsDto.builder()
                .totalRestaurants(totalRestaurants)
                .categoryStats(categoryStats)
                .ratingStats(ratingStats)
                .averageRating(averageRating != null ? Math.round(averageRating * 100.0) / 100.0 : 0.0)
                .highRatedCount(highRatedCount)
                .recentRestaurantsCount(recentCount != null ? recentCount : 0L)
                .build();

        log.debug("맛집 통계 조회 완료 - 총 {}개 맛집, 평균 평점 {}", totalRestaurants, averageRating);
        return stats;
    }
}
