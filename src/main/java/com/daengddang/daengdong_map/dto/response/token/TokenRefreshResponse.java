package com.daengddang.daengdong_map.dto.response.token;

import lombok.Getter;

@Getter
public class TokenRefreshResponse {

    private final String accessToken;

    public TokenRefreshResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}