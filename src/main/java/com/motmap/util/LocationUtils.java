package com.motmap.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LocationUtils {

    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * 두 지점 간의 거리를 계산합니다 (Haversine 공식)
     * @param lat1 첫 번째 지점의 위도
     * @param lon1 첫 번째 지점의 경도
     * @param lat2 두 번째 지점의 위도
     * @param lon2 두 번째 지점의 경도
     * @return 두 지점 간의 거리 (km)
     */
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    /**
     * 위도가 유효한 범위인지 검증합니다
     * @param latitude 검증할 위도
     * @return 유효하면 true, 그렇지 않으면 false
     */
    public static boolean isValidLatitude(Double latitude) {
        return latitude != null && latitude >= -90.0 && latitude <= 90.0;
    }

    /**
     * 경도가 유효한 범위인지 검증합니다
     * @param longitude 검증할 경도
     * @return 유효하면 true, 그렇지 않으면 false
     */
    public static boolean isValidLongitude(Double longitude) {
        return longitude != null && longitude >= -180.0 && longitude <= 180.0;
    }

    /**
     * 위도와 경도가 모두 유효한지 검증합니다
     * @param latitude 위도
     * @param longitude 경도
     * @return 둘 다 유효하면 true, 그렇지 않으면 false
     */
    public static boolean isValidLocation(Double latitude, Double longitude) {
        return isValidLatitude(latitude) && isValidLongitude(longitude);
    }
}
