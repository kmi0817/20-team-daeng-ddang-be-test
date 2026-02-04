package com.daengddang.daengdong_map.util;

public record BlockOccupancyResult(Type type, Long previousDogId) {

    public enum Type {
        OCCUPIED,
        TAKEN,
        ALREADY_OWNED
    }

    public static BlockOccupancyResult occupied() {
        return new BlockOccupancyResult(Type.OCCUPIED, null);
    }

    public static BlockOccupancyResult taken(Long previousDogId) {
        return new BlockOccupancyResult(Type.TAKEN, previousDogId);
    }

    public static BlockOccupancyResult alreadyOwned() {
        return new BlockOccupancyResult(Type.ALREADY_OWNED, null);
    }
}
