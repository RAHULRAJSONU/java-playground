package io.github.rahulrajsonu.systemdesign.ratelimiter;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.LongSupplier;

public class BucketManager {
    private static final long NANOS_PER_SECOND = 1_000_000_000;
    private final Map<UUID, Bucket> buckets;
    private final LongSupplier clock;

    public BucketManager(LongSupplier clock) {
        this.clock = Objects.requireNonNull(clock);
        this.buckets = new ConcurrentHashMap<>();
    }

    public RateLimiterResponse consume(UUID clientId){
        Objects.requireNonNull(clientId, "clientId cannot be null");

        Bucket bucket = buckets.computeIfAbsent(clientId, id -> {
            RateLimitConfig config = ClientService.getClientTier(id);
            return new Bucket(
                    config.capacity(),
                    config.refillRate(),
                    clock.getAsLong()
            );
        });
        return consumeToken(bucket);
    }

    private RateLimiterResponse consumeToken(Bucket bucket){
        while (true){
            // 1. Read the current immutable state.
            Bucket.BucketState current = bucket.getState();

            // 2. Capture time after reading the state
            long now = clock.getAsLong();

            // 3. Calculate the refilled state
            double elapsedSeconds = (double)(now - current.lastRefillTimestamp())/NANOS_PER_SECOND;
            double refilledToken = Math.min(
                    bucket.getCapacity(),
                    current.availableTokens() + elapsedSeconds * bucket.getRefillRate()
            );

            // 4. Decide weather this request can consume a token
            boolean allowed = refilledToken >= 1.0;

            double remainingTokens = allowed
                    ? refilledToken - 1.0
                    : refilledToken;
            // 5. Construct the next immutable state
            Bucket.BucketState updated = new Bucket.BucketState(
                    remainingTokens, now
            );
            // 6. Atomically publish the state
            if(bucket.compareAndSet(current, updated)){
                double retryAfter = allowed ? 0.0 : (1.0 - remainingTokens)/bucket.getRefillRate();
                return new RateLimiterResponse(
                        allowed,
                        remainingTokens,
                        retryAfter
                );
            }
            // CAS failed: another thread modified this bucket.
            // Retry using the latest state.
        }
    }

}
