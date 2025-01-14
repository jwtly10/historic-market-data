package com.jwtly.historicmarketdata.domain.port.out;

import com.jwtly.historicmarketdata.domain.exception.BrokerRequestException;
import com.jwtly.historicmarketdata.domain.model.OHCLV;
import com.jwtly.historicmarketdata.domain.model.Period;

import java.time.Instant;
import java.util.List;

public interface HistoricDataPort {
    List<OHCLV> getHistoricData(String symbol, Period period, Instant start, Instant end) throws BrokerRequestException;
}
