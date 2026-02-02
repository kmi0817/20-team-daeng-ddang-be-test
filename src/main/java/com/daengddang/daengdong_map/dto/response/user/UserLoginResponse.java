package com.daengddang.daengdong_map.dto.response.user;

import com.daengddang.daengdong_map.domain.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserLoginResponse {

    private final Long userId;
    private final Boolean isAgreed;

    @Builder
    private UserLoginResponse(Long userId, Boolean isAgreed) {
        this.userId = userId;
        this.isAgreed = isAgreed;
    }

    public static UserLoginResponse from(User user) {
        return UserLoginResponse.builder()
                .userId(user.getId())
                .isAgreed(user.getIsAgreed())
                .build();
    }

    public static UserLoginResponse from(Long userId) {
        return UserLoginResponse.builder()
                .userId(userId)
                .isAgreed(null)
                .build();
    }
}
