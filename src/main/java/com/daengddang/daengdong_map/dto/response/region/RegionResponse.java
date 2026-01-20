package com.daengddang.daengdong_map.dto.response.region;

import com.daengddang.daengdong_map.domain.region.Region;
import lombok.Builder;
import lombok.Getter;

@Getter
public class RegionResponse {

    private Long regionId;
    private String name;

    @Builder
    public RegionResponse(Long regionId, String name) {
        this.regionId = regionId;
        this.name = name;
    }

    public static RegionResponse from(Region region) {
        return RegionResponse.builder()
                .regionId(region.getId())
                .name(region.getName())
                .build();
    }
}
