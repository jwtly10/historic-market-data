package com.jwtly.historicmarketdata.adapter.out.broker.oanda.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CandlestickResponse(
        @JsonProperty("instrument")
        String instrument,
        @JsonProperty("granularity")
        String granularity,
        @JsonProperty("candles")
        List<Candlestick> candles
) {
}