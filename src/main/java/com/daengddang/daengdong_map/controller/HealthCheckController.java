package com.daengddang.daengdong_map.controller;

import com.daengddang.daengdong_map.common.ApiResponse;
import com.daengddang.daengdong_map.common.SuccessCode;
import com.daengddang.daengdong_map.controller.api.HealthCheckApi;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v3")
public class HealthCheckController implements HealthCheckApi {

    @GetMapping("/health")
    @Override
    public ApiResponse<Void> getHealthCheck() {
        return ApiResponse.success(SuccessCode.HEALTH_CHECK_OK);
    }
}
