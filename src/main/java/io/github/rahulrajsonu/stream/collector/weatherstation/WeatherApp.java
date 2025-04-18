package io.github.rahulrajsonu.stream.collector.weatherstation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.github.rahulrajsonu.utility.Utility;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class WeatherApp {
    public static void main(String[] args) {
        List<WeatherReading> readings = List.of(
                new WeatherReading("ST01", LocalDateTime.now(), 25.5, 60.0, 12.3),
                new WeatherReading("ST01", LocalDateTime.now().plusHours(1), 26.2, 62.0, 13.5),
                new WeatherReading("ST02", LocalDateTime.now(), 24.0, 58.0, 10.1),
                new WeatherReading("ST01", LocalDateTime.now().plusDays(1), 23.4, 55.0, 14.8)
        );

        System.out.println(readings.stream().collect(analyze()));
        System.out.println("Station Wise analytics");
        Map<String, WeatherAnalyticsSummary> weatherSummary = readings.stream().collect(Collectors.groupingBy(WeatherReading::stationId, analyze()));
        Utility.printAsPrettyJson(weatherSummary);
    }

    public static Collector<WeatherReading, WeatherAnalyticsSummary, WeatherAnalyticsSummary> analyze(){
        return Collector.of(
                WeatherAnalyticsSummary::new,
                WeatherAnalyticsSummary::add,
                WeatherAnalyticsSummary::combine,
                Collector.Characteristics.UNORDERED,
                Collector.Characteristics.IDENTITY_FINISH
        );
    }
}
