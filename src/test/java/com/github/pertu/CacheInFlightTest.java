package com.github.pertu;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import com.github.benmanes.caffeine.cache.AsyncCacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

class CacheInFlightTest {

    //@Test
    void test_simultaneous_requests_get_the_same_future() throws InterruptedException {
        
        AsyncCacheLoader<Integer, Integer> loader =
                /* the future never completes = always "in flight" */
                (key, ignoredExecutor) -> new CompletableFuture<>();

        /* I want this cache to reuse simultaneous/in-flight requests
           but never actually cache results */
        var cacheInFlight = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ZERO)
                //.expireAfterWrite(Duration.ofSeconds(1)) // uncomment to make the test pass
                .buildAsync(loader);

        var future1 = cacheInFlight.get(1);
        //Thread.sleep(1); // uncomment this sleep() to make the test pass
        var future2 = cacheInFlight.get(1);

        assertSame(future1, future2); // the fail
        System.out.print("OK");
    }

    /* the above test is timing-dependent
       => loop for more reliable fail
       on my machine it usually fails on 2nd or 3rd iteration */
    @Test
    void test_in_loop() throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            test_simultaneous_requests_get_the_same_future();
        }
    }
}