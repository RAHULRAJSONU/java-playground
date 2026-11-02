package io.github.rahulrajsonu.systemdesign.ratelimiter;

import java.util.UUID;

public interface RateLimiter {

    RateLimiterResponse isRequestAllowed(UUID clientId);
}
