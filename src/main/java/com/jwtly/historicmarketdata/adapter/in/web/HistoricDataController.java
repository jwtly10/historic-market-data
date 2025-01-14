package com.jwtly.historicmarketdata.adapter.in.web;

import com.jwtly.historicmarketdata.domain.GetHistoricDataQuery;
import com.jwtly.historicmarketdata.domain.model.Broker;
import com.jwtly.historicmarketdata.domain.model.OHCLV;
import com.jwtly.historicmarketdata.domain.model.Period;
import com.jwtly.historicmarketdata.domain.port.in.GetHistoricDataUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("api/v1/candles")
@Tag(name = "Historical Market Data", description = "APIs for pulling historic market candle data")
public class HistoricDataController {
    private final GetHistoricDataUseCase getHistoricDataUseCase;

    public HistoricDataController(GetHistoricDataUseCase getHistoricDataUseCase) {
        this.getHistoricDataUseCase = getHistoricDataUseCase;
    }

    @GetMapping
    public List<OHCLV> getHistoricData(
            @RequestParam String symbol,
            @RequestParam Period period,
            @RequestParam Instant from,
            @RequestParam Instant to,
            @RequestParam String broker
    ) {
        var query = new GetHistoricDataQuery(symbol, period, from, to, Broker.fromString(broker));
        return getHistoricDataUseCase.getHistoricData(query);
    }
}
