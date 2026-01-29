package com.daengddang.daengdong_map.controller.api;

import com.daengddang.daengdong_map.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Health", description = "Health check endpoints")
public interface HealthCheckApi {

    @Operation(summary = "Health check")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "HEALTH_CHECK_OK")
    })
    ApiResponse<Void> getHealthCheck();
}
