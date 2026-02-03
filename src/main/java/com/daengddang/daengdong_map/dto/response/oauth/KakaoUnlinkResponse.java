package com.daengddang.daengdong_map.dto.response.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class KakaoUnlinkResponse {

    @JsonProperty("id")
    private Long id;
}
