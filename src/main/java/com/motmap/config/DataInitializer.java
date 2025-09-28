package com.motmap.config;

import com.motmap.entity.Category;
import com.motmap.entity.Restaurant;
import com.motmap.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RestaurantRepository restaurantRepository;

    @Override
    public void run(String... args) throws Exception {
        // 초기 데이터가 이미 있는지 확인
        if (restaurantRepository.count() > 0) {
            log.info("초기 데이터가 이미 존재합니다. 스킵합니다.");
            return;
        }

        log.info("초기 맛집 데이터 생성을 시작합니다.");

        // Builder 패턴을 사용한 서울 주요 맛집 초기 데이터 생성
        Restaurant[] initialRestaurants = {
            Restaurant.builder()
                .name("명동교자")
                .address("서울특별시 중구 명동2가 25-2")
                .category(Category.KOREAN)
                .rating(4)
                .review("명동의 유명한 만두집입니다. 왕만두가 특히 맛있어요!")
                .latitude(37.563692)
                .longitude(126.982814)
                .build(),

            Restaurant.builder()
                .name("전주중앙회관")
                .address("서울특별시 중구 다동 5-1")
                .category(Category.KOREAN)
                .rating(5)
                .review("전통 한정식의 진수를 맛볼 수 있는 곳. 정갈한 상차림이 인상적입니다.")
                .latitude(37.566570)
                .longitude(126.977829)
                .build(),

            Restaurant.builder()
                .name("스타벅스 명동점")
                .address("서울특별시 중구 명동1가 59-4")
                .category(Category.CAFE)
                .rating(4)
                .review("명동 한복판에 위치한 스타벅스. 쇼핑 중 쉬어가기 좋아요.")
                .latitude(37.564718)
                .longitude(126.982573)
                .build(),

            Restaurant.builder()
                .name("교동짬뽕")
                .address("서울특별시 중구 명동1가 10-1")
                .category(Category.CHINESE)
                .rating(4)
                .review("얼큰한 짬뽕으로 유명한 중식당. 해산물이 푸짐합니다.")
                .latitude(37.564289)
                .longitude(126.982041)
                .build(),

            Restaurant.builder()
                .name("긴자료코")
                .address("서울특별시 중구 명동2가 54-15")
                .category(Category.JAPANESE)
                .rating(5)
                .review("고급 일식 레스토랑. 신선한 스시와 사시미가 일품입니다.")
                .latitude(37.563098)
                .longitude(126.983478)
                .build()
        };

        // 데이터베이스에 일괄 저장
        for (Restaurant restaurant : initialRestaurants) {
            restaurantRepository.save(restaurant);
            log.debug("초기 맛집 데이터 저장: {}", restaurant.getName());
        }

        log.info("✅ 초기 맛집 데이터 {}개가 성공적으로 생성되었습니다.", initialRestaurants.length);
    }
}
