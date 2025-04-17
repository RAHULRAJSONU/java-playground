package io.github.rahulrajsonu.stream.collector;


import io.github.rahulrajsonu.stream.Person;

import java.util.stream.Collectors;

public class CollectorsUtilityFunction {
    public static void main(String[] args) {
//        joinNamesInUpperCaseUsingComma();
//        separateEvenAndOddAged();
//        groupTheAgeByTheName();
//        countTheName();
        groupThePersonNameWithLengthGreaterThan2ByAge();
    }

    private static void joinNamesInUpperCaseUsingComma() {
        System.out.println(Person.getPersonListDate().stream()
                .map(Person::name)
                .map(String::toUpperCase)
                .collect(Collectors.joining(",")));
    }

    private static void separateEvenAndOddAged() {
        System.out.println(Person.getPersonListDate().stream()
                .collect(Collectors.partitioningBy(p->p.age() % 2==0)));
    }

    private static void groupTheAgeByTheName() {
        System.out.println(Person.getPersonListDate().stream()
                .collect(Collectors.groupingBy(Person::name,
                        Collectors.mapping(Person::age, Collectors.toSet()))
                )
        );
    }

    private static void countTheName() {
        System.out.println(Person.getPersonListDate().stream()
                .collect(Collectors.groupingBy(Person::name,
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue))));
    }

    private static void groupThePersonNameWithLengthGreaterThan2ByAge() {
        System.out.println(Person.getPersonListDate().stream()
                .collect(Collectors.groupingBy(Person::age,
                        Collectors.mapping(Person::name,
                                Collectors.filtering(n->n.length() > 4,
                                        Collectors.toSet())))));
    }
}
