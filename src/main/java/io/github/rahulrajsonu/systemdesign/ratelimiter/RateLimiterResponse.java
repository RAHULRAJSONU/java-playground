package io.github.rahulrajsonu.systemdesign.ratelimiter;

public record RateLimiterResponse(boolean allowed, double tokenLeft, double retryAfter){}