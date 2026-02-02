package com.daengddang.daengdong_map;

import com.daengddang.daengdong_map.security.oauth.kakao.KakaoOAuthProperties;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.TimeZone;

@SpringBootApplication
@EnableConfigurationProperties(KakaoOAuthProperties.class)
public class DaengdongMapApplication {

	public static void main(String[] args) {
		SpringApplication.run(DaengdongMapApplication.class, args);
	}

	@PostConstruct
	public void changeTimeKST() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}

}
