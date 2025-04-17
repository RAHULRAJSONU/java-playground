package io.github.rahulrajsonu.stream;

import java.util.List;

public record Person(String name, int age) {
    public static List<Person> getPersonListDate(){
        return List.of(
                new Person("Sara", 20),
                new Person("Sara", 22),
                new Person("Bob", 20),
                new Person("Paula", 32),
                new Person("Paul", 32),
                new Person("Jack", 3),
                new Person("Jack", 72),
                new Person("Jill", 11),
                new Person("John", 25),
                new Person("Smith", 26)
        );
    }
}
