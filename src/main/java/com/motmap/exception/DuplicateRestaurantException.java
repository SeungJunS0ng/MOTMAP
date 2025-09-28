package com.motmap.exception;

public class DuplicateRestaurantException extends RuntimeException {
    public DuplicateRestaurantException(String name, String address) {
        super("이미 등록된 맛집입니다. 이름: " + name + ", 주소: " + address);
    }

    public DuplicateRestaurantException(String message) {
        super(message);
    }
}
