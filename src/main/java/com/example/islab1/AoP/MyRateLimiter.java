package com.example.islab1.AoP;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class MyRateLimiter {

    private final Map<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
    private final Map<String, Long> resetTimes = new ConcurrentHashMap<>();

    private static final int DEFAULT_LIMIT = 1000;
    private static final int TIME_WINDOW = 60 * 1000;


    public boolean isAllowed(String key) {
        long now = System.currentTimeMillis();
        AtomicInteger counter = requestCounts.computeIfAbsent(key, k -> new AtomicInteger(0));
        Long lastReset = resetTimes.computeIfAbsent(key, k -> now);
        if (now - lastReset > TIME_WINDOW) {
            counter.set(0);
            resetTimes.put(key, now);
        }
        if (counter.get() >= DEFAULT_LIMIT) {
            return false;
        }
        counter.incrementAndGet();
        return true;
    }
}