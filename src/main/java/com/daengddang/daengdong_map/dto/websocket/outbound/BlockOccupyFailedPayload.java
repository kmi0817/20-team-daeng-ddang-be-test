package com.daengddang.daengdong_map.dto.websocket.outbound;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class BlockOccupyFailedPayload {

    private final BlockOccupyFailReason reason;

    @JsonCreator
    public BlockOccupyFailedPayload(
            @JsonProperty("reason") BlockOccupyFailReason reason
    ) {
        this.reason = reason;
    }

    public static BlockOccupyFailedPayload of(BlockOccupyFailReason reason) {
        return new BlockOccupyFailedPayload(reason);
    }
}
