package io.github.rahulrajsonu.systemdesign.ratelimiter;

public record RateLimitConfig(int capacity, double refillRate) {}
