package io.github.rahulrajsonu.stream.collector;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class GroupingByCollectors {

    public static void main(String[] args) {
        List<Student> students = List.of(
                new Student("Alice", "New York", 30),
                new Student("Bob", "London", 25),
                new Student("Charlie", "New York", 35),
                new Student("David", "London", 40)
        );

        System.out.println(students.stream().collect(groupStudentByCity()));
    }

    public static Collector<Student, Map<String, List<Student>>, Map<String, List<Student>>> groupStudentByCity(){
        return new Collector<Student, Map<String, List<Student>>, Map<String, List<Student>>>() {
            @Override
            public Supplier<Map<String, List<Student>>> supplier() {
                return HashMap::new;
            }

            @Override
            public BiConsumer<Map<String, List<Student>>, Student> accumulator() {
                return (map, student)->
                        map.computeIfAbsent(student.city(), l->new ArrayList<>())
                                .add(student);
            }

            @Override
            public BinaryOperator<Map<String, List<Student>>> combiner() {
                return (map1, map2)->{
                    map2.forEach((k,v)->{
                        map1.merge(k,v,
                                (list1,list2)->{list1.addAll(list2); return list1;}
                        );
                    });
                    return map1;
                };
            }

            @Override
            public Function<Map<String, List<Student>>, Map<String, List<Student>>> finisher() {
                return Function.identity();
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Set.of(Characteristics.IDENTITY_FINISH);
            }
        };
    }
}
