package io.github.rahulrajsonu.systemdesign.ratelimiter;

import java.util.UUID;
import java.util.function.LongSupplier;

public class BucketRateLimiter implements RateLimiter{

    private final BucketManager bucketManager;

    public BucketRateLimiter(LongSupplier clock) {
        this.bucketManager = new BucketManager(clock);
    }

    @Override
    public RateLimiterResponse isRequestAllowed(UUID clientId) {
        return bucketManager.consume(clientId);
    }
}
