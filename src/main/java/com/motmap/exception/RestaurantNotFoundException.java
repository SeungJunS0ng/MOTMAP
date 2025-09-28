package com.motmap.exception;

public class RestaurantNotFoundException extends BusinessException {

    public RestaurantNotFoundException(Long id) {
        super(ErrorCode.RESTAURANT_NOT_FOUND, "맛집을 찾을 수 없습니다. ID: " + id);
    }

    public RestaurantNotFoundException(String message) {
        super(ErrorCode.RESTAURANT_NOT_FOUND, message);
    }
}
