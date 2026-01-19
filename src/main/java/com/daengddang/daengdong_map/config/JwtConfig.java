package com.daengddang.daengdong_map.config;

import com.daengddang.daengdong_map.security.jwt.JwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties(JwtProperties.class)
@Configuration
public class JwtConfig {
}