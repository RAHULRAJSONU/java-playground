package io.github.rahulrajsonu.functionalInterface;

import java.util.Random;
import java.util.function.Supplier;

public class SupplierDemo {

    public static void main(String[] args) {
        Supplier<Integer> integerSupplier = randomIntSupplier(70, 80);
        Supplier<Integer> memoizedRandom = memoize(integerSupplier);
        for (int i = 0; i < 10; i++){
            //System.out.println(integerSupplier.get());
            System.out.println(memoizedRandom.get());
        }
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
                    cache[0] = original.get();
                }
            }
            return (T) cache[0];
        };
    }
}
