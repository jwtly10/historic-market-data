package com.jwtly.historicmarketdata.adapter.out.broker.oanda.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariables;

import java.net.http.HttpClient;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@EnabledIfEnvironmentVariables({
        @EnabledIfEnvironmentVariable(named = "OANDA_API_KEY", matches = ".*")
})
class OandaClientIntegrationTest {
    private OandaClient client;
    private String apiKey;

    @BeforeEach
    void setUp() {
        apiKey = System.getenv("OANDA_API_KEY");
        Assertions.assertNotNull(apiKey, "OANDA_API_KEY environment variable must be set");

        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        client = new OandaClient(apiKey, "https://api-fxpractice.oanda.com", HttpClient.newHttpClient(), objectMapper);
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void testCanFetchCandles() {
        var from = ZonedDateTime.of(2025, 1, 8, 0, 0, 0, 0, ZoneId.of("UTC")).toInstant();
        var to = ZonedDateTime.of(2025, 1, 10, 0, 0, 0, 0, ZoneId.of("UTC")).toInstant();

        var response = client.fetchCandles("EUR_USD", "M5", Instant.from(from), Instant.from(to));

        // Check everything is in order
        for (int i = 0; i < 5; i++) {
            var candle = response.candles().get(i);
            var expectedTime = from.plusSeconds(i * 5 * 60);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
                    .withZone(ZoneId.of("UTC"));
            String expectedTimeStr = formatter.format(expectedTime);
            String actualTimeStr = formatter.format(Instant.parse(candle.time()));

            Assertions.assertEquals(expectedTimeStr, actualTimeStr);
        }

        Assertions.assertNotNull(response);
        Assertions.assertEquals(576, response.candles().size());
        Assertions.assertEquals("EUR_USD", response.instrument());
        Assertions.assertEquals("M5", response.granularity());
        Assertions.assertFalse(response.candles().isEmpty());
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void testGetsAllDataForADay() {
        var from = ZonedDateTime.of(2025, 1, 8, 0, 0, 0, 0, ZoneId.of("UTC")).toInstant();
        var to = ZonedDateTime.of(2025, 1, 9, 0, 0, 0, 0, ZoneId.of("UTC")).toInstant();

        var response = client.fetchCandles("EUR_USD", "M5", Instant.from(from), Instant.from(to));
        Assertions.assertEquals(288, response.candles().size());
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void testDoesNotReturnWeekendData() {
        var from = ZonedDateTime.of(2025, 1, 11, 0, 0, 0, 0, ZoneId.of("UTC")).toInstant();
        var to = ZonedDateTime.of(2025, 1, 12, 0, 0, 0, 0, ZoneId.of("UTC")).toInstant();

        var response = client.fetchCandles("EUR_USD", "M5", Instant.from(from), Instant.from(to));
        Assertions.assertEquals(0, response.candles().size());
    }
}