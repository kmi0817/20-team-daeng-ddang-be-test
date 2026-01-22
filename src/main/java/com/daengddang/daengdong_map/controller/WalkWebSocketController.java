package com.daengddang.daengdong_map.controller;

import com.daengddang.daengdong_map.dto.websocket.common.WebSocketEventType;
import com.daengddang.daengdong_map.dto.websocket.common.WebSocketMessage;
import com.daengddang.daengdong_map.dto.websocket.inbound.LocationUpdatePayload;
import com.daengddang.daengdong_map.service.WalkRealtimeService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WalkWebSocketController {

    private final WalkRealtimeService walkRealtimeService;

    @MessageMapping("/walks/{walkId}/location")
    public void handleLocationUpdate(
            @DestinationVariable Long walkId,
            WebSocketMessage<LocationUpdatePayload> message,
            Principal principal
    ) {
        if (message == null || message.getData() == null) {
            walkRealtimeService.sendError(walkId, "유효하지 않은 위치 정보입니다.");
            return;
        }
        if (message.getType() != null && message.getType() != WebSocketEventType.LOCATION_UPDATE) {
            walkRealtimeService.sendError(walkId, "유효하지 않은 이벤트 타입입니다.");
            return;
        }

        walkRealtimeService.handleLocationUpdate(walkId, message.getData(), principal);
    }

}
