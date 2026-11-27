import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        MatchManager manager = new MatchManager("matches.csv");

        try {
            manager.loadMatches();
            System.out.println("Match history loaded.");
        } catch (FileNotFoundException e) {
            System.out.println("Starting new match history.");
        } catch (IOException e) {
            System.out.println("Error loading matches.");
            scanner.close();
            return;
        }

        System.out.println("=== League of Legends Matchmaking Analyzer ===");

        System.out.print("Did you win? (yes/no): ");
        String result = scanner.nextLine().trim();

        if (!result.equalsIgnoreCase("yes") &&
                !result.equalsIgnoreCase("no")) {
            System.out.println("Invalid match result.");
            scanner.close();
            return;
        }

        boolean win = result.equalsIgnoreCase("yes");

        System.out.print("Opponent rank: ");
        String rank = scanner.nextLine().trim();

        if (rank.isEmpty() || rank.contains(",")) {
            System.out.println("Invalid rank.");
            scanner.close();
            return;
        }

        System.out.print("LP change: ");

        if (!scanner.hasNextInt()) {
            System.out.println("Invalid LP change.");
            scanner.close();
            return;
        }

        int lpChange = scanner.nextInt();

        Match match = new Match(win, rank, lpChange);
        manager.addMatch(match);

        try {
            manager.saveMatches();
            System.out.println("Match saved successfully!");
        } catch (IOException e) {
            System.out.println("Error saving match.");
        }

        System.out.println("\n=== Match History ===");
        manager.displayMatches();

        scanner.close();
    }
}