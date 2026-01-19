package com.daengddang.daengdong_map.dto.response.user;

import lombok.Getter;

@Getter
public class UserResponse {

    private final Long userId;

    private UserResponse(Long userId) {
        this.userId = userId;
    }

    public static UserResponse from(Long userId) {
        return new UserResponse(userId);
    }
}