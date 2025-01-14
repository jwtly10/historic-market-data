package com.jwtly.historicmarketdata.adapter.out.broker.oanda;

import com.jwtly.historicmarketdata.adapter.out.broker.oanda.client.OandaClient;
import com.jwtly.historicmarketdata.adapter.out.broker.oanda.model.Candlestick;
import com.jwtly.historicmarketdata.adapter.out.broker.oanda.model.CandlestickData;
import com.jwtly.historicmarketdata.adapter.out.broker.oanda.model.CandlestickResponse;
import com.jwtly.historicmarketdata.domain.model.Period;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OandaDataAdapterTest {

    @Mock
    private OandaClient oandaClient;

    private OandaDataAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new OandaDataAdapter(oandaClient);
    }

    @Test
    void testBatchingWithLargeTimeRange() {
        String symbol = "EUR_USD";
        Period period = Period.M5;
        Instant from = Instant.parse("2024-12-01T00:00:00Z");
        Instant to = Instant.parse("2025-01-10T00:00:00Z");

        when(oandaClient.fetchCandles(eq(symbol), eq("M5"), any(Instant.class), any(Instant.class)))
                .thenAnswer(invocation -> {
                    Instant batchStart = invocation.getArgument(2);
                    Instant batchEnd = invocation.getArgument(3);
                    return createMockResponse(batchStart, batchEnd, period);
                });

        var result = adapter.getHistoricData(symbol, period, from, to);

        // A day would have 288 candles at 5 minutes
        // 40 days between from and to
        // 40*288=11,520 candles

        // As of 14/Jan/2025, Oanda has a limit of 5,000 candles per request,
        // and we batch at 4,000 candles per request, just to be safe
        // so there should be 11,520/4,000 = 2.88 batches, so 3 (4000, 4000, 3520)
        verify(oandaClient, times(3)).fetchCandles(eq(symbol), eq("M5"), any(Instant.class), any(Instant.class));

        assertFalse(result.isEmpty());
        assertTrue(result.getFirst().time().equals(from));
        assertTrue(result.getLast().time().equals(to));

        for (int i = 1; i < result.size(); i++) {
            assertTrue(result.get(i).time().isAfter(result.get(i - 1).time()));
        }
    }

    private CandlestickResponse createMockResponse(Instant start, Instant end, Period period) {
        List<Candlestick> candles = new ArrayList<>();
        Instant current = start;

        while (!current.isAfter(end)) {
            candles.add(new Candlestick(
                    current.toString(),
                    new CandlestickData("1.1000", "1.1100", "1.0900", "1.1050"),
                    100L,
                    true
            ));
            current = current.plus(period.getDuration());
        }
        return new CandlestickResponse("EUR_USD", "M5", candles);
    }

    @Test
    void testValidationFailsForFutureStartDate() {
        Instant futureStart = Instant.now().plus(1, ChronoUnit.DAYS);
        Instant futureEnd = futureStart.plus(1, ChronoUnit.DAYS);

        assertThrows(IllegalArgumentException.class, () ->
                adapter.getHistoricData("EUR_USD", Period.M5, futureStart, futureEnd)
        );
    }

    @Test
    void testValidationFailsForNullPeriod() {
        Instant start = Instant.now().minus(1, ChronoUnit.DAYS);
        Instant end = Instant.now();

        assertThrows(IllegalArgumentException.class, () ->
                adapter.getHistoricData("EUR_USD", null, start, end)
        );
    }

    @Test
    void testValidationFailsForInvalidSymbol() {
        Instant start = Instant.now().minus(1, ChronoUnit.DAYS);
        Instant end = Instant.now();

        assertThrows(IllegalArgumentException.class, () ->
                adapter.getHistoricData("", Period.M5, start, end)
        );
    }

    @Test
    void testValidationFailsForStartAfterEnd() {
        Instant start = Instant.now().minus(1, ChronoUnit.DAYS);
        Instant end = start.minus(1, ChronoUnit.DAYS);

        assertThrows(IllegalArgumentException.class, () ->
                adapter.getHistoricData("EUR_USD", Period.M5, start, end)
        );
    }
}