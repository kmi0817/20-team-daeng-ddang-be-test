package com.daengddang.daengdong_map.dto.response.user;

import com.daengddang.daengdong_map.domain.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserTermAgreeResponse {

    private final Boolean isAgreed;

    @Builder
    public UserTermAgreeResponse(Boolean isAgreed) {
        this.isAgreed = isAgreed;
    }

    public static UserTermAgreeResponse from(User user) {
        return UserTermAgreeResponse.builder()
                .isAgreed(user.getIsAgreed())
                .build();
    }
}
