import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class MatchManager {

    private ArrayList<Match> matches;
    private String filename;

    public MatchManager(String filename) {
        this.matches = new ArrayList<>();
        this.filename = filename;
    }

    public void addMatch(Match match) {
        matches.add(match);
    }

    public ArrayList<Match> getMatches() {
        return matches;
    }

    public void displayMatches() {
        if (matches.isEmpty()) {
            System.out.println("No matches recorded.");
            return;
        }

        for (int i = 0; i < matches.size(); i++) {
            System.out.println((i + 1) + ". " + matches.get(i));
        }
    }

    public void saveMatches() throws IOException {
        try (PrintWriter writer = new PrintWriter(
                new FileWriter(filename))) {

            for (Match match : matches) {
                writer.println(match.toCSV());
            }
        }
    }

    public void loadMatches() throws IOException {
        matches.clear();

        try (BufferedReader reader = new BufferedReader(
                new FileReader(filename))) {

            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                boolean win = Boolean.parseBoolean(parts[0]);
                String rank = parts[1];
                int lpChange = Integer.parseInt(parts[2]);

                matches.add(new Match(win, rank, lpChange));
            }
        }
    }
}