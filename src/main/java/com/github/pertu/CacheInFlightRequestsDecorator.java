package com.github.pertu;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;

public class CacheInFlightRequestsDecorator<K, V> implements MyAsyncRepository<K, V> {
    private final AsyncLoadingCache<K, V> cache;

    public CacheInFlightRequestsDecorator(MyAsyncRepository<K, V> delegate) {
        this.cache = Caffeine.newBuilder()
                /* I want values to expire immediately,
                   after the underlying loader (delegate)
                   completes the request => expire = ZERO;
                   but I also want to avoid simultaneous/concurrent requests
                   with the same key => this "cache" */
                .expireAfterWrite(Duration.ZERO)
                //.expireAfterWrite(Duration.ofSeconds(1)) // uncomment to make the test pass
                .buildAsync((key, ignoredExecutor) -> delegate.get(key));
    }

    @Override
    public CompletableFuture<V> get(K key) {
        return cache.get(key);
    }
}
