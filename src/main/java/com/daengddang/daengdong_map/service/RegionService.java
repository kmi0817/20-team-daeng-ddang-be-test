package com.daengddang.daengdong_map.service;

import com.daengddang.daengdong_map.common.ErrorCode;
import com.daengddang.daengdong_map.common.exception.BaseException;
import com.daengddang.daengdong_map.domain.region.Region;
import com.daengddang.daengdong_map.domain.region.RegionStatus;
import com.daengddang.daengdong_map.dto.response.region.RegionResponse;
import com.daengddang.daengdong_map.dto.response.user.RegionListResponse;
import com.daengddang.daengdong_map.repository.RegionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegionService {

    private final RegionRepository regionRepository;

    public RegionListResponse getRegions(Long parentId) {
        List<Region> regions;
        if (parentId == null) {
            regions = regionRepository.findByParentIsNullAndStatusOrderByNameAsc(RegionStatus.ACTIVE);
        } else {
            Region parent = regionRepository
                    .findByIdAndStatus(parentId, RegionStatus.ACTIVE)
                    .orElseThrow(() -> new BaseException(ErrorCode.REGION_NOT_FOUND));
            regions = regionRepository.findByParentAndStatusOrderByNameAsc(parent, RegionStatus.ACTIVE);
            if (regions.isEmpty()) {
                throw new BaseException(ErrorCode.REGION_NOT_FOUND);
            }
        }

        return RegionListResponse.from(regions);
    }

    public RegionResponse getRegion(Long regionId) {
        Region region = regionRepository.findById(regionId).orElseThrow(
                () -> new BaseException(ErrorCode.REGION_NOT_FOUND)
        );

        return RegionResponse.from(region);
    }
}
