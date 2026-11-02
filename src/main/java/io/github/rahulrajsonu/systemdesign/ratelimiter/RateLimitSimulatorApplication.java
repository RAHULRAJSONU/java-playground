package io.github.rahulrajsonu.systemdesign.ratelimiter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

public class RateLimitSimulatorApplication {

    static void main(String[] args) throws Exception {
        AtomicLong fakeTime = new AtomicLong(0);
        RateLimiter rateLimiter = new BucketRateLimiter(fakeTime::get);

//        simulateSequentialExecution(rateLimiter, fakeTime);
        simulateConcurrentExecution(rateLimiter, fakeTime);
    }

    private static void simulateConcurrentExecution(RateLimiter rateLimiter, AtomicLong fakeTime) throws Exception{
        int totalRequest = 1_000;
        UUID clientId = UUID.fromString("862261e9-0253-42c4-95d3-76e769d48bd8");

        AtomicInteger allowed = new AtomicInteger();
        AtomicInteger rejected = new AtomicInteger();

        // All virtual threads signal readiness before starting
        CountDownLatch ready = new CountDownLatch(totalRequest);

        // Release all requests together
        CountDownLatch start = new CountDownLatch(1);

        try(ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()){
            List<Future<?>> futures = new ArrayList<>();
            for(int i = 0;  i < totalRequest; i++){
                Future<?> future = executor.submit(() -> {
                    ready.countDown();

                    try{
                        start.await();
                        RateLimiterResponse response =
                                rateLimiter.isRequestAllowed(clientId);
                        if(response.allowed()){
                            allowed.incrementAndGet();
                        }else{
                            rejected.incrementAndGet();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    }
                });

                futures.add(future);
            }

            ready.await(); // until all 1_000 virtual threads are ready.
            long startTime = System.nanoTime();
            start.countDown();

            for(Future<?> future : futures){
                future.get();
            }

            long executionTime = System.nanoTime() - startTime;

            IO.println("\n=======RESULTS==========");
            IO.println("Total Requests: "+ totalRequest);
            IO.println("Allowed: "+ allowed.get());
            IO.println("Rejected: "+rejected.get());
            IO.println(
                    "Execution Time: "+ TimeUnit.NANOSECONDS.toMillis(executionTime)+ " ms"
            );
            IO.println("Total Processed: "+ (allowed.get() + rejected.get()));
            IO.println("==========================");
        }
    }

    private static void simulateSequentialExecution(RateLimiter rateLimiter, AtomicLong fakeTime) {
        List.of(
                    UUID.fromString("862261e9-0253-42c4-95d3-76e769d48bd8")
//                    UUID.fromString("dcd23d1c-3c4d-472e-808a-6f9b136a9b9b"),
//                    UUID.fromString("71692a59-fd55-479b-adbc-50dd450faf9e")
                )
                .forEach(client -> {
                    IntStream.range(1, 15)
                            .forEach(t -> {
                                IO.println("Processing request for client: "+client+", allowed: "+ rateLimiter.isRequestAllowed(client));
                                if(t >= 3 && t <= 6){
                                   fakeTime.addAndGet(1_000_000_000);
                                }
                            });
                });
    }
}
