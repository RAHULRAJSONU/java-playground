package io.github.rahulrajsonu.stream.gatherer;

import io.github.rahulrajsonu.stream.BlogPost;
import io.github.rahulrajsonu.utility.Utility;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class StreamGathererDemo {
    public static void main(String[] args) {
        List<BlogPost> sampleBlogPosts = BlogPost.getSampleBlogPosts();
//        getPostByCategory(BlogPost.getSampleBlogPosts(),"java").forEach(System.out::println);
//        recentPostsByCategoryUsingMapAndTransform(BlogPost.getSampleBlogPosts())
//                .forEach((k,v)->System.out.println(k+" -> "+v.size()));
//        groupByWithLimit(BlogPost.getSampleBlogPosts(), 2);
//        relatedPost(sampleBlogPosts,sampleBlogPosts.getFirst(), 3);
        calculateReadingTime(sampleBlogPosts);
    }

    //Get Post by Category
    public static List<BlogPost> getPostByCategory(List<BlogPost> blogs, String category){
        return blogs.stream()
                .filter(blogPost -> blogPost.category().equalsIgnoreCase(category))
                .sorted(Comparator.comparing(BlogPost::category).reversed())
                .limit(3)
                .toList();
    }

    // recent posts by category
    public static Map<String, List<BlogPost>> recentPostsByCategory(List<BlogPost> blogPosts){
        return blogPosts.stream()
                .collect(Collectors.groupingBy(
                        BlogPost::category,
                        Collectors.collectingAndThen(
                                //Collect posts into a list
                                Collectors.toList(),
                                // Then transform each list by sorting and limiting
                                categoryPosts->categoryPosts.stream()
                                        .sorted(Comparator.comparing(BlogPost::publishedDate).reversed())
                                        .limit(3)
                                        .toList()
                        ))
                );
    }

    // recent posts by category by map and transform
    public static Map<String, List<BlogPost>> recentPostsByCategoryUsingMapAndTransform(List<BlogPost> blogPosts){
        return blogPosts.stream()
                .collect(Collectors.groupingBy(BlogPost::category))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry->entry.getValue().stream()
                                .sorted(Comparator.comparing(BlogPost::publishedDate))
                                .limit(3)
                                .toList()
                ));
    }

    // Gatherers
    public static void groupByWithLimit(List<BlogPost> posts, int limit){
        Map<String, List<BlogPost>> collect = posts.stream()
                .gather(BlogGatherers.groupByWithLimit(
                        BlogPost::category,
                        limit,
                        Comparator.comparing(BlogPost::publishedDate).reversed()
                ))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Utility.printAsPrettyJson(collect);
    }

    public static void relatedPost(List<BlogPost> posts, BlogPost target, int limit){
        List<BlogPost> relatedPosts = posts.stream()
                .gather(BlogGatherers.relatedPosts(target, limit))
                .findFirst()
                .orElse(List.of());
        Utility.printAsPrettyJson(relatedPosts);
    }

    public static void calculateReadingTime(List<BlogPost> posts){
        Map<Long, Duration> durationMap = posts.stream()
                .gather(BlogGatherers.calculateReadingTimes())
                .flatMap(map -> map.entrySet().stream())
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue
                        )
                );
        System.out.println(durationMap);
    }
}
