package io.github.rahulrajsonu.stream.gatherer;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Gatherer;
import java.util.stream.Gatherers;

public class StreamGatherersExample {
    public static void main(String[] args) {
//        evaluate(
//                List.of("one","two","three").iterator(),
//                Gatherer.of(
//                        (_,element, downstream)
//                                ->downstream.push(element.toUpperCase())
//                ),
//                System.out::println
//        );
//        foldExample(List.of(1,2,3,4,5,6));
//        scanExample(List.of(1,2,3,4,5,6));
        windowExample(List.of(1,2,3,4,5,6));
    }


    static <T, A, R> void evaluate(
            Iterator<T> source,
            Gatherer<T, A, R> gatherer,
            Consumer<R> output){
        Gatherer.Downstream<R> downstream = (R r)->{output.accept(r); return true;};
        var state = gatherer.initializer().get();
        var integrator = gatherer.integrator();
        while (source.hasNext() && integrator.integrate(state, source.next(), downstream)){}
        gatherer.finisher().accept(state, downstream);
    }

    static void foldExample(List<Integer> source){
        Optional<String> stringInts = source.stream()
                .gather(
                        Gatherers.fold(() -> "", (string, i) -> string + i)
                ).findFirst();
        System.out.println(stringInts);
        Optional<Integer> sumNumbers = source.stream()
                .gather(
                        Gatherers.fold(() -> 0, (sum, i) -> sum + i)
                ).findFirst();
        System.out.println(sumNumbers);
    }

    static void scanExample(List<Integer> source){
        System.out.println(
                source.stream()
                        .gather(
                                Gatherers.scan(
                                        ()->"",
                                        (string, number)->string+number
                                )
                        ).toList()
        );
        System.out.println(
                source.stream()
                        .gather(
                                Gatherers.scan(
                                        ()->0,
                                        (sum, number)->sum+number
                                )
                        ).toList()
        );
    }

    static void windowExample(List<Integer> source){
        System.out.println(
                source.stream()
                        .gather(
                                Gatherers.windowFixed(3)
                        ).toList()
        );

        System.out.println(
                source.stream()
                        .gather(
                                Gatherers.windowFixed(2)
                        ).toList()
        );
    }

}
