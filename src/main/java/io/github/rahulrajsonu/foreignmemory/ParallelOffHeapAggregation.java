package io.github.rahulrajsonu.foreignmemory;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ParallelOffHeapAggregation {
    private static final ValueLayout.OfInt INT = ValueLayout.JAVA_INT;

    static void main(String[] args) throws ExecutionException, InterruptedException {
        int elementCount = 10_000_000;
        long start = System.currentTimeMillis();
        int workerCount = Math.min(Runtime.getRuntime().availableProcessors(), 1);
        long total = aggregate(elementCount, workerCount);
        System.out.println("Worker: "+workerCount);
        System.out.println("Total: "+total);
        System.out.println("Expected: "+((long)elementCount * (elementCount-1)/2));
        System.out.println("Time took to process: "+ (System.currentTimeMillis()-start)+" ms");
        System.out.println("-------------------------------------------------------------------------------");
        start = System.currentTimeMillis();
        workerCount = Math.min(Runtime.getRuntime().availableProcessors(), 4);
        total = aggregate(elementCount, workerCount);
        System.out.println("Worker: "+workerCount);
        System.out.println("Total: "+total);
        System.out.println("Expected: "+((long)elementCount * (elementCount-1)/2));
        System.out.println("Time took to process: "+ (System.currentTimeMillis()-start)+" ms");
        System.out.println("-------------------------------------------------------------------------------");
        start = System.currentTimeMillis();
        workerCount = Math.min(Runtime.getRuntime().availableProcessors(), 8);
        total = aggregate(elementCount, workerCount);
        System.out.println("Worker: "+workerCount);
        System.out.println("Total: "+total);
        System.out.println("Expected: "+((long)elementCount * (elementCount-1)/2));
        System.out.println("Time took to process: "+ (System.currentTimeMillis()-start)+" ms");
    }

    static long aggregate(int elementCount, int workerCount) throws InterruptedException, ExecutionException {
        long bytes = (long) elementCount * Integer.BYTES;
        // The arena must remain open until all workers finish
        try(Arena arena = Arena.ofShared()){
            MemorySegment data = arena.allocate(bytes);
            for(int i=0; i<elementCount; i++){
                data.setAtIndex(INT, i, i);
            }

            // The executor is closed before the arena.
            try(ExecutorService executor = Executors.newFixedThreadPool(workerCount)){
                List<Callable<Long>> tasks = new ArrayList<>();

                for(int worker = 0; worker <workerCount; worker++){
                    final int workId = worker;

                    long start = (long) worker * elementCount/workerCount;
                    long end = (long) (worker+1)*elementCount/workerCount;

                    tasks.add(() -> {
                        long partialSum = 0;
                        for(long i = start; i < end; i++){
                            partialSum += data.getAtIndex(INT, i);
                        }
                        System.out.printf("Worker %d processed [%d, %d)%n", workId, start, end);
                        return partialSum;
                    });
                }
                List<Future<Long>> futures = executor.invokeAll(tasks);
                long total = 0;
                for(Future<Long> future : futures){
                    total += future.get();
                }
                return total;
            }
        }
    }
}
