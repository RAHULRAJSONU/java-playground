package io.github.rahulrajsonu.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.github.rahulrajsonu.stream.collector.weatherstation.WeatherAnalyticsSummary;

import java.util.Map;

public class Utility {
    public static <K,V> void printAsPrettyJson(Map<K, V> summaries) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT); // for pretty print

            String jsonOutput = mapper.writeValueAsString(summaries);
            System.out.println(jsonOutput);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
