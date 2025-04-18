package io.github.rahulrajsonu.stream.collector.iplanalytics;

public record BallEvent(
    int matchId,
    int inning,
    String battingTeam,
    String bowlingTeam,
    int over,
    int ball,
    String batter,
    String bowler,
    String nonStriker,
    int batsmanRuns,
    int extraRuns,
    int totalRuns,
    String extrasType,
    boolean isWicket,
    String playerDismissed,
    String dismissalKind,
    String fielder
) {}
