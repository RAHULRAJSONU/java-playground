package io.github.rahulrajsonu.stream.gatherer;

import io.github.rahulrajsonu.stream.BlogPost;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StreamGathererDemo {
    public static void main(String[] args) {
//        getPostByCategory(BlogPost.getSampleBlogPosts(),"java").forEach(System.out::println);
        recentPostsByCategoryUsingMapAndTransform(BlogPost.getSampleBlogPosts())
                .forEach((k,v)->System.out.println(k+" -> "+v.size()));
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

    // recent posts by category using gatherers
//    public static Map<String, List<BlogPost>> recentPostsByCategoryUsingStreamGatherersFixedWindow(List<BlogPost> blogPosts){
//        return blogPosts.stream()
//                .gather()
//    }
}
