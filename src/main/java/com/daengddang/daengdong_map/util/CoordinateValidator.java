package com.daengddang.daengdong_map.util;

public final class CoordinateValidator {

    public static final int MIN_RADIUS_METERS = 0;
    public static final int MAX_RADIUS_METERS = 1500;

    private CoordinateValidator() {
    }

    public static boolean isValidLatLng(Double lat, Double lng) {
        if (lat == null || lng == null) {
            return false;
        }
        return isValidLatLng(lat.doubleValue(), lng.doubleValue());
    }

    public static boolean isValidLatLng(double lat, double lng) {
        if (!Double.isFinite(lat) || !Double.isFinite(lng)) {
            return false;
        }
        return lat >= -90.0 && lat <= 90.0 && lng >= -180.0 && lng <= 180.0;
    }

    public static boolean isValidRadius(Integer radiusMeters) {
        return radiusMeters != null
                && radiusMeters >= MIN_RADIUS_METERS
                && radiusMeters <= MAX_RADIUS_METERS;
    }
}
