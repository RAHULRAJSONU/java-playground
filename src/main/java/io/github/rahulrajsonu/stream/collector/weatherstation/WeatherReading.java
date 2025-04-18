package io.github.rahulrajsonu.stream.collector.weatherstation;

import java.time.LocalDateTime;

public record WeatherReading (
        String stationId,
        LocalDateTime timestamp,
        double temperature,
        double humidity,
        double windSpeed){ }
