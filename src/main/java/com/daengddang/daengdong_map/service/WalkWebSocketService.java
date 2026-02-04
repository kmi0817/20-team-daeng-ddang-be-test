package com.daengddang.daengdong_map.service;

import com.daengddang.daengdong_map.util.BlockIdUtil;
import com.daengddang.daengdong_map.dto.websocket.common.WebSocketEventType;
import com.daengddang.daengdong_map.dto.websocket.common.WebSocketMessage;
import com.daengddang.daengdong_map.dto.websocket.common.WebSocketErrorReason;
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
import com.daengddang.daengdong_map.repository.WalkBlockLogRepository;
import com.daengddang.daengdong_map.repository.WalkRepository;
import com.daengddang.daengdong_map.util.AfterCommitExecutor;
import com.daengddang.daengdong_map.util.CoordinateValidator;
import com.daengddang.daengdong_map.util.StayValidator;
import com.daengddang.daengdong_map.util.WalkPointWriter;

import java.time.LocalDateTime;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WalkWebSocketService {

    private final SimpMessagingTemplate messagingTemplate;
    private final WalkRepository walkRepository;
    private final BlockRepository blockRepository;
    private final BlockOwnershipRepository blockOwnershipRepository;
    private final WalkBlockLogRepository walkBlockLogRepository;
    private final WalkPointWriter walkPointWriter;
    private final StayValidator stayValidator;
    private final BlockSyncService blockSyncService;
    private final AfterCommitExecutor afterCommitExecutor;

    @Transactional
    public void handleLocationUpdate(Long walkId, LocationUpdatePayload payload, Principal principal) {
        LocalDateTime timestamp = LocalDateTime.now();

        Walk walk = walkRepository.findById(walkId)
                .orElse(null);

        if (walk == null || walk.getStatus() != WalkStatus.IN_PROGRESS) {
            sendError(walkId, WebSocketErrorReason.INVALID_WALK_SESSION.getMessage());
            return;
        }

        if (!CoordinateValidator.isValidLatLng(payload.getLat(), payload.getLng())) {
            sendError(walkId, WebSocketErrorReason.INVALID_LOCATION.getMessage());
            return;
        }

        walkPointWriter.save(walk, payload.getLat(), payload.getLng(), timestamp);

        int blockX = BlockIdUtil.toBlockX(payload.getLat());
        int blockY = BlockIdUtil.toBlockY(payload.getLng());
        String blockId = BlockIdUtil.toBlockId(blockX, blockY);
        String areaKey = blockSyncService.toAreaKey(blockX, blockY);
        boolean staySatisfied = stayValidator.isStaySatisfied(walkId, blockId, timestamp);

        if (!staySatisfied) {
            BlockOccupyFailedPayload failPayload =
                    BlockOccupyFailedPayload.from(BlockOccupyFailReason.INSUFFICIENT_STAY_TIME);

            WebSocketMessage<BlockOccupyFailedPayload> message =
                    new WebSocketMessage<>(WebSocketEventType.BLOCK_OCCUPY_FAILED, failPayload,
                            WebSocketEventType.BLOCK_OCCUPY_FAILED.getMessage());

            messagingTemplate.convertAndSend("/topic/walks/" + walkId, message);
            blockSyncService.syncBlocksOnAreaChange(walkId, blockX, blockY, areaKey, timestamp);
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
            walkBlockLogRepository.insertIfNotExists(walk.getId(), block.getId(), dog.getId(), timestamp);
            sendBlockOccupied(walkId, blockId, dog, timestamp, areaKey);
            blockSyncService.syncBlocks(walkId, blockX, blockY, areaKey, timestamp);
            return;
        }

        if (ownership.getDog().getId().equals(dog.getId())) {
            ownership.updateLastPassedAt(timestamp);
            blockSyncService.syncBlocksOnAreaChange(walkId, blockX, blockY, areaKey, timestamp);
            return;
        }

        Long previousDogId = ownership.getDog().getId();
        ownership.updateOwner(dog, timestamp);
        walkBlockLogRepository.insertIfNotExists(walk.getId(), block.getId(), dog.getId(), timestamp);
        sendBlockTaken(walkId, blockId, previousDogId, dog.getId(), timestamp, areaKey);
        blockSyncService.syncBlocks(walkId, blockX, blockY, areaKey, timestamp);
    }

    public void sendError(Long walkId, String message) {
        messagingTemplate.convertAndSend("/topic/walks/" + walkId, WebSocketMessage.error(message));
    }

    private void sendBlockOccupied(Long walkId, String blockId, Dog dog, LocalDateTime occupiedAt, String areaKey) {
        BlockOccupiedPayload payload = BlockOccupiedPayload.from(blockId, dog.getId(), dog.getName(), occupiedAt);
        WebSocketMessage<BlockOccupiedPayload> message =
                new WebSocketMessage<>(WebSocketEventType.BLOCK_OCCUPIED, payload,
                        WebSocketEventType.BLOCK_OCCUPIED.getMessage());
        afterCommitExecutor.sendAfterCommit(() -> {
            messagingTemplate.convertAndSend("/topic/walks/" + walkId, message);
            messagingTemplate.convertAndSend("/topic/blocks/" + areaKey, message);
        });
    }

    private void sendBlockTaken(Long walkId, String blockId, Long previousDogId, Long newDogId,
                                LocalDateTime takenAt, String areaKey) {
        BlockTakenPayload payload = BlockTakenPayload.from(blockId, previousDogId, newDogId, takenAt);
        WebSocketMessage<BlockTakenPayload> message =
                new WebSocketMessage<>(WebSocketEventType.BLOCK_TAKEN, payload,
                        WebSocketEventType.BLOCK_TAKEN.getMessage());
        afterCommitExecutor.sendAfterCommit(() -> {
            messagingTemplate.convertAndSend("/topic/walks/" + walkId, message);
            messagingTemplate.convertAndSend("/topic/blocks/" + areaKey, message);
        });
    }

}
