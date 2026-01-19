package com.daengddang.daengdong_map.controller;

import com.daengddang.daengdong_map.common.ApiResponse;
import com.daengddang.daengdong_map.domain.user.User;
import com.daengddang.daengdong_map.dto.request.auth.KakaoLoginRequest;
import com.daengddang.daengdong_map.dto.response.auth.AuthTokenResponse;
import com.daengddang.daengdong_map.dto.response.auth.TokenPair;
import com.daengddang.daengdong_map.security.oauth.kakao.model.KakaoOAuthUser;
import com.daengddang.daengdong_map.service.AuthService;
import com.daengddang.daengdong_map.service.AuthTokenService;
import com.daengddang.daengdong_map.service.KakaoOAuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthTokenService authTokenService;
    private final KakaoOAuthService kakaoOAuthService;

    /**
     * 카카오 OAuth 로그인
     */
    @PostMapping("/kakao/login")
    public ApiResponse<AuthTokenResponse> kakaoLogin(
            @RequestBody KakaoLoginRequest request,
            HttpServletResponse response
    ) {
        KakaoOAuthUser oauthUser =
                kakaoOAuthService.authenticate(request.getCode());

        User user = authService.loginOrRegister(oauthUser);
        TokenPair tokenPair = authTokenService.issueTokens(user);

        setRefreshTokenCookie(response, tokenPair.getRefreshToken());

        return ApiResponse.success(
                "로그인에 성공했습니다.",
                AuthTokenResponse.of(
                        tokenPair.getAccessToken(),
                        user.getRegion() == null,
                        user.getId()
                )
        );
    }

    /**
     * Access Token 재발급
     */
    @PostMapping("/token/refresh")
    public ApiResponse<AuthTokenResponse> refreshToken(
            @CookieValue(name = "refreshToken") String refreshToken
    ) {
        String newAccessToken =
                authTokenService.reissueAccessToken(refreshToken);

        return ApiResponse.success(
                "토큰이 갱신되었습니다.",
                AuthTokenResponse.of(
                        newAccessToken,
                        false,
                        null   // userId는 프론트에서 굳이 필요 없으면 null
                )
        );
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public ApiResponse<Void> logout(
            @RequestHeader("Authorization") String authorizationHeader,
            HttpServletResponse response
    ) {
        Long userId = extractUserId(authorizationHeader);

        authTokenService.logout(userId);
        clearRefreshTokenCookie(response);

        return ApiResponse.success("로그아웃 되었습니다.", null);
    }

    /* ================= Cookie ================= */

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // TODO: prod에서는 true
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 14);
        response.addCookie(cookie);
    }

    private void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private Long extractUserId(String authorizationHeader) {
        // Bearer xxx
        return Long.valueOf(
                authorizationHeader.substring(7)
        );
    }
}