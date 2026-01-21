package com.daengddang.daengdong_map.controller;

import com.daengddang.daengdong_map.common.ApiResponse;
import com.daengddang.daengdong_map.common.SuccessCode;
import com.daengddang.daengdong_map.dto.response.region.RegionResponse;
import com.daengddang.daengdong_map.service.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v3/regions")
@RequiredArgsConstructor
public class RegionController {

    private final RegionService regionService;

    @GetMapping
    public ApiResponse<RegionResponse> getRegion(@RequestParam Long regionId) {
        return ApiResponse.success(SuccessCode.REGION_RETRIEVED, regionService.getRegion(regionId));
    }
}
