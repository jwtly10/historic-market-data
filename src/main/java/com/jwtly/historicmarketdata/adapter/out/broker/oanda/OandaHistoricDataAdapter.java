package com.jwtly.historicmarketdata.adapter.out.broker.oanda;

import com.jwtly.historicmarketdata.adapter.out.broker.oanda.client.OandaClient;
import com.jwtly.historicmarketdata.adapter.out.broker.oanda.model.CandlestickResponse;
import com.jwtly.historicmarketdata.domain.exception.BrokerRequestException;
import com.jwtly.historicmarketdata.domain.model.OHCLV;
import com.jwtly.historicmarketdata.domain.model.Period;
import com.jwtly.historicmarketdata.domain.port.out.HistoricDataPort;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
public class OandaHistoricDataAdapter implements HistoricDataPort {
    private final static int MAX_CANDLE_PER_REQUEST = 3999;  // we have a smaller limit than the default 5000 for oanda
    private final OandaClient client;

    public OandaHistoricDataAdapter(OandaClient client) {
        this.client = client;
    }

    @Override
    public List<OHCLV> getHistoricData(String symbol, Period period, Instant from, Instant to) throws BrokerRequestException {
        log.debug("Fetching historic data for symbol {} ({}) from {} to {}", symbol, period, from, to);
        validateRequest(symbol, period, from, to);
        var start = Instant.now();
        var batches = 0;
        var granularity = mapPeriodToGranularity(period);

        List<OHCLV> bars = new ArrayList<>();

        Instant batchStart = from;
        while (batchStart.isBefore(to)) {
            Instant batchTo = batchStart.plus(period.getDuration().multipliedBy(MAX_CANDLE_PER_REQUEST));
            if (batchTo.isAfter(to)) {
                batchTo = to;
            }
            log.debug("Fetching candle batch from {} to {}", batchStart, batchTo);
            CandlestickResponse res = client.fetchCandles(symbol, granularity, batchStart, batchTo);
            batches++;
            List<OHCLV> batchBars = mapToOHCLV(res);
            log.debug("Fetched {} candles in batch", batchBars.size());

            if (batchBars.isEmpty()) {
                break; // No more data
            }

            bars.addAll(batchBars);

            batchStart = batchBars.getLast().time().plus(period.getDuration());
        }

        log.info("Fetched {} candles in {} batches in {}ms", bars.size(), batches, Instant.now().toEpochMilli() - start.toEpochMilli());
        return bars;
    }

    private List<OHCLV> mapToOHCLV(CandlestickResponse res) {
        return res.candles().stream()
                .map(candle -> new OHCLV(
                        Double.parseDouble(candle.mid().open()),
                        Double.parseDouble(candle.mid().high()),
                        Double.parseDouble(candle.mid().low()),
                        Double.parseDouble(candle.mid().close()),
                        candle.volume(),
                        Instant.parse(candle.time())
                ))
                .collect(Collectors.toList());
    }

    private String mapPeriodToGranularity(Period period) {
        return switch (period) {
            case M1 -> "M1";
            case M5 -> "M5";
            case M15 -> "M15";
            case M30 -> "M30";
            case H1 -> "H1";
            case H4 -> "H4";
            case D -> "D";
            case W -> "W";
        };
    }

    private void validateRequest(String symbol, Period period, Instant from, Instant to) {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        if (from.isAfter(Instant.now())) {
            throw new IllegalArgumentException("Start date cannot be in the future");
        }

        if (to.isAfter(Instant.now())) {
            throw new IllegalArgumentException("End date cannot be in the future");
        }

        if (period == null) {
            throw new IllegalArgumentException("Period cannot be null");
        }

        if (symbol == null || symbol.isBlank()) {
            throw new IllegalArgumentException("Symbol cannot be null or empty");
        }
    }
}
