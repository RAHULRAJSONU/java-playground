package io.github.rahulrajsonu.lombok;

public class LombokDemo {
    public static void main(String[] args) {
        Person person = Person.builder()
                .name("Alice")
                .age(30)
                .build();

        System.out.println("Person: " + person);
        System.out.println("Name: " + person.getName());
        System.out.println("Age: " + person.getAge());

        person.setAge(31);
        System.out.println("Updated Age: " + person.getAge());
    }
}
