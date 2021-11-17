package com.github.pertu;

import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

class CacheInFlightRequestsDecoratorTest {

    //@Test
    void test_simultaneous_requests_get_the_same_future() throws InterruptedException {

        MyAsyncRepository<Integer, Integer> delegate =
                /* returned future never completes (always "in flight") */
                key -> new CompletableFuture<>();

        var cacheInFlight = new CacheInFlightRequestsDecorator<>(delegate);

        var future1 = cacheInFlight.get(1);
        Thread.sleep(1); // uncomment this sleep() to make the test pass
        var future2 = cacheInFlight.get(1);

        assertSame(future1, future2); // the fail
        System.out.print("OK");
    }

    /* the above test is timing-dependent
       => repeat for more reliable fail
       on my machine it usually fails on 2nd or 3rd iteration */
    @Test
    void test_in_loop() throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            test_simultaneous_requests_get_the_same_future();
        }
    }
}