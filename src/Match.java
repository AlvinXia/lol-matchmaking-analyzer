public class Match {

    private boolean win;
    private String opponentRank;
    private int lpChange;

    public Match(boolean win, String opponentRank, int lpChange) {
        this.win = win;
        this.opponentRank = opponentRank;
        this.lpChange = lpChange;
    }

    public boolean isWin() {
        return win;
    }

    public String getOpponentRank() {
        return opponentRank;
    }

    public int getLpChange() {
        return lpChange;
    }

    public String toCSV() {
        return win + "," + opponentRank + "," + lpChange;
    }

    @Override
    public String toString() {
        return (win ? "Win" : "Loss") + " | "
                + opponentRank + " | LP: " + lpChange;
    }
}
