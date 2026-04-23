package com.ks.demo.cache.config;

public interface MultiLevelCache<K, V> {

    V get(K key);

    void put(K key, V value);

    void evict(K key);

    void clear();
}
