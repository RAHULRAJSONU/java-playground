package io.github.rahulrajsonu.stream.gatherer;

import io.github.rahulrajsonu.stream.BlogPost;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Gatherer;

public final class BlogGatherers {

    private BlogGatherers(){}

    /**
     * Returns a gatherer that groups blog posts by a provided key and limits
     * each group to the specified number of posts, sorted by the given comparator.
     *
     * @param keyExtractor function to extract the grouping key from each blog post
     * @param limit maximum number of posts to keep per group
     * @param comparator comparator for sorting posts within each group
     * @param <K> the type of keys for grouping
     * @return a gatherer that produces map entries with keys and limited lists of posts
     */
    public static <K> Gatherer<BlogPost, Map<K, List<BlogPost>>, Map.Entry<K, List<BlogPost>>> groupByWithLimit(
            Function<? super BlogPost, ? extends K> keyExtractor,
            int limit,
            Comparator<? super BlogPost> comparator){

        return Gatherer.of(
                HashMap::new, // initializer

                (map, post, downstream)->{ // Integrator
                    K key = keyExtractor.apply(post);
                    map.computeIfAbsent(key, k->new ArrayList<>()).add(post);
                    return true;
                },

                (map1,map2)->map1, // combiner

                (map,downstream)->{  //finisher
                    map.forEach((key, posts)->{
                        List<BlogPost> limitedPosts = posts.stream()
                                .sorted(comparator)
                                .limit(limit)
                                .toList();
                        downstream.push(Map.entry(key, limitedPosts));
                    });
                }
        );
    }

    /**
     * Returns a gatherer that finds posts related to the target post
     * based on category and content similarity.
     */
    public static Gatherer<BlogPost,?,List<BlogPost>> relatedPosts(BlogPost targetPost, int limit){
        return Gatherer.ofSequential(
                ()->new HashMap<String, List<BlogPost>>(), // initializer

                (map, post, downstream)->{ //Integrator
                    if(!post.id().equals(targetPost.id())){
                        map.computeIfAbsent(post.category(), k->new ArrayList<>()).add(post);
                    }
                    return true;
                },

                (map, downstream)->{ //Finisher

                    List<BlogPost> sameCategoryPosts = map.getOrDefault(targetPost.category(), List.of());

                    List<BlogPost> relatedPosts = sameCategoryPosts.stream()
                            .map(post->Map.entry(post, calculateSimilarity(targetPost, post)))
                            .sorted(Map.Entry.<BlogPost, Double> comparingByValue().reversed())
                            .limit(limit)
                            .map(Map.Entry::getKey)
                            .toList();
                    downstream.push(relatedPosts);
                }
        );
    }

    /**
     * Returns a gatherer that extracts and counts hashtags from blog post content.
     */
    public static Gatherer<BlogPost, ? , Map<String, Integer>> extractTags(){
        return Gatherer.ofSequential(
                //initializer
                ()-> new HashMap<String, Integer>(),
                //integrator
                (tagCount, post, downstream)->{
                    String content = post.content().toLowerCase();
                    Pattern pattern = Pattern.compile("#(\\w+)");
                    Matcher matcher = pattern.matcher(content);

                    while (matcher.find()){
                        String tag = matcher.group(1);
                        tagCount.merge(tag, 1, Integer::sum);
                    }

                    return true;
                },
                // finisher
                (tagCount, downstream)->{
                    downstream.push(tagCount);
                }
        );
    }

    /**
     * Returns a gatherer that calculates estimated reading times for blog posts.
     */
    public static Gatherer<BlogPost, ?, Map<Long, Duration>> calculateReadingTimes(){
        final int WORDS_PER_MINUTES = 200;
        return Gatherer.ofSequential(
                // Initializer
                ()->new HashMap<Long, Duration>(),
                // Integrator
                (durationMap, post, downstream)->{
                    String content = post.content();
                    int wordCount = content.split("\\s+").length;
                    double minutes = (double) wordCount/WORDS_PER_MINUTES;
                    Duration readingTime = Duration.ofSeconds(Math.round(minutes*60));
                    durationMap.put(post.id(), readingTime);
                    return true;
                },
                // Finisher
                (durationMap, downstream)->{
                    downstream.push(durationMap);
                }
        );
    }

    /**
     * Returns a gatherer that finds the most prolific authors.
     */
    public static Gatherer<BlogPost, ? , List<Map.Entry<String, Integer>>> popularAuthors(int limit){
        return Gatherer.ofSequential(
                ()->new HashMap<String, Integer>(),
                (authorCounts, post, downstream)->{
                    authorCounts.merge(post.author(),1, Integer::sum);
                    return true;
                },
                (authorCounts, downstream)->{
                    List<Map.Entry<String, Integer>> topAuthors = authorCounts.entrySet().stream()
                            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                            .limit(limit)
                            .toList();
                    downstream.push(topAuthors);
                }
        );
    }

    /**
     * Returns a gatherer that groups posts by month for an archive view.
     */
    public static Gatherer<BlogPost, ?, Map<YearMonth,List<BlogPost>>> monthlyArchive(){
        return Gatherer.ofSequential(
                //Initializer
                ()->new TreeMap<YearMonth, List<BlogPost>>(Comparator.reverseOrder()),
                //Integrator
                (monthlyArchive, post, downstream)->{
                    LocalDateTime publishedOn = post.publishedDate();
                    YearMonth yearMonth = YearMonth.from(publishedOn);
                    monthlyArchive.computeIfAbsent(yearMonth, k->new ArrayList<>()).add(post);
                    return true;
                },
                //Finisher
                (monthlyArchive, downstream)->{
                    monthlyArchive.forEach((month,posts)->
                            posts.sort(Comparator.comparing(BlogPost::publishedDate).reversed()));
                    downstream.push(monthlyArchive);
                }
        );
    }

    // Helper method for calculating similarity between posts
    private static double calculateSimilarity(BlogPost post1, BlogPost post2) {
        Set<String> words1 = new HashSet<>(Arrays.asList(post1.title().toLowerCase().split("\\W+")));
        Set<String> words2 = new HashSet<>(Arrays.asList(post2.title().toLowerCase().split("\\W+")));

        Set<String> intersection = new HashSet<>(words1);
        intersection.retainAll(words2);

        return (double) intersection.size() / Math.max(words1.size(), words2.size());
    }
}
