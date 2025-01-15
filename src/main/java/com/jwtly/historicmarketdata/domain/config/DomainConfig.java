package com.jwtly.historicmarketdata.domain.config;

import com.jwtly.historicmarketdata.domain.model.Broker;
import com.jwtly.historicmarketdata.domain.model.OHCLV;
import com.jwtly.historicmarketdata.domain.port.in.GetHistoricDataQuery;
import com.jwtly.historicmarketdata.domain.port.in.GetHistoricDataUseCase;
import com.jwtly.historicmarketdata.domain.port.out.CachePort;
import com.jwtly.historicmarketdata.domain.port.out.HistoricDataPort;
import com.jwtly.historicmarketdata.domain.service.GetHistoricDataService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
public class DomainConfig {
    @Bean
    public GetHistoricDataUseCase getHistoricDataUseCase(
            Map<Broker, HistoricDataPort> brokerAdapters,
            CachePort<GetHistoricDataQuery, List<OHCLV>> cache
    ) {
        return new GetHistoricDataService(brokerAdapters, cache);
    }
}