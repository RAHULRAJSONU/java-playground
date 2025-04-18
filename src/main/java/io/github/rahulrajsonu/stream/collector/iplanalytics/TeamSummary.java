package io.github.rahulrajsonu.stream.collector.iplanalytics;

public class TeamSummary {
    private int totalRuns = 0;
    private int totalWickets = 0;
    private int totalFours = 0;
    private int totalSixes = 0;

    void add(BallEvent e) {
        totalRuns += e.totalRuns();
        if (e.isWicket()) totalWickets++;
        if (e.batsmanRuns() == 4) totalFours++;
        if (e.batsmanRuns() == 6) totalSixes++;
    }

    void combine(TeamSummary other) {
        totalRuns += other.totalRuns;
        totalWickets += other.totalWickets;
        totalFours += other.totalFours;
        totalSixes += other.totalSixes;
    }

    public int getTotalRuns() {
        return totalRuns;
    }

    public int getTotalWickets() {
        return totalWickets;
    }

    public int getTotalFours() {
        return totalFours;
    }

    public int getTotalSixes() {
        return totalSixes;
    }
}
