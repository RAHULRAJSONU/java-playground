package io.github.rahulrajsonu.stream.collector;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class Joining implements Collector<String, StringBuffer, String> {

    private final String delimiter;

    public Joining(String delimiter){
        this.delimiter=delimiter;
    }

    public Supplier<StringBuffer> supplier(){
        return StringBuffer::new;
    }

    public BiConsumer<StringBuffer, String> accumulator(){
        return (stringBuffer, s) -> {
            if(!stringBuffer.isEmpty()){
                stringBuffer.append(delimiter);
            }
            stringBuffer.append(s);
        };
    }

    public BinaryOperator<StringBuffer> combiner(){
        return StringBuffer::append;
    }

    public Function<StringBuffer, String> finisher(){
        return Object::toString;
    }

    public Set<Characteristics> characteristics(){
        return Set.of();
    }

    public static void main(String[] args) {
        Collection<String> strings = List.of("one", "two","three","four","five");
        String str = strings.stream().collect(new Joining("|"));
        System.out.println(str);
    }
}
