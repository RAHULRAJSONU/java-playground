package io.github.rahulrajsonu.stream.collector.iplanalytics;

import io.github.rahulrajsonu.utility.Utility;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IplAnalyticsAppBasic {
    public static void main(String[] args) {
        List<BallEvent> events = loadDataset("C:/Users/Development/Documents/Workspace/datasets/deliveries.csv");
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
                .sorted(Map.Entry.comparingByValue(Comparator.comparing(BowlerSummary::getWickets).reversed()))
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (a,b)->a,
                                LinkedHashMap::new)
                )
                .entrySet()
                .stream()
                .limit(5)
                .collect(
                Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a,b)->a,
                        LinkedHashMap::new)
        );
        Utility.printAsPrettyJson(ballerAnalytics);
    }

    public static List<BallEvent> loadDataset(String path) {
        try(Stream<String> lines = Files.lines(Path.of(path))) {
                return lines.skip(1)
                        .map(line->{
                            try {
                                String[] columns = line.split(",");
                                return new BallEvent(
                                        Integer.parseInt(columns[0]),
                                        Integer.parseInt(columns[1]),
                                        columns[2],
                                        columns[3],
                                        Integer.parseInt(columns[4]),
                                        Integer.parseInt(columns[5]),
                                        columns[6],
                                        columns[7],
                                        columns[8],
                                        Integer.parseInt(columns[9]),
                                        Integer.parseInt(columns[10]),
                                        Integer.parseInt(columns[11]),
                                        columns[12],
                                        Integer.parseInt(columns[13])==1,
                                        columns[14],
                                        columns[15],
                                        columns[16]

                                );
                            }catch (Exception e){
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
