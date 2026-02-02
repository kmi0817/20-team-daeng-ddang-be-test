package com.daengddang.daengdong_map.dto.request.expression;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class FastApiExpressionAnalyzeRequest {

    @JsonProperty("analysis_id")
    private String analysisId;

    @JsonProperty("video_url")
    private String videoUrl;

    private FastApiExpressionAnalyzeRequest(String analysisId, String videoUrl) {
        this.analysisId = analysisId;
        this.videoUrl = videoUrl;
    }

    public static FastApiExpressionAnalyzeRequest of(String analysisId, String videoUrl) {
        return new FastApiExpressionAnalyzeRequest(analysisId, videoUrl);
    }
}
