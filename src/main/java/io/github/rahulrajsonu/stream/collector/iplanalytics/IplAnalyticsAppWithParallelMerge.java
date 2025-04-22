package io.github.rahulrajsonu.stream.collector.iplanalytics;

import io.github.rahulrajsonu.utility.Utility;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.BiFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class IplAnalyticsAppWithParallelMerge {

    private static final int BATCH_SIZE = 10_000;
    private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        final long startTime = System.currentTimeMillis();
        String filePath = "C:/Users/Development/Documents/Workspace/datasets/deliveries.csv";

        Comparator<Map.Entry<String, BowlerSummary>> byWicketsDesc =
                Map.Entry.comparingByValue(Comparator.comparingInt(BowlerSummary::getWickets).reversed());

        Map<String, BowlerSummary> analytics = loadAndAnalyzeParallel(
                filePath,
                IplAnalyticsAppWithParallelMerge::computeBowlerStats,
                true,
                byWicketsDesc
        );

        Utility.printAsPrettyJson(analytics);
        System.out.println("Processed in total: "+(System.currentTimeMillis()-startTime));
    }

    public static Map<String, BowlerSummary> loadAndAnalyzeParallel(
            String path,
            BiFunction<List<BallEvent>, Comparator<Map.Entry<String, BowlerSummary>>, Map<String, BowlerSummary>> analyzer,
            boolean skipHeader,
            Comparator<Map.Entry<String, BowlerSummary>> comparator) throws IOException, InterruptedException, ExecutionException {

        Path filePath = Paths.get(path);
        long fileSize = Files.size(filePath);

        ConcurrentHashMap<String, BowlerSummary> store = new ConcurrentHashMap<>();

        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        List<Future<?>> futures = new ArrayList<>();

        try (FileChannel channel = FileChannel.open(filePath, StandardOpenOption.READ)) {
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, fileSize);

            StringBuilder lineBuilder = new StringBuilder(200);
            List<BallEvent> batch = new ArrayList<>(BATCH_SIZE);
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

                    if (batch.size() >= BATCH_SIZE) {
                        List<BallEvent> batchCopy = new ArrayList<>(batch);
                        futures.add(executor.submit(() -> {
                            Map<String, BowlerSummary> partialResult = analyzer.apply(batchCopy, comparator);
                            partialResult.forEach((key, value) -> store.merge(key, value, (existing, incoming) -> {
                                existing.combine(incoming);
                                return existing;
                            }));
                        }));
                        batch.clear();
                    }
                } else {
                    lineBuilder.append(c);
                }
            }

            // Remaining records
            if (!batch.isEmpty()) {
                List<BallEvent> batchCopy = new ArrayList<>(batch);
                futures.add(executor.submit(() -> {
                    Map<String, BowlerSummary> partialResult = analyzer.apply(batchCopy, comparator);
                    partialResult.forEach((key, value) -> store.merge(key, value, (existing, incoming) -> {
                        existing.combine(incoming);
                        return existing;
                    }));
                }));
            }
        }

        // Wait for all tasks to finish
        for (Future<?> f : futures) f.get();

        executor.shutdown();

        // Final sort and top-5 selection
        return store.entrySet().stream()
                .sorted(comparator)
                .limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
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
}
