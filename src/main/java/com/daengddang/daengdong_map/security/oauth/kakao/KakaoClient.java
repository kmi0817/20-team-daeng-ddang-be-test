package com.daengddang.daengdong_map.security.oauth.kakao;

import com.daengddang.daengdong_map.common.ErrorCode;
import com.daengddang.daengdong_map.common.exception.BaseException;
import com.daengddang.daengdong_map.dto.response.oauth.KakaoTokenResponse;
import com.daengddang.daengdong_map.dto.response.oauth.KakaoUnlinkResponse;
import com.daengddang.daengdong_map.dto.response.oauth.KakaoUserResponse;
import com.daengddang.daengdong_map.security.oauth.kakao.model.KakaoOAuthUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoClient {

    private final RestClient restClient;
    private final KakaoOAuthProperties props;

    public KakaoOAuthUser authenticate(String authorizationCode) {

        KakaoTokenResponse token = requestToken(authorizationCode);
        KakaoUserResponse user = requestUser(token.getAccessToken());

        return KakaoOAuthUser.from(user);
    }

    public KakaoUnlinkResponse unlinkByAdminKey(Long kakaoUserId) {
        try {
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("target_id_type", "user_id");
            body.add("target_id", String.valueOf(kakaoUserId));

            return restClient.post()
                    .uri(props.getUnlinkUri())
                    .header(HttpHeaders.AUTHORIZATION, "KakaoAK " + props.getAdminKey())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(body)
                    .retrieve()
                    .body(KakaoUnlinkResponse.class);

        } catch (Exception e) {
            throw new BaseException(ErrorCode.LOGIN_SERVER_COMMUNICATION_FAILED);
        }
    }

    private KakaoTokenResponse requestToken(String code) {
        try {
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "authorization_code");
            body.add("client_id", props.getClientId());
            body.add("client_secret", props.getClientSecret());
            body.add("redirect_uri", props.getRedirectUri());
            body.add("code", code);

            return restClient.post()
                    .uri(props.getTokenUri())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(body)
                    .retrieve()
                    .body(KakaoTokenResponse.class);

        } catch (Exception e) {
            throw new BaseException(ErrorCode.LOGIN_SERVER_COMMUNICATION_FAILED);
        }
    }

    private KakaoUserResponse requestUser(String accessToken) {
        try {
            return restClient.get()
                    .uri(props.getUserInfoUri())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .body(KakaoUserResponse.class);

        } catch (Exception e) {
            throw new BaseException(ErrorCode.LOGIN_SERVER_COMMUNICATION_FAILED);
        }
    }
}
