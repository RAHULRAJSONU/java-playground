package io.github.rahulrajsonu.functionalInterface;

import java.util.function.BiFunction;
import java.util.function.Function;

public class BiFunctionDemo {
    public static void main(String[] args) {
        BiFunction<Integer, Integer, Integer> add = Integer::sum;
        System.out.println(add.apply(5, 5));
        System.out.println(add.andThen(Integer::doubleValue).apply(5,5));
    }
}
