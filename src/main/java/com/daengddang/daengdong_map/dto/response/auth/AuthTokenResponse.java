package com.daengddang.daengdong_map.dto.response.auth;

import com.daengddang.daengdong_map.dto.response.user.UserResponse;
import lombok.Getter;

@Getter
public class AuthTokenResponse {

    private final String accessToken;
    private final boolean isNewUser;
    private final UserResponse user;

    private AuthTokenResponse(
            String accessToken,
            boolean isNewUser,
            UserResponse user
    ) {
        this.accessToken = accessToken;
        this.isNewUser = isNewUser;
        this.user = user;
    }

    public static AuthTokenResponse of(
            String accessToken,
            boolean isNewUser,
            Long userId
    ) {
        return new AuthTokenResponse(
                accessToken,
                isNewUser,
                UserResponse.from(userId)
        );
    }
}