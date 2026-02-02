package com.daengddang.daengdong_map.dto.request.user;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UserTermAgreeDto {

    @NotNull
    private Boolean isAgreed;

}
