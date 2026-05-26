package com.soulup.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record VideoAnalysisResult(
        @JsonProperty("exercise")
        String exercise,

        @JsonProperty("repetitions")
        Integer repetitions,

        @JsonProperty("duration")
        Integer duration,

        @JsonProperty("feedback")
        String feedback
) {
}
