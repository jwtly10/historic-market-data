package com.jwtly.historicmarketdata.adapter.out.broker.config;

import com.jwtly.historicmarketdata.domain.model.Broker;
import com.jwtly.historicmarketdata.domain.port.out.HistoricDataPort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumMap;
import java.util.Map;

@Configuration
public class BrokerConfig {
    @Bean
    public Map<Broker, HistoricDataPort> brokerAdapters(
            @Qualifier("oandaHistoricDataAdapter") HistoricDataPort oandaAdapter
    ) {
        Map<Broker, HistoricDataPort> adapters = new EnumMap<>(Broker.class);
        adapters.put(Broker.OANDA, oandaAdapter);
        return adapters;
    }
}