package com.github.pertu;

import java.util.concurrent.CompletableFuture;

public interface MyAsyncRepository<K, V> {
    CompletableFuture<V> get(K key);
}
