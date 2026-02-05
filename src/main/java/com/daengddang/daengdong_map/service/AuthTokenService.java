package com.daengddang.daengdong_map.service;

import com.daengddang.daengdong_map.common.ErrorCode;
import com.daengddang.daengdong_map.common.exception.BaseException;
import com.daengddang.daengdong_map.domain.auth.RefreshToken;
import com.daengddang.daengdong_map.domain.user.User;
import com.daengddang.daengdong_map.domain.user.UserStatus;
import com.daengddang.daengdong_map.dto.response.auth.TokenPair;
import com.daengddang.daengdong_map.repository.RefreshTokenRepository;
import com.daengddang.daengdong_map.security.jwt.JwtProperties;
import com.daengddang.daengdong_map.security.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthTokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    @Transactional
    public TokenPair issueTokens(User user) {

        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getId(),
                user.getStatus()
        );

        String refreshToken = jwtTokenProvider.generateRefreshToken(
                user.getId()
        );

        LocalDateTime expiresAt = LocalDateTime.now()
                .plusDays(jwtProperties.getRefreshTokenExpireDays());

        refreshTokenRepository.save(
                RefreshToken.builder()
                        .token(refreshToken)
                        .userId(user.getId())
                        .expiresAt(expiresAt)
                        .build()
        );

        return new TokenPair(accessToken, refreshToken);
    }

    @Transactional
    public String reissueAccessToken(String refreshToken) {

        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() ->
                        new BaseException(ErrorCode.INVALID_REFRESH_TOKEN)
                );

        if (storedToken.isExpired()) {
            throw new BaseException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        Long userId = parseUserId(refreshToken);

        /*
         * refresh token 이 DB에 살아 있고 만료되지 않았다면
         * 최소한 ACTIVE 사용자라는 의미
         * (탈퇴/차단 처리는 Security Filter 단계에서 다시 검증)
         */
        return jwtTokenProvider.generateAccessToken(
                userId,
                UserStatus.ACTIVE
        );
    }

    @Transactional
    public void logout(Long userId) {
        refreshTokenRepository.deleteAllByUserId(userId);
    }

    private Long parseUserId(String refreshToken) {
        try {
            Claims claims = jwtTokenProvider.parseClaims(refreshToken);
            return Long.valueOf(claims.getSubject());
        } catch (ExpiredJwtException e) {
            throw new BaseException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        } catch (JwtException | IllegalArgumentException e) {
            throw new BaseException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
    }
}
