package io.github.rahulrajsonu.stream.collector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class ToList<T> implements Collector<T, List<T>, List<T>> {

    public Supplier<List<T>> supplier(){
        return ArrayList::new;
    }

    public BiConsumer<List<T>, T> accumulator(){
        return Collection::add;
    }

    public BinaryOperator<List<T>> combiner() {
        return (list1, list2)->{list1.addAll(list2); return list1;};
    }

    public Function<List<T>, List<T>> finisher(){
        return Function.identity();
    }

    public Set<Characteristics> characteristics(){
        return Set.of(Characteristics.IDENTITY_FINISH);
    }

    public static void main(String[] args) {
        Collection<String> strings = List.of("one", "two","three","four","five");
        List<String> result = strings.stream().collect(new ToList<>());
        System.out.println("result = "+result);
    }
}
