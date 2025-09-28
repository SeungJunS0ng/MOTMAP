package com.motmap.exception;

public class DuplicateRestaurantException extends BusinessException {

    public DuplicateRestaurantException(String name, String address) {
        super(ErrorCode.DUPLICATE_RESTAURANT, "이미 등록된 맛집입니다. 이름: " + name + ", 주소: " + address);
    }

    public DuplicateRestaurantException(String message) {
        super(ErrorCode.DUPLICATE_RESTAURANT, message);
    }
}
