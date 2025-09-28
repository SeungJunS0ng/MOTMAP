package com.motmap.config;

import com.motmap.entity.Category;
import com.motmap.entity.Restaurant;
import com.motmap.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RestaurantRepository restaurantRepository;

    @Autowired
    public DataInitializer(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // 초기 데이터가 이미 있는지 확인
        if (restaurantRepository.count() > 0) {
            return;
        }

        // 서울 주요 맛집 초기 데이터 생성
        Restaurant[] initialRestaurants = {
            new Restaurant(
                "명동교자",
                "서울특별시 중구 명동2가 25-2",
                Category.KOREAN,
                4,
                "명동의 유명한 만두집입니다. 왕만두가 특히 맛있어요!",
                37.563692,
                126.982814
            ),
            new Restaurant(
                "전주중앙회관",
                "서울특별시 중구 다동 5-1",
                Category.KOREAN,
                5,
                "전통 한정식의 진수를 맛볼 수 있는 곳. 정갈한 상차림이 인상적입니다.",
                37.566570,
                126.977829
            ),
            new Restaurant(
                "스타벅스 명동점",
                "서울특별시 중구 명동1가 59-4",
                Category.CAFE,
                4,
                "명동 한복판에 위치한 스타벅스. 쇼핑 중 쉬어가기 좋아요.",
                37.564718,
                126.982573
            ),
            new Restaurant(
                "교동짬뽕",
                "서울특별시 중구 명동1가 10-1",
                Category.CHINESE,
                4,
                "얼큰한 짬뽕으로 유명한 중식당. 해산물이 푸짐합니다.",
                37.564289,
                126.982041
            ),
            new Restaurant(
                "긴자료코",
                "서울특별시 중구 명동2가 54-15",
                Category.JAPANESE,
                5,
                "고급 일식 레스토랑. 신선한 스시와 사시미가 일품입니다.",
                37.563098,
                126.983478
            )
        };

        // 데이터베이스에 저장
        for (Restaurant restaurant : initialRestaurants) {
            restaurantRepository.save(restaurant);
        }

        System.out.println("✅ 초기 맛집 데이터 " + initialRestaurants.length + "개가 생성되었습니다.");
    }
}
