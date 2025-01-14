package com.jwtly.historicmarketdata.domain.model;

import java.time.Instant;

public record OHCLV(
        double open,
        double high,
        double close,
        double low,
        double volume,
        Instant time
) {
}
