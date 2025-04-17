package io.github.rahulrajsonu.stream;

import java.time.LocalDateTime;
import java.util.List;

public record BlogPost(
    Long id,
    String title,
    String author,
    String content,
    String category,
    LocalDateTime publishedDate
) {
    public static List<BlogPost> getSampleBlogPosts() {
        return List.of(
                new BlogPost(
                        1L,
                        "Understanding Java Records: A Modern Approach to Data Carriers",
                        "Alice Johnson",
                        "Java Records simplify the creation of immutable data carriers. Learn how to use them effectively in your codebase.",
                        "Java",
                        LocalDateTime.of(2025, 4, 10, 9, 30)
                ),
                new BlogPost(
                        2L,
                        "10 Spring Boot Tips You Should Know",
                        "Bob Carter",
                        "Spring Boot makes Java backend development smooth. Here are 10 tips that will improve your workflow.",
                        "Spring Boot",
                        LocalDateTime.of(2025, 4, 8, 14, 15)
                ),
                new BlogPost(
                        3L,
                        "The Rise of Project Loom: Lightweight Concurrency in Java",
                        "Diana Smith",
                        "Project Loom introduces virtual threads to Java. Here's why it matters and how you can start using it.",
                        "Java",
                        LocalDateTime.of(2025, 4, 1, 11, 45)
                ),
                new BlogPost(
                        4L,
                        "Exploring Design Patterns in Modern Java",
                        "Carlos Mendes",
                        "A guide through popular design patterns and how they’re used with Java 17+ features.",
                        "Software Design",
                        LocalDateTime.of(2025, 3, 29, 16, 10)
                ),
                new BlogPost(
                        5L,
                        "How to Build REST APIs with Spring Boot and OpenAPI",
                        "Emily Zhang",
                        "Creating well-documented RESTful APIs is easier with Spring Boot and OpenAPI. Learn the integration steps.",
                        "API Development",
                        LocalDateTime.of(2025, 4, 12, 10, 0)
                ),
                new BlogPost(
                        6L,
                        "Migrating Legacy Java Applications to Java 21",
                        "Raj Patel",
                        "A hands-on walkthrough on how to safely migrate legacy Java applications to modern Java versions.",
                        "Java",
                        LocalDateTime.of(2025, 4, 3, 13, 20)
                ),
                new BlogPost(
                        7L,
                        "Top 5 Libraries Every Java Developer Should Know in 2025",
                        "Sara Kim",
                        "From Apache Commons to Lombok, these libraries can supercharge your productivity.",
                        "Java",
                        LocalDateTime.of(2025, 4, 14, 17, 35)
                ),
                new BlogPost(
                        8L,
                        "Dependency Injection Deep Dive with Spring Framework",
                        "James Lee",
                        "Understand how dependency injection works under the hood and best practices for cleaner code.",
                        "Spring Framework",
                        LocalDateTime.of(2025, 3, 31, 8, 0)
                ),
                new BlogPost(
                        9L,
                        "Kotlin vs Java: Which One Should You Learn in 2025?",
                        "Anita Desai",
                        "Comparing the pros and cons of Kotlin and Java for backend development and Android.",
                        "Programming Languages",
                        LocalDateTime.of(2025, 4, 9, 12, 45)
                ),
                new BlogPost(
                        10L,
                        "Unit Testing in Java: JUnit 5 Features You Shouldn’t Miss",
                        "Liam O’Connor",
                        "Explore the powerful new features in JUnit 5 and how they can help write more maintainable tests.",
                        "Testing",
                        LocalDateTime.of(2025, 4, 6, 15, 0)
                )
        );
    }
}