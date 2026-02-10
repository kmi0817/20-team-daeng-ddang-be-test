package com.daengddang.daengdong_map.dto.response.footprint;

import lombok.Builder;
import lombok.Getter;

@Getter
public class FootprintDailyRecordItemResponse {

    private final FootprintRecordType type;
    private final Long id;
    private final String title;

    @Builder
    private FootprintDailyRecordItemResponse(FootprintRecordType type, Long id, String title) {
        this.type = type;
        this.id = id;
        this.title = title;
    }

    public static FootprintDailyRecordItemResponse of(FootprintRecordType type, Long id, String title) {
        return FootprintDailyRecordItemResponse.builder()
                .type(type)
                .id(id)
                .title(title)
                .build();
    }
}
