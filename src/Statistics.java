import java.util.ArrayList;

public class Statistics {

    public static int getTotalWins(ArrayList<Match> matches) {
        int wins = 0;

        for (Match match : matches) {
            if (match.isWin()) {
                wins++;
            }
        }

        return wins;
    }

    public static int getTotalLosses(ArrayList<Match> matches) {
        return matches.size() - getTotalWins(matches);
    }

    public static double getWinRate(ArrayList<Match> matches) {
        if (matches.isEmpty()) {
            return 0;
        }

        return (double) getTotalWins(matches)
                / matches.size() * 100;
    }

    public static int getTotalLP(ArrayList<Match> matches) {
        int total = 0;

        for (Match match : matches) {
            total += match.getLpChange();
        }

        return total;
    }

    public static double getAverageLP(ArrayList<Match> matches) {
        if (matches.isEmpty()) {
            return 0;
        }

        return (double) getTotalLP(matches) / matches.size();
    }

        public static double getWinRateByRank(
            ArrayList<Match> matches, String rank) {

        int wins = 0;
        int total = 0;

        for (Match match : matches) {
            if (match.getOpponentRank().equalsIgnoreCase(rank)) {
                total++;

                if (match.isWin()) {
                    wins++;
                }
            }
        }

        if (total == 0) {
            return 0;
        }

        return (double) wins / total * 100;
    }

    public static int getMatchCountByRank(
            ArrayList<Match> matches, String rank) {

        int count = 0;

        for (Match match : matches) {
            if (match.getOpponentRank().equalsIgnoreCase(rank)) {
                count++;
            }
        }

        return count;
    }
}