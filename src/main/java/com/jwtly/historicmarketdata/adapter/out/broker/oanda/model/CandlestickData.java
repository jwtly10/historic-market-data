package com.jwtly.historicmarketdata.adapter.out.broker.oanda.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <a href="https://developer.oanda.com/rest-live-v20/instrument-df/#CandlestickData">[Docs]</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CandlestickData(
        @JsonProperty("o")
        String open,
        @JsonProperty("h")
        String high,
        @JsonProperty("l")
        String low,
        @JsonProperty("c")
        String close
) {
}
