import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
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

        boolean running = true;

        while (running) {

            System.out.println("\n=== League of Legends Matchmaking Analyzer ===");
            System.out.println("1. Add Match");
            System.out.println("2. View Match History");
            System.out.println("3. View Overall Statistics");
            System.out.println("4. View Statistics by Opponent Rank");
            System.out.println("5. Exit");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine();

            switch (choice) {

                case "1":
                    addMatch(scanner, manager);
                    break;

                case "2":
                    manager.displayMatches();
                    break;

                case "3":
                    displayStatistics(manager);
                    break;

                case "4":
                    displayRankStatistics(scanner, manager);
                    break;

                case "5":
                    running = false;
                    System.out.println("Goodbye!");
                    break;

                default:
                    System.out.println("Invalid option.");
            }
        }

        scanner.close();
    }

    public static void addMatch(Scanner scanner, MatchManager manager) {

        System.out.print("Did you win? (yes/no): ");
        String result = scanner.nextLine().trim();

        if (!result.equalsIgnoreCase("yes") &&
                !result.equalsIgnoreCase("no")) {
            System.out.println("Invalid match result.");
            return;
        }

        boolean win = result.equalsIgnoreCase("yes");

        System.out.print("Opponent rank: ");
        String rank = scanner.nextLine().trim();

        if (rank.isEmpty() || rank.contains(",")) {
            System.out.println("Invalid rank.");
            return;
        }

        System.out.print("LP change: ");

        int lpChange;

        try {
            lpChange = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid LP change.");
            return;
        }

        Match match = new Match(win, rank, lpChange);
        manager.addMatch(match);

        try {
            manager.saveMatches();
            System.out.println("Match saved successfully!");
        } catch (IOException e) {
            System.out.println("Error saving match.");
        }
    }

    public static void displayStatistics(MatchManager manager) {

        ArrayList<Match> matches = manager.getMatches();

        if (matches.isEmpty()) {
            System.out.println("No matches recorded.");
            return;
        }

        System.out.println("\n=== Overall Statistics ===");

        System.out.println("Total Matches: " + matches.size());
        System.out.println("Wins: " + Statistics.getTotalWins(matches));
        System.out.println("Losses: " + Statistics.getTotalLosses(matches));

        System.out.printf("Win Rate: %.2f%%%n",
                Statistics.getWinRate(matches));

        System.out.println("Total LP Change: "
                + Statistics.getTotalLP(matches));

        System.out.printf("Average LP Change: %.2f%n",
                Statistics.getAverageLP(matches));
    }

    public static void displayRankStatistics(
            Scanner scanner, MatchManager manager) {

        System.out.print("Enter opponent rank: ");
        String rank = scanner.nextLine().trim();

        ArrayList<Match> matches = manager.getMatches();

        int count = Statistics.getMatchCountByRank(matches, rank);

        if (count == 0) {
            System.out.println("No matches recorded against " + rank);
            return;
        }

        System.out.println("\n=== Rank Statistics ===");
        System.out.println("Opponent Rank: " + rank);
        System.out.println("Matches Played: " + count);

        System.out.printf("Win Rate: %.2f%%%n",
                Statistics.getWinRateByRank(matches, rank));
    }
}