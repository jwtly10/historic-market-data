package com.jwtly.historicmarketdata.adapter.out.cache.config;

import com.jwtly.historicmarketdata.adapter.out.cache.CaffeineCacheAdapter;
import com.jwtly.historicmarketdata.domain.model.OHCLV;
import com.jwtly.historicmarketdata.domain.port.in.GetHistoricDataQuery;
import com.jwtly.historicmarketdata.domain.port.out.CachePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class CacheConfig {
    @Bean
    public CachePort<GetHistoricDataQuery, List<OHCLV>> caffeineCacheAdapter() {
        return new CaffeineCacheAdapter();
    }
}
