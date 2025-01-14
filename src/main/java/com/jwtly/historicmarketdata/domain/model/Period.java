package com.jwtly.historicmarketdata.domain.model;

import lombok.Getter;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

@Getter
public enum Period {
    M1(Duration.ofMinutes(1)),
    M5(Duration.ofMinutes(5)),
    M15(Duration.ofMinutes(15)),
    M30(Duration.ofMinutes(30)),
    H1(Duration.ofHours(1)),
    H4(Duration.ofHours(4)),
    D(Duration.ofDays(1)),
    W(Duration.ofDays(7));

    private final Duration duration;

    Period(Duration duration) {
        this.duration = duration;
    }

    public static Period fromDuration(Duration duration) {
        return Arrays.stream(values())
                .filter(period -> period.duration.equals(duration))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No period matches duration: " + duration));
    }

    public long getNumberOfCandles(Instant start, Instant end) {
        return duration.getSeconds() == 0
                ? 0
                : ChronoUnit.SECONDS.between(start, end) / duration.getSeconds();
    }
}