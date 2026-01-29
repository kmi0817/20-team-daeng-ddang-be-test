package com.daengddang.daengdong_map.dto.request.expression;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ExpressionAnalyzeRequest {

    @NotBlank
    private String videoUrl;
}
