package com.jwtly.historicmarketdata.domain.service;

import com.jwtly.historicmarketdata.domain.model.Broker;
import com.jwtly.historicmarketdata.domain.model.OHCLV;
import com.jwtly.historicmarketdata.domain.port.in.GetHistoricDataQuery;
import com.jwtly.historicmarketdata.domain.port.in.GetHistoricDataUseCase;
import com.jwtly.historicmarketdata.domain.port.out.CachePort;
import com.jwtly.historicmarketdata.domain.port.out.HistoricDataPort;

import java.util.List;
import java.util.Map;

public class GetHistoricDataService implements GetHistoricDataUseCase {
    private final Map<Broker, HistoricDataPort> brokerAdapters;
    private final CachePort<GetHistoricDataQuery, List<OHCLV>> cache;

    public GetHistoricDataService(
            Map<Broker, HistoricDataPort> brokerAdapters,
            CachePort<GetHistoricDataQuery, List<OHCLV>> cache
    ) {
        this.brokerAdapters = brokerAdapters;
        this.cache = cache;
    }

    @Override
    public List<OHCLV> getHistoricData(GetHistoricDataQuery query) {
        var cachedData = cache.get(query);
        if (cachedData != null) {
            return cachedData;
        }

        var broker = brokerAdapters.get(query.broker());
        var data = broker.getHistoricData(query.symbol(), query.period(), query.from(), query.to());

        cache.put(query, data);

        return data;
    }
}
