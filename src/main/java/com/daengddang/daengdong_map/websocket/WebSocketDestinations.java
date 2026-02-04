package com.daengddang.daengdong_map.websocket;

public final class WebSocketDestinations {

    public static final String APP_PREFIX = "/app";
    public static final String WS_ENDPOINT = "/ws/walks";
    public static final String WALK_LOCATION = "/walks/{walkId}/location";

    public static final String TOPIC_PREFIX = "/topic";
    public static final String WALKS_PREFIX = TOPIC_PREFIX + "/walks/";
    public static final String BLOCKS_PREFIX = TOPIC_PREFIX + "/blocks/";

    private WebSocketDestinations() {
    }

    public static String walk(Long walkId) {
        return WALKS_PREFIX + walkId;
    }

    public static String blocks(String areaKey) {
        return BLOCKS_PREFIX + areaKey;
    }
}
