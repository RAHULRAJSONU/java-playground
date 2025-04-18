package io.github.rahulrajsonu.functionalInterface;

import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SupplierDemo {

    public static void main(String[] args) throws InterruptedException {
        Supplier<Integer> integerSupplier = randomIntSupplier(70, 80);
        Supplier<Integer> memoizedRandom = memoize(integerSupplier);
        Supplier<Integer> threadSafeMemoizedRandom = threadSafeMemoize(integerSupplier);

        //for (int i = 0; i < 10; i++){
            //System.out.println(integerSupplier.get());
            //System.out.println(memoizedRandom.get());
        //}
        final Long started = System.currentTimeMillis();
        try(ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<Integer>> values = executorService.invokeAll(
                    IntStream.range(0, 100000)
                            .parallel()
                            .mapToObj(in -> {
                                try {
                                    Thread.sleep(10);
                                }catch (InterruptedException _){

                                }
                                return (Callable<Integer>) threadSafeMemoizedRandom::get;
                            })
                            .toList()
            );
            values.stream()
                    .map(fi-> {
                        try {
                            return fi.get();
                        } catch (InterruptedException | ExecutionException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toSet())
                    .forEach(System.out::println);
        }
        System.out.println("Completed 100000 tasks in "+(System.currentTimeMillis()-started)+ "Milliseconds");
    }

    public static Supplier<Integer> randomIntSupplier(final int from, final int to){
        return () -> new Random().nextInt(from, to);
    }

    public static <T> Supplier<T> memoize(Supplier<T> original){
        final Object[] cache = new Object[1];
        return ()->{
            if(cache[0]==null){
                cache[0] = original.get();
            }
            return (T) cache[0];
        };
    }

    public static <T> Supplier<T> threadSafeMemoize(Supplier<T> original){
        final Object lock = new Object();
        final Object[] cache = new Object[1];
        return ()->{
            if(cache[0]==null){
                synchronized (lock){
                    if(cache[0]==null)cache[0] = original.get();
                }
            }
            return (T) cache[0];
        };
    }
}
