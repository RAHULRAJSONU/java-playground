package io.github.rahulrajsonu.stream;

public class ReduceExample {
    public static void main(String[] args) {
        Integer totalAge = Person.getPersonListDate().stream()
                .map(Person::age)
                // .reduce(0, (total, age) -> total + age);
                // .reduce(0, (total, age) -> Integer.sum(total,age));
                .reduce(0, Integer::sum);

    }
}

