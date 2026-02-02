package com.daengddang.daengdong_map.util;

public final class WalkMetricsValidator {

    private static final double MAX_SPEED_KMH = 30.0;

    private WalkMetricsValidator() {
    }

    public static boolean isAbnormalSpeed(double totalDistanceKm, int durationSeconds) {
        if (totalDistanceKm <= 0) {
            return false;
        }
        if (durationSeconds <= 0) {
            return true;
        }
        double hours = durationSeconds / 3600.0;
        double speedKmh = totalDistanceKm / hours;
        return speedKmh > MAX_SPEED_KMH;
    }
}
