package com.daengddang.daengdong_map.service;

import com.daengddang.daengdong_map.common.BlockIdUtil;
import com.daengddang.daengdong_map.dto.websocket.common.WebSocketEventType;
import com.daengddang.daengdong_map.dto.websocket.common.WebSocketMessage;
import com.daengddang.daengdong_map.dto.websocket.inbound.LocationUpdatePayload;
import com.daengddang.daengdong_map.dto.websocket.outbound.BlockOccupiedPayload;
import com.daengddang.daengdong_map.dto.websocket.outbound.BlockOccupyFailReason;
import com.daengddang.daengdong_map.dto.websocket.outbound.BlockOccupyFailedPayload;
import com.daengddang.daengdong_map.dto.websocket.outbound.BlockTakenPayload;
import com.daengddang.daengdong_map.domain.block.Block;
import com.daengddang.daengdong_map.domain.block.BlockOwnership;
import com.daengddang.daengdong_map.domain.dog.Dog;
import com.daengddang.daengdong_map.domain.walk.Walk;
import com.daengddang.daengdong_map.domain.walk.WalkStatus;
import com.daengddang.daengdong_map.repository.BlockOwnershipRepository;
import com.daengddang.daengdong_map.repository.BlockRepository;
import com.daengddang.daengdong_map.repository.WalkRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.security.Principal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WalkRealtimeService {

    private static final long STAY_SECONDS = 5;

    private final SimpMessagingTemplate messagingTemplate;
    private final WalkRepository walkRepository;
    private final BlockRepository blockRepository;
    private final BlockOwnershipRepository blockOwnershipRepository;
    private final ConcurrentMap<Long, StayState> stayStates = new ConcurrentHashMap<>();

    @Transactional
    public void handleLocationUpdate(Long walkId, LocationUpdatePayload payload, Principal principal) {
        // TODO: walk 상태/권한 검증 + 점유/탈취 로직
        // 클라이언트 시간은 신뢰하지 않고 서버 시간을 사용한다.
        LocalDateTime timestamp = LocalDateTime.now();

        int blockX = BlockIdUtil.toBlockX(payload.getLat());
        int blockY = BlockIdUtil.toBlockY(payload.getLng());
        String blockId = BlockIdUtil.toBlockId(blockX, blockY);
        boolean staySatisfied = updateStayState(walkId, blockId, timestamp);

        if (!staySatisfied) {
            BlockOccupyFailedPayload failPayload =
                    BlockOccupyFailedPayload.of(BlockOccupyFailReason.INSUFFICIENT_STAY_TIME);
            WebSocketMessage<BlockOccupyFailedPayload> message =
                    new WebSocketMessage<>(WebSocketEventType.BLOCK_OCCUPY_FAILED, failPayload, "블록 점유에 실패했습니다.");
            messagingTemplate.convertAndSend("/topic/walks/" + walkId, message);
            return;
        }

        Walk walk = walkRepository.findById(walkId)
                .orElse(null);
        if (walk == null || walk.getStatus() != WalkStatus.IN_PROGRESS) {
            sendError(walkId, "유효하지 않은 산책 세션입니다.");
            return;
        }

        Dog dog = walk.getDog();
        blockRepository.insertIfNotExists(blockX, blockY);
        Block block = blockRepository.findByXAndY(blockX, blockY)
                .orElseThrow();

        BlockOwnership ownership = blockOwnershipRepository.findById(block.getId()).orElse(null);
        if (ownership == null) {
            BlockOwnership newOwnership = BlockOwnership.builder()
                    .block(block)
                    .dog(dog)
                    .acquiredAt(timestamp)
                    .lastPassedAt(timestamp)
                    .build();
            blockOwnershipRepository.save(newOwnership);
            sendBlockOccupied(walkId, blockId, dog, timestamp);
            return;
        }

        if (ownership.getDog().getId().equals(dog.getId())) {
            ownership.updateLastPassedAt(timestamp);
            return;
        }

        Long previousDogId = ownership.getDog().getId();
        ownership.updateOwner(dog, timestamp);
        sendBlockTaken(walkId, blockId, previousDogId, dog.getId(), timestamp);
    }

    public void sendError(Long walkId, String message) {
        messagingTemplate.convertAndSend("/topic/walks/" + walkId, WebSocketMessage.error(message));
    }

    private boolean updateStayState(Long walkId, String blockId, LocalDateTime timestamp) {
        StayState state = stayStates.get(walkId);
        if (state == null || !state.blockId.equals(blockId)) {
            stayStates.put(walkId, new StayState(blockId, timestamp));
            return false;
        }

        state.lastSeenAt = timestamp;
        Duration stayed = Duration.between(state.enteredAt, timestamp);
        return stayed.getSeconds() >= STAY_SECONDS;
    }

    // blockId/좌표 변환은 BlockIdUtil에서 공통 처리한다.

    private void sendBlockOccupied(Long walkId, String blockId, Dog dog, LocalDateTime occupiedAt) {
        BlockOccupiedPayload payload = BlockOccupiedPayload.of(blockId, dog.getId(), dog.getName(), occupiedAt);
        WebSocketMessage<BlockOccupiedPayload> message =
                new WebSocketMessage<>(WebSocketEventType.BLOCK_OCCUPIED, payload, null);
        messagingTemplate.convertAndSend("/topic/walks/" + walkId, message);
    }

    private void sendBlockTaken(Long walkId, String blockId, Long previousDogId, Long newDogId, LocalDateTime takenAt) {
        BlockTakenPayload payload = BlockTakenPayload.of(blockId, previousDogId, newDogId, takenAt);
        WebSocketMessage<BlockTakenPayload> message =
                new WebSocketMessage<>(WebSocketEventType.BLOCK_TAKEN, payload, null);
        messagingTemplate.convertAndSend("/topic/walks/" + walkId, message);
    }

    private static class StayState {
        private final String blockId;
        private final LocalDateTime enteredAt;
        private LocalDateTime lastSeenAt;

        private StayState(String blockId, LocalDateTime enteredAt) {
            this.blockId = blockId;
            this.enteredAt = enteredAt;
            this.lastSeenAt = enteredAt;
        }
    }
}
