package com.daengddang.daengdong_map.service;

import com.daengddang.daengdong_map.common.ErrorCode;
import com.daengddang.daengdong_map.common.exception.BaseException;
import com.daengddang.daengdong_map.dto.response.oauth.KakaoTokenResponse;
import com.daengddang.daengdong_map.dto.response.oauth.KakaoUserResponse;
import com.daengddang.daengdong_map.security.oauth.kakao.KakaoOAuthProperties;
import com.daengddang.daengdong_map.security.oauth.kakao.model.KakaoOAuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class KakaoOAuthService {

    private final RestTemplate restTemplate;
    private final KakaoOAuthProperties kakaoOAuthProperties;

    public KakaoOAuthUser authenticate(String code) {

        // 1. authorization code → access token
        KakaoTokenResponse tokenResponse = requestAccessToken(code);

        // 2. access token → kakao user info
        KakaoUserResponse userResponse =
                requestUserInfo(tokenResponse.getAccessToken());

        return KakaoOAuthUser.from(userResponse);
    }

    private KakaoTokenResponse requestAccessToken(String code) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body =
                "grant_type=authorization_code" +
                        "&client_id=" + kakaoOAuthProperties.getClientId() +
                        "&redirect_uri=" + kakaoOAuthProperties.getRedirectUri() +
                        "&code=" + code;

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<KakaoTokenResponse> response =
                restTemplate.postForEntity(
                        "https://kauth.kakao.com/oauth/token",
                        request,
                        KakaoTokenResponse.class
                );

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new BaseException(ErrorCode.INVALID_AUTHORIZATION_CODE);
        }

        return response.getBody();
    }

    private KakaoUserResponse requestUserInfo(String accessToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<KakaoUserResponse> response =
                restTemplate.exchange(
                        "https://kapi.kakao.com/v2/user/me",
                        HttpMethod.GET,
                        request,
                        KakaoUserResponse.class
                );

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new BaseException(ErrorCode.LOGIN_SERVER_COMMUNICATION_FAILED);
        }

        return response.getBody();
    }
}