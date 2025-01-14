package com.jwtly.historicmarketdata.domain;

import com.jwtly.historicmarketdata.domain.model.Broker;
import com.jwtly.historicmarketdata.domain.model.Period;

import java.time.Instant;

public record GetHistoricDataQuery(
        String symbol,
        Period period,
        Instant from,
        Instant to,
        Broker broker
) {
    public GetHistoricDataQuery {
        validateQuery(symbol, period, from, to);
    }

    private void validateQuery(String symbol, Period period, Instant from, Instant to) {
        if (symbol == null || symbol.isBlank()) {
            throw new IllegalArgumentException("Symbol cannot be null or empty");
        }
        if (period == null) {
            throw new IllegalArgumentException("Period cannot be null");
        }
        if (from == null || to == null) {
            throw new IllegalArgumentException("Time range cannot be null");
        }
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        if (from.isAfter(Instant.now())) {
            throw new IllegalArgumentException("Start date cannot be in the future");
        }
        if (to.isAfter(Instant.now())) {
            throw new IllegalArgumentException("End date cannot be in the future");
        }
    }
}
