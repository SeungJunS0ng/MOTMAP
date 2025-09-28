package com.motmap.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
            "restaurants",           // 전체 맛집 목록
            "restaurantStats",       // 통계 정보
            "categoryRestaurants",   // 카테고리별 맛집
            "highRatedRestaurants"   // 고평점 맛집
        );
    }
}
