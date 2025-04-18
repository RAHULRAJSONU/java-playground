package io.github.rahulrajsonu.stream.collector.weatherstation;

public class WeatherAnalyticsSummary {

    int count = 0;

    private double totalTemperature = 0.0;
    private double minTemperature = Double.MIN_VALUE;
    private double maxTemperature = Double.MAX_VALUE;

    private double totalWindSpeed = 0.0;
    private double minWindSpeed = Double.MIN_VALUE;
    private double maxWindSpeed = Double.MAX_VALUE;

    private double totalHumidity = 0.0;
    private double minHumidity = Double.MIN_VALUE;
    private double maxHumidity = Double.MAX_VALUE;

    void add(WeatherReading reading){
        totalTemperature += reading.temperature();
        minTemperature = Math.min(minTemperature, reading.temperature());
        maxTemperature = Math.max(maxTemperature, reading.temperature());

        totalWindSpeed += reading.windSpeed();
        minWindSpeed = Math.min(minWindSpeed, reading.windSpeed());
        maxWindSpeed = Math.max(maxWindSpeed, reading.windSpeed());

        totalHumidity += reading.humidity();
        minHumidity = Math.min(minHumidity, reading.humidity());
        maxHumidity = Math.max(maxHumidity, reading.humidity());

        count++;
    }

    WeatherAnalyticsSummary combine(WeatherAnalyticsSummary other){
        totalTemperature += other.totalTemperature;
        minTemperature = Math.min(minTemperature, other.minTemperature);
        maxTemperature = Math.max(maxTemperature, other.maxTemperature);

        totalWindSpeed += other.totalWindSpeed;
        minWindSpeed = Math.min(minWindSpeed, other.minWindSpeed);
        maxWindSpeed = Math.max(maxWindSpeed, other.maxWindSpeed);

        totalHumidity += other.totalHumidity;
        minHumidity = Math.min(minHumidity, other.minHumidity);
        maxHumidity = Math.max(maxHumidity, other.maxWindSpeed);

        count += other.count;
        return this;
    }

    public double getTotalTemperature() {
        return totalTemperature;
    }

    public double getMinTemperature() {
        return minTemperature;
    }

    public double getMaxTemperature() {
        return maxTemperature;
    }

    public double getTotalWindSpeed() {
        return totalWindSpeed;
    }

    public double getMinWindSpeed() {
        return minWindSpeed;
    }

    public double getMaxWindSpeed() {
        return maxWindSpeed;
    }

    public double getTotalHumidity() {
        return totalHumidity;
    }

    public double getMinHumidity() {
        return minHumidity;
    }

    public double getMaxHumidity() {
        return maxHumidity;
    }
}
