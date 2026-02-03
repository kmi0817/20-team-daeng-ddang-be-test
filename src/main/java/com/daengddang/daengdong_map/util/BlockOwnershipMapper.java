package com.daengddang.daengdong_map.util;

import com.daengddang.daengdong_map.common.BlockIdUtil;
import com.daengddang.daengdong_map.domain.block.Block;
import com.daengddang.daengdong_map.domain.block.BlockOwnership;
import java.time.LocalDateTime;

public final class BlockOwnershipMapper {

    private BlockOwnershipMapper() {
    }

    public static String toBlockId(BlockOwnership ownership) {
        Block block = ownership.getBlock();
        return BlockIdUtil.toBlockId(block.getX(), block.getY());
    }

    public static Long toOwnerDogId(BlockOwnership ownership) {
        return ownership.getDog().getId();
    }

    public static LocalDateTime toAcquiredAt(BlockOwnership ownership) {
        return ownership.getAcquiredAt();
    }
}
