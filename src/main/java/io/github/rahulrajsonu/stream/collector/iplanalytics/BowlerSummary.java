package io.github.rahulrajsonu.stream.collector.iplanalytics;

public class BowlerSummary {
    private int runsConceded = 0;
    private int wickets = 0;
    private int ballsBowled = 0;

    public void add(BallEvent e) {
        if (!e.extrasType().equalsIgnoreCase("wides") && !e.extrasType().equalsIgnoreCase("noballs")) {
            ballsBowled++;
        }
        runsConceded += e.totalRuns();
        if (e.isWicket()) wickets++;
    }

    public void combine(BowlerSummary other) {
        runsConceded += other.runsConceded;
        wickets += other.wickets;
        ballsBowled += other.ballsBowled;
    }

    public double economy() {
        return (runsConceded * 6.0) / ballsBowled;
    }

    public int getRunsConceded() {
        return runsConceded;
    }

    public int getWickets() {
        return wickets;
    }

    public int getBallsBowled() {
        return ballsBowled;
    }
}
