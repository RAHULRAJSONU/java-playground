package io.github.rahulrajsonu.stream.collector.iplanalytics;

import io.github.rahulrajsonu.utility.Utility;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class IplAnalyticsAppUsingMappedMemoryFiles {
    public static void main(String[] args) throws IOException {
        String filePath = "C:/Users/Development/Documents/Workspace/datasets/deliveries.csv";
        Comparator<Map.Entry<String, BowlerSummary>> bowlerComparator = Map.Entry.comparingByValue(Comparator.comparing(BowlerSummary::getWickets).reversed());
        Utility.printAsPrettyJson(loadAndAnalyze(filePath, IplAnalyticsAppUsingMappedMemoryFiles::getBallerAnalytics, Boolean.TRUE, bowlerComparator));
    }

    private static Map<String, BowlerSummary> getBallerAnalytics(List<BallEvent> events, Comparator<Map.Entry<String, BowlerSummary>> bowlerComparator) {
        Map<String, BowlerSummary> ballerAnalytics = events.stream()
                .parallel()
                .collect(
                        Collectors.groupingBy(
                                BallEvent::bowler,
                                Collector.of(
                                        BowlerSummary::new,
                                        BowlerSummary::add,
                                        (l,r)->{l.combine(r);return l;},
                                        Collector.Characteristics.IDENTITY_FINISH
                                )
                        )
                )
                .entrySet()
                .stream()
                .sorted(bowlerComparator)
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (a,b)->a,
                                LinkedHashMap::new)
                )
                .entrySet()
                .stream()
                .collect(
                Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a,b)->a,
                        LinkedHashMap::new)
        );
        return ballerAnalytics;
    }

    public static Map<String, BowlerSummary> loadAndAnalyze(
            String path,
            BiFunction<List<BallEvent>, Comparator<Map.Entry<String, BowlerSummary>>, Map<String, BowlerSummary>> analyzer,
            boolean skipHeader,
            Comparator<Map.Entry<String, BowlerSummary>> bowlerComparator) throws IOException {
        Path filePath = Paths.get(path);
        long fileSize = Files.size(filePath);
        var batchSize = 10_000;
        Map<String, BowlerSummary> store = new LinkedHashMap<>();
        try (FileChannel fileChannel = FileChannel.open(filePath, StandardOpenOption.READ)) {
            MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileSize);

            // Line reading variables
            StringBuilder lineBuilder = new StringBuilder();
            List<BallEvent> eventBatch = new ArrayList<>();

            while (buffer.hasRemaining()) {
                char c = (char) buffer.get();
                if (c == '\n') {
                    String line = lineBuilder.toString();
                    if(skipHeader){
                        skipHeader = Boolean.FALSE;
                        lineBuilder.setLength(0);
                        continue;
                    }
                    BallEvent event = BallEvent.parse(line);
                    if (event != null) eventBatch.add(event);

                    // Process batch when enough records are collected (say 10,000)
                    if (eventBatch.size() >= batchSize) {
                        Map<String, BowlerSummary> partialBatchResult = analyzer.apply(eventBatch, bowlerComparator);
                        merge(partialBatchResult, store, bowlerComparator);
                        partialBatchResult.clear();
                        eventBatch.clear();
                    }
                    lineBuilder.setLength(0);
                } else {
                    lineBuilder.append(c);
                }
            }

            // process remaining records
            if (!eventBatch.isEmpty()) {
                merge(analyzer.apply(eventBatch, bowlerComparator), store, bowlerComparator);
            }
        }
        return store.entrySet()
                .stream()
                .sorted(bowlerComparator)
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (m1,m2)->m1,
                                LinkedHashMap::new
                        ),
                        m->
                                m.entrySet()
                                        .stream()
                                        .limit(5)
                                        .collect(Collectors.toMap(
                                                Map.Entry::getKey,
                                                Map.Entry::getValue,
                                                (m1,m2)->m1,
                                                LinkedHashMap::new
                                        ))
                ));
    }

    private static void merge(
            Map<String, BowlerSummary> partialBatchResult,
            Map<String, BowlerSummary> store,
            Comparator<Map.Entry<String, BowlerSummary>> bowlerComparator) {
        partialBatchResult.forEach((key, value) -> store.merge(
                key,
                value,
                (s, p) -> {
                    s.combine(p);
                    return s;
                }));
    }
}
