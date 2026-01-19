package com.daengddang.daengdong_map.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secret;
    private String issuer;

    private long accessTokenExpireMinutes;
    private long refreshTokenExpireDays;
}