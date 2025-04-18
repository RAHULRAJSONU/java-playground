package io.github.rahulrajsonu.stream.collector;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class ToSet<T> implements Collector<T, Set<T>, Set<T>> {

    public Supplier<Set<T>> supplier(){
        return HashSet::new;
    }

    public BiConsumer<Set<T>, T> accumulator(){
        return Collection::add;
    }

    public BinaryOperator<Set<T>> combiner(){
        return (list1, list2)->{list1.addAll(list2);return list1;};
    }

    public Function<Set<T>, Set<T>> finisher(){
        return Function.identity();
    }

    public Set<Characteristics> characteristics(){
        return Set.of(Characteristics.IDENTITY_FINISH);
    }

    public static void main(String[] args) {
        Collection<String> strings = List.of("one", "two","three","four","five");
        Set<String> result = strings.stream().collect(new ToSet<>());
        System.out.println("result = "+result);
    }
}
