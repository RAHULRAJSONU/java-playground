package io.github.rahulrajsonu.systemdesign.ratelimiter;

import lombok.Getter;

import java.util.concurrent.atomic.AtomicReference;

public class Bucket {
    @Getter
    private final int capacity;
    @Getter
    private final double refillRate;
    private final AtomicReference<BucketState> state;

    public Bucket(int capacity, double refillRate, long initialTimestamp) {
        if(capacity <= 0){
            throw new IllegalArgumentException("Capacity must be greater than zero");
        }

        if(!Double.isFinite(refillRate) || refillRate <= 0){
            throw new IllegalArgumentException("Refill rate must be greater than zero");
        }
        this.capacity = capacity;
        this.refillRate = refillRate;
        this.state = new AtomicReference<>(
                new BucketState(capacity, initialTimestamp)
        );
    }

    BucketState getState() {
        return state.get();
    }

    boolean compareAndSet(BucketState expected, BucketState newState){
        return state.compareAndSet(expected, newState);
    }

    public record BucketState(
            double availableTokens,
            long lastRefillTimestamp
    ){}
}
