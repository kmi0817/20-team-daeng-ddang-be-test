package com.daengddang.daengdong_map.controller;

import com.daengddang.daengdong_map.common.ApiResponse;
import com.daengddang.daengdong_map.common.SuccessCode;
import com.daengddang.daengdong_map.controller.api.DogApi;
import com.daengddang.daengdong_map.dto.response.dog.BreedListResponse;
import com.daengddang.daengdong_map.service.BreedService;
import com.daengddang.daengdong_map.service.DogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v3/dogs")
@RequiredArgsConstructor
public class DogController implements DogApi {

    private final BreedService breedService;
    private final DogService dogService;

    @GetMapping("/breeds")
    @Override
    public ApiResponse<BreedListResponse> getBreeds(
            @RequestParam(name = "keyword", required = false) String keyword
    ) {
        return ApiResponse.success(
                SuccessCode.DOG_BREED_LIST_RETRIEVED,
                breedService.getBreeds(keyword)
        );
    }

}
