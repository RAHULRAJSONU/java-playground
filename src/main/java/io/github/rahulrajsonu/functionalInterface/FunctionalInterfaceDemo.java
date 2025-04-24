package io.github.rahulrajsonu.functionalInterface;

import io.github.rahulrajsonu.functionalInterface.custom.TriFunction;

import java.util.List;
import java.util.stream.Gatherers;

public class FunctionalInterfaceDemo {
    public static void main(String[] args) {
        List<String> strings = List.of("one", "two", "three", "four", "five", "six", "seven", "eight");
        processString(strings, FunctionalInterfaceDemo::compareAndConcatTheSmallest);
    }

    public static void processString(List<String> list, TriFunction<StringBuilder, String, String, String> processor){
        StringBuilder result = new StringBuilder();
        list.stream()
                .gather(Gatherers.windowFixed(2))
                .forEach(batch->{
                    if(batch.size()<2){
                        processor.apply(result,batch.get(0),"");
                    }else {
                        processor.apply(result, batch.get(0), batch.get(1));
                    }
                });
        System.out.println(result);
    }

    public static String compareAndConcatTheSmallest(StringBuilder result, String str1, String str2){
        if(str1.length() > str2.length()){
            result.append(str2);
        }else {
            result.append(str1);
        }
        return result.toString();
    }
}
