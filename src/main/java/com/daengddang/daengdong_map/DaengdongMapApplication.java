package com.daengddang.daengdong_map;

import com.daengddang.daengdong_map.security.oauth.kakao.KakaoOAuthProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(KakaoOAuthProperties.class)
public class DaengdongMapApplication {

	public static void main(String[] args) {
		SpringApplication.run(DaengdongMapApplication.class, args);
	}

}
