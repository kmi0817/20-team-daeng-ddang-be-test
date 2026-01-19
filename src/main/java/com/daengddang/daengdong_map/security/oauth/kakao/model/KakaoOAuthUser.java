package com.daengddang.daengdong_map.security.oauth.kakao.model;

import com.daengddang.daengdong_map.dto.response.oauth.KakaoUserResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KakaoOAuthUser {

    private Long kakaoUserId;

    public static KakaoOAuthUser from(KakaoUserResponse response) {
        return new KakaoOAuthUser(
                response.getId()
        );
    }
}
