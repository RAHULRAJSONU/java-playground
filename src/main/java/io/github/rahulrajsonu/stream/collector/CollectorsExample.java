package io.github.rahulrajsonu.stream.collector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.ObjIntConsumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class CollectorsExample {
    public static void main(String[] args) {
        //collectToList();
        collectPrimitiveInAStringBuffer();
    }

    public static void collectToList(){
        Supplier<List<Integer>> supplier = ArrayList::new;
        ObjIntConsumer<List<Integer>> accumulator = Collection::add;
        BiConsumer<List<Integer>, List<Integer>> combiner = Collection::addAll;
        List<Integer> collect = IntStream.range(0, 10)
                .collect(supplier, accumulator, combiner);
        System.out.println(collect);
    }

    public static void collectPrimitiveInAStringBuffer(){
        Supplier<StringBuffer> supplier = StringBuffer::new;
        ObjIntConsumer<StringBuffer> accumulator = StringBuffer::append;
        BiConsumer<StringBuffer, StringBuffer> combiner = StringBuffer::append;
        StringBuffer collected = IntStream.range(0,10)
                .collect(supplier,accumulator,combiner);
        System.out.println(collected);
    }
}
