package com.example.gateway.config;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Naive fixed-window rate limiter — no Redis, just an in-memory counter.
 * Good enough to feel the mechanism; not something to actually deploy as-is.
 */
@Component
public class RateLimitFilterFunction implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    private static final int MAX_REQUESTS_PER_WINDOW = 5;
    private static final Duration WINDOW = Duration.ofSeconds(10);

    private final AtomicInteger requestCount = new AtomicInteger(0);
    private volatile Instant windowStart = Instant.now();

    @Override
    public ServerResponse filter(ServerRequest request, HandlerFunction<ServerResponse> next) throws Exception {
        if (!tryAcquire()) {
            return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }

        return next.handle(request);
    }

    private synchronized boolean tryAcquire() {
        if (Instant.now().isAfter(windowStart.plus(WINDOW))) {
            windowStart = Instant.now();
            requestCount.set(0);
        }
        return requestCount.incrementAndGet() <= MAX_REQUESTS_PER_WINDOW;
    }
}