package com.jwtly.historicmarketdata.domain.service;

import com.jwtly.historicmarketdata.domain.GetHistoricDataQuery;
import com.jwtly.historicmarketdata.domain.model.Broker;
import com.jwtly.historicmarketdata.domain.model.OHCLV;
import com.jwtly.historicmarketdata.domain.port.in.GetHistoricDataUseCase;
import com.jwtly.historicmarketdata.domain.port.out.HistoricDataPort;

import java.util.List;
import java.util.Map;

public class GetHistoricDataService implements GetHistoricDataUseCase {
    private final Map<Broker, HistoricDataPort> brokerAdapters;

    public GetHistoricDataService(Map<Broker, HistoricDataPort> brokerAdapters) {
        this.brokerAdapters = brokerAdapters;
    }

    @Override
    public List<OHCLV> getHistoricData(GetHistoricDataQuery query) {
        var broker = brokerAdapters.get(query.broker());
        return broker.getHistoricData(query.symbol(), query.period(), query.from(), query.to());
    }
}
