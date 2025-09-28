package com.motmap.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidationUtils {

    private static final Pattern KOREAN_ENGLISH_NUMBER = Pattern.compile("^[가-힣a-zA-Z0-9\\s\\-_.(),&]+$");
    private static final Pattern ADDRESS_PATTERN = Pattern.compile("^[가-힣a-zA-Z0-9\\s\\-_.(),&]+$");
    private static final int MAX_NAME_LENGTH = 50;
    private static final int MAX_ADDRESS_LENGTH = 200;
    private static final int MAX_REVIEW_LENGTH = 1000;

    /**
     * 맛집 이름 유효성 검증
     */
    public static boolean isValidRestaurantName(String name) {
        if (!StringUtils.hasText(name)) {
            return false;
        }

        String trimmedName = name.trim();
        return trimmedName.length() <= MAX_NAME_LENGTH &&
               KOREAN_ENGLISH_NUMBER.matcher(trimmedName).matches();
    }

    /**
     * 주소 유효성 검증
     */
    public static boolean isValidAddress(String address) {
        if (!StringUtils.hasText(address)) {
            return false;
        }

        String trimmedAddress = address.trim();
        return trimmedAddress.length() <= MAX_ADDRESS_LENGTH &&
               ADDRESS_PATTERN.matcher(trimmedAddress).matches();
    }

    /**
     * 리뷰 내용 유효성 검증
     */
    public static boolean isValidReview(String review) {
        if (review == null) {
            return true; // 리뷰는 선택사항
        }

        String trimmedReview = review.trim();
        return trimmedReview.length() <= MAX_REVIEW_LENGTH;
    }

    /**
     * 문자열 정제 (XSS 방지)
     */
    public static String sanitizeString(String input) {
        if (!StringUtils.hasText(input)) {
            return "";
        }

        return input.trim()
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;")
                .replaceAll("'", "&#x27;")
                .replaceAll("&", "&amp;");
    }

    /**
     * 평점 유효성 검증
     */
    public static boolean isValidRating(Integer rating) {
        return rating != null && rating >= 1 && rating <= 5;
    }
}
