package io.github.rahulrajsonu.stream.collector;


import io.github.rahulrajsonu.stream.Person;

import java.util.stream.Collectors;

public class Sample {
    public static void main(String[] args) {
        joinNamesInUpperCaseUsingComma();
    }

    private static void joinNamesInUpperCaseUsingComma() {
        System.out.println(Person.getPersonListDate().stream()
                .map(Person::name)
                .map(String::toUpperCase)
                .collect(Collectors.joining(",")));
    }
}
