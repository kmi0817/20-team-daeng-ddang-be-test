package com.daengddang.daengdong_map.dto.request.user;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UserRegisterRequest {

    @NotNull
    private Long regionId;
}
