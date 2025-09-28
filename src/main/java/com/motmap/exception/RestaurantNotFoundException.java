package com.motmap.exception;

public class RestaurantNotFoundException extends RuntimeException {
    public RestaurantNotFoundException(Long id) {
        super("맛집을 찾을 수 없습니다. ID: " + id);
    }

    public RestaurantNotFoundException(String message) {
        super(message);
    }
}
