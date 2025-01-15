package com.jwtly.historicmarketdata.domain.port.out;

public interface CachePort<K, V> {
    V get(K key);

    void put(K key, V value);
}
