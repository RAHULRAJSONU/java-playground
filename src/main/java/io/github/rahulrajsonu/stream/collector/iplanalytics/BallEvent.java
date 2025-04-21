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
) {
    public static BallEvent parse(String line){
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
    }

}
