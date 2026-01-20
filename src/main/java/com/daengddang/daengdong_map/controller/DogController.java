package com.daengddang.daengdong_map.controller;

import com.daengddang.daengdong_map.common.ApiResponse;
import com.daengddang.daengdong_map.common.ErrorCode;
import com.daengddang.daengdong_map.common.exception.BaseException;
import com.daengddang.daengdong_map.dto.request.dog.DogRegisterRequest;
import com.daengddang.daengdong_map.dto.response.dog.BreedListResponse;
import com.daengddang.daengdong_map.dto.response.dog.DogRegisterResponse;
import com.daengddang.daengdong_map.security.AuthUser;
import com.daengddang.daengdong_map.service.BreedService;
import com.daengddang.daengdong_map.service.DogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v3/dogs")
@RequiredArgsConstructor
public class DogController {

    private final BreedService breedService;
    private final DogService dogService;

    @GetMapping("/breeds")
    public ApiResponse<BreedListResponse> getBreeds(
            @RequestParam(name = "keyword", required = false) String keyword
    ) {
        return ApiResponse.success(
                "강아지 종 목록 조회에 성공했습니다.",
                breedService.getBreeds(keyword)
        );
    }

    @PostMapping
    public ApiResponse<DogRegisterResponse> registerDog(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody DogRegisterRequest request
    ) {
        if (authUser == null) {
            throw new BaseException(ErrorCode.UNAUTHORIZED);
        }
        return ApiResponse.success(
                "강아지 정보 등록에 성공했습니다.",
                dogService.registerDog(authUser.getUserId(), request)
        );
    }
}
