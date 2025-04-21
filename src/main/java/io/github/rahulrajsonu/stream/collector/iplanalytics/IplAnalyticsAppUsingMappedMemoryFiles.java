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

        Comparator<Map.Entry<String, BowlerSummary>> byWicketsDesc =
                Map.Entry.comparingByValue(Comparator.comparingInt(BowlerSummary::getWickets).reversed());

        Map<String, BowlerSummary> analytics = loadAndAnalyze(
                filePath,
                IplAnalyticsAppUsingMappedMemoryFiles::computeBowlerStats,
                true,
                byWicketsDesc
        );

        Utility.printAsPrettyJson(analytics);
    }

    private static Map<String, BowlerSummary> computeBowlerStats(
            List<BallEvent> events,
            Comparator<Map.Entry<String, BowlerSummary>> comparator) {

        return events.stream()
                .collect(Collectors.groupingBy(
                        BallEvent::bowler,
                        Collector.of(
                                BowlerSummary::new,
                                BowlerSummary::add,
                                (l, r) -> {
                                    l.combine(r);
                                    return l;
                                },
                                Collector.Characteristics.IDENTITY_FINISH
                        )
                ));
    }

    public static Map<String, BowlerSummary> loadAndAnalyze(
            String path,
            BiFunction<List<BallEvent>, Comparator<Map.Entry<String, BowlerSummary>>, Map<String, BowlerSummary>> analyzer,
            boolean skipHeader,
            Comparator<Map.Entry<String, BowlerSummary>> comparator) throws IOException {

        Path filePath = Paths.get(path);
        long fileSize = Files.size(filePath);
        int batchSize = 10_000;

        Map<String, BowlerSummary> aggregatedStats = new LinkedHashMap<>();

        try (FileChannel channel = FileChannel.open(filePath, StandardOpenOption.READ)) {
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, fileSize);

            StringBuilder lineBuilder = new StringBuilder(200);
            List<BallEvent> batch = new ArrayList<>(batchSize);
            boolean isHeader = skipHeader;

            while (buffer.hasRemaining()) {
                char c = (char) buffer.get();
                if (c == '\n') {
                    String line = lineBuilder.toString();
                    lineBuilder.setLength(0);

                    if (isHeader) {
                        isHeader = false;
                        continue;
                    }

                    BallEvent event = BallEvent.parse(line);
                    if (event != null) batch.add(event);

                    if (batch.size() >= batchSize) {
                        merge(analyzer.apply(batch, comparator), aggregatedStats);
                        batch.clear();
                    }
                } else {
                    lineBuilder.append(c);
                }
            }

            // Remaining records
            if (!batch.isEmpty()) {
                merge(analyzer.apply(batch, comparator), aggregatedStats);
            }
        }

        // Final sorting & top 5 selection
        return aggregatedStats.entrySet().stream()
                .sorted(comparator)
                .limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    private static void merge(Map<String, BowlerSummary> batchResult, Map<String, BowlerSummary> store) {
        batchResult.forEach((key, value) ->
                store.merge(key, value, (existing, incoming) -> {
                    existing.combine(incoming);
                    return existing;
                }));
    }
}

