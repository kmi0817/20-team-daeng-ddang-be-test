package com.daengddang.daengdong_map.controller.user;

import com.daengddang.daengdong_map.common.ApiResponse;
import com.daengddang.daengdong_map.common.ErrorCode;
import com.daengddang.daengdong_map.common.exception.BaseException;
import com.daengddang.daengdong_map.dto.request.user.UserRegisterRequest;
import com.daengddang.daengdong_map.dto.response.user.RegionListResponse;
import com.daengddang.daengdong_map.dto.response.user.UserRegisterResponse;
import com.daengddang.daengdong_map.security.AuthUser;
import com.daengddang.daengdong_map.service.user.RegionService;
import com.daengddang.daengdong_map.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v3/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RegionService regionService;

    @PostMapping
    public ApiResponse<UserRegisterResponse> registerUserInfo(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody UserRegisterRequest request
    ) {
        if (authUser == null) {
            throw new BaseException(ErrorCode.UNAUTHORIZED);
        }
        return ApiResponse.success(
                "사용자 정보 등록에 성공했습니다.",
                userService.registerUserInfo(authUser.getUserId(), request)
        );
    }

    @GetMapping("/regions")
    public ApiResponse<RegionListResponse> getRegions(
            @RequestParam(name = "parentId", required = false) Long parentId
    ) {
        return ApiResponse.success(
                "지역 목록 조회에 성공했습니다.",
                regionService.getRegions(parentId)
        );
    }
}

