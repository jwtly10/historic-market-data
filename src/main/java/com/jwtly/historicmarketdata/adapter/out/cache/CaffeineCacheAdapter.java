package com.jwtly.historicmarketdata.adapter.out.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.jwtly.historicmarketdata.domain.model.Broker;
import com.jwtly.historicmarketdata.domain.model.OHCLV;
import com.jwtly.historicmarketdata.domain.model.Period;
import com.jwtly.historicmarketdata.domain.port.in.GetHistoricDataQuery;
import com.jwtly.historicmarketdata.domain.port.out.CachePort;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.List;

@Slf4j
public class CaffeineCacheAdapter implements CachePort<GetHistoricDataQuery, List<OHCLV>> {
    private final static int MAX_SIZE = 10;
    private final Cache<QueryCacheKey, List<OHCLV>> cache;

    public CaffeineCacheAdapter() {
        this.cache = Caffeine.newBuilder()
                .maximumSize(MAX_SIZE)
                .recordStats()
                .build();
    }

    @Override
    public List<OHCLV> get(GetHistoricDataQuery key) {
        return cache.getIfPresent(QueryCacheKey.from(key));
    }

    @Override
    public void put(GetHistoricDataQuery key, List<OHCLV> value) {
        cache.put(QueryCacheKey.from(key), value);

        var stats = cache.stats();
        var size = cache.estimatedSize();

        log.info("Cache stats after put - Size: {}, Hit rate: {}, Miss rate: {}, Eviction count: {}",
                size,
                stats.hitRate(),
                stats.missRate(),
                stats.evictionCount()
        );

        if (size >= MAX_SIZE * 0.8) { // 80% of max size
            log.warn("Cache is approaching maximum capacity: {}/100", size);
        }
    }

    private record QueryCacheKey(
            String symbol,
            Period period,
            Instant from,
            Instant to,
            Broker broker
    ) {
        static QueryCacheKey from(GetHistoricDataQuery query) {
            return new QueryCacheKey(
                    query.symbol(),
                    query.period(),
                    query.from(),
                    query.to(),
                    query.broker()
            );
        }
    }
}
