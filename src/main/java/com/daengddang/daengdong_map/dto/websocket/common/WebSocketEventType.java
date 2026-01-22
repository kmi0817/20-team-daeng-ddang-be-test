package com.daengddang.daengdong_map.dto.websocket.common;

public enum WebSocketEventType {
    CONNECTED,
    LOCATION_UPDATE,
    BLOCK_OCCUPIED,
    BLOCK_OCCUPY_FAILED,
    BLOCK_TAKEN,
    BLOCKS_SYNC,
    WALK_ENDED,
    ERROR,
    TEST_BROADCAST
}
