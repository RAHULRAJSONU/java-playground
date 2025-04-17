package io.github.rahulrajsonu.stream.collector;

import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class EmployeeCollector implements
        Collector<Employee,
                  List<Triplet<Integer, String, String>>,
                  List<Triplet<Integer, String, String>>> {

    public static EmployeeCollector toEmployeeList(){
        return new EmployeeCollector();
    }

    @Override
    public Supplier<List<Triplet<Integer, String, String>>> supplier() {
        return ArrayList::new;
    }

    @Override
    public BiConsumer<List<Triplet<Integer, String, String>>, Employee> accumulator() {
        return (list, employee)->
                list.add(
                        Triplet.with(employee.year(), employee.firstName(), employee.lastName())
                );
    }

    @Override
    public BinaryOperator<List<Triplet<Integer, String, String>>> combiner() {
        return (list1, list2)->{
            list1.addAll(list2);
            return list1;
        };
    }

    @Override
    public Function<List<Triplet<Integer, String, String>>, List<Triplet<Integer, String, String>>> finisher() {
        return Collections::unmodifiableList;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of(Characteristics.UNORDERED);
    }
}
