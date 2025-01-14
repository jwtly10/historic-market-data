package com.jwtly.historicmarketdata.adapter.out.broker.oanda.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <a href="https://developer.oanda.com/rest-live-v20/instrument-df/#Candlestick">[Docs]</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Candlestick(
        @JsonProperty("time")
        String time,
        @JsonProperty("mid")
        CandlestickData mid,
        @JsonProperty("volume")
        double volume,
        @JsonProperty("complete")
        boolean complete
) {
}
