package io.github.rahulrajsonu.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.rahulrajsonu.stream.collector.weatherstation.WeatherAnalyticsSummary;

import java.util.Map;

public class Utility {
    public static <K,V> void printAsPrettyJson(Object summaries) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.enable(SerializationFeature.INDENT_OUTPUT); // for pretty print

            String jsonOutput = mapper.writeValueAsString(summaries);
            System.out.println(jsonOutput);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
