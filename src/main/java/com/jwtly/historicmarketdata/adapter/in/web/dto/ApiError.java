package com.jwtly.historicmarketdata.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jwtly.historicmarketdata.domain.exception.BrokerRequestException;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Error response containing details about the failure")
public record ApiError(
        @Schema(
                description = "Detailed error message",
                example = "Failed to fetch data from broker"
        )
        String message,

        @Schema(
                description = "Original status code from broker",
                example = "429",
                nullable = true
        )
        Integer statusCode,

        @Schema(
                description = "Detailed response from broker",
                example = "{\"errorMessage\":\"Rate limit exceeded\"}",
                nullable = true
        )
        String details
) {
    public ApiError(String message) {
        this(message, null, null);
    }

    public static ApiError fromBrokerException(BrokerRequestException ex) {
        return new ApiError(
                ex.getMessage(),
                ex.getStatusCode(),
                ex.getResponseBody()
        );
    }
}