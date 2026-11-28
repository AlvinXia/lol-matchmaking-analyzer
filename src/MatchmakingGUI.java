import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicProgressBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class MatchmakingGUI extends JFrame {

    private final Color background = new Color(10, 20, 30);
    private final Color panelColor = new Color(25, 40, 55);
    private final Color gold = new Color(200, 170, 110);
    private final Color green = new Color(80, 210, 150);
    private final Color red = new Color(235, 90, 90);
    private final Color gray = new Color(130, 145, 160);

    private final String[] ranks = {
            "Iron", "Bronze", "Silver", "Gold",
            "Platinum", "Emerald", "Diamond",
            "Master", "Grandmaster", "Challenger"
    };

    private JLabel totalMatchesLabel;
    private JLabel winRateLabel;
    private JLabel totalLPLabel;
    private JLabel averageLPLabel;

    private JComboBox<String> resultBox;
    private JComboBox<String> rankBox;
    private JComboBox<String> filterBox;

    private JTextField lpField;
    private JButton addButton;
    private JButton deleteButton;

    private JTable matchTable;
    private DefaultTableModel tableModel;

    private JProgressBar[] rankBars;
    private JLabel[] rankCountLabels;
    private JLabel[] rankPercentageLabels;

    private MatchManager manager;

    public MatchmakingGUI() {

        setTitle("League of Legends Matchmaking Analyzer");
        setSize(1200, 750);
        setMinimumSize(new Dimension(950, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(background);
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new BorderLayout(10, 15));
        topPanel.setBackground(background);

        JLabel title = new JLabel("MATCHMAKING ANALYZER");
        title.setForeground(gold);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(createStatisticsPanel(), BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setBackground(background);

        centerPanel.add(createHistoryPanel(), BorderLayout.CENTER);
        centerPanel.add(createRankPanel(), BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(createInputPanel(), BorderLayout.SOUTH);

        setContentPane(mainPanel);

        manager = new MatchManager("matches.csv");

        try {
            manager.loadMatches();
        } catch (FileNotFoundException e) {
            System.out.println("Starting new match history.");
        } catch (IOException | RuntimeException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading match history.");

            addButton.setEnabled(false);
            deleteButton.setEnabled(false);
            filterBox.setEnabled(false);
            return;
        }

        addButton.addActionListener(e -> addMatch());
        deleteButton.addActionListener(e -> deleteMatch());
        filterBox.addActionListener(e -> refreshTable());

        refreshDashboard();
    }

    private JPanel createStatisticsPanel() {

        JPanel panel = new JPanel(new GridLayout(1, 4, 10, 10));
        panel.setBackground(background);

        totalMatchesLabel = new JLabel("0");
        winRateLabel = new JLabel("0.00%");
        totalLPLabel = new JLabel("0");
        averageLPLabel = new JLabel("0.00");

        panel.add(createStatCard("Total Matches", totalMatchesLabel));
        panel.add(createStatCard("Win Rate", winRateLabel));
        panel.add(createStatCard("Total LP", totalLPLabel));
        panel.add(createStatCard("Average LP", averageLPLabel));

        return panel;
    }

    private JPanel createStatCard(String title, JLabel value) {

        JPanel card = new JPanel(new GridLayout(2, 1));
        card.setBackground(panelColor);
        card.setBorder(
                BorderFactory.createEmptyBorder(15, 10, 15, 10));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.LIGHT_GRAY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        value.setForeground(gold);
        value.setFont(new Font("SansSerif", Font.BOLD, 26));
        value.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(titleLabel);
        card.add(value);

        return card;
    }

    private JPanel createHistoryPanel() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(background);

        JPanel header = new JPanel(new BorderLayout(10, 10));
        header.setBackground(background);

        JLabel title = new JLabel("MATCH HISTORY");
        title.setForeground(gold);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));

        filterBox = new JComboBox<>();
        filterBox.addItem("All Ranks");

        for (String rank : ranks) {
            filterBox.addItem(rank);
        }

        header.add(title, BorderLayout.WEST);
        header.add(filterBox, BorderLayout.EAST);

        String[] columns = {
                "Match", "Result", "Opponent Rank", "LP Change"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        matchTable = new JTable(tableModel);

        matchTable.setRowHeight(32);
        matchTable.setBackground(panelColor);
        matchTable.setForeground(Color.WHITE);
        matchTable.setGridColor(background);
        matchTable.setFillsViewportHeight(true);
        matchTable.setSelectionBackground(new Color(45, 65, 85));

        matchTable.setSelectionMode(
                javax.swing.ListSelectionModel.SINGLE_SELECTION);

        DefaultTableCellRenderer renderer =
                new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value,
                    boolean isSelected, boolean hasFocus,
                    int row, int column) {

                Component component =
                        super.getTableCellRendererComponent(
                                table, value, isSelected,
                                hasFocus, row, column);

                setHorizontalAlignment(SwingConstants.CENTER);

                component.setForeground(Color.WHITE);

                if (!isSelected) {
                    component.setBackground(panelColor);
                }

                if (column == 1) {

                    if ("Victory".equals(value)) {
                        component.setForeground(green);
                    } else {
                        component.setForeground(red);
                    }
                }

                if (column == 3) {

                    int lp = Integer.parseInt(value.toString());

                    if (lp > 0) {
                        component.setForeground(green);
                    } else if (lp < 0) {
                        component.setForeground(red);
                    }

                    setText(lp > 0 ? "+" + lp : String.valueOf(lp));
                }

                return component;
            }
        };

        for (int i = 0; i < matchTable.getColumnCount(); i++) {
            matchTable.getColumnModel()
                    .getColumn(i).setCellRenderer(renderer);
        }

        JScrollPane scrollPane = new JScrollPane(matchTable);
        scrollPane.getViewport().setBackground(panelColor);

        deleteButton = new JButton("Delete Selected Match");
        deleteButton.setForeground(red);
        deleteButton.setFont(new Font("SansSerif", Font.BOLD, 13));

        panel.add(header, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(deleteButton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createRankPanel() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(background);
        panel.setPreferredSize(new Dimension(330, 0));

        JLabel title = new JLabel("WIN RATE BY RANK");
        title.setForeground(gold);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));

        JPanel barsPanel = new JPanel(
                new GridLayout(ranks.length, 1, 5, 12));

        barsPanel.setBackground(panelColor);
        barsPanel.setBorder(
                BorderFactory.createEmptyBorder(12, 12, 12, 12));

        rankBars = new JProgressBar[ranks.length];
        rankCountLabels = new JLabel[ranks.length];
        rankPercentageLabels = new JLabel[ranks.length];

        for (int i = 0; i < ranks.length; i++) {

            JPanel row = new JPanel(new BorderLayout(5, 5));
            row.setBackground(panelColor);

            JPanel information = new JPanel(new BorderLayout(5, 5));
            information.setBackground(panelColor);

            JLabel rankLabel = new JLabel(ranks[i]);
            rankLabel.setForeground(Color.WHITE);
            rankLabel.setFont(new Font("SansSerif", Font.BOLD, 13));

            rankPercentageLabels[i] = new JLabel("N/A");
            rankPercentageLabels[i].setForeground(gray);
            rankPercentageLabels[i].setFont(
                    new Font("SansSerif", Font.BOLD, 13));

            rankCountLabels[i] = new JLabel("0 games");
            rankCountLabels[i].setForeground(gray);
            rankCountLabels[i].setFont(
                    new Font("SansSerif", Font.PLAIN, 11));

            rankBars[i] = new JProgressBar(0, 100);
            rankBars[i].setUI(new BasicProgressBarUI());
            rankBars[i].setStringPainted(false);
            rankBars[i].setBorderPainted(false);
            rankBars[i].setBackground(new Color(45, 60, 75));
            rankBars[i].setForeground(green);
            rankBars[i].setPreferredSize(new Dimension(200, 9));

            information.add(rankLabel, BorderLayout.WEST);
            information.add(rankPercentageLabels[i], BorderLayout.EAST);

            row.add(information, BorderLayout.NORTH);
            row.add(rankBars[i], BorderLayout.CENTER);
            row.add(rankCountLabels[i], BorderLayout.SOUTH);

            barsPanel.add(row);
        }

        JScrollPane scrollPane = new JScrollPane(barsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);

        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createInputPanel() {

        JPanel panel = new JPanel(new GridLayout(2, 4, 10, 10));
        panel.setBackground(background);

        String[] results = {"Victory", "Defeat"};

        resultBox = new JComboBox<>(results);
        rankBox = new JComboBox<>(ranks);
        lpField = new JTextField();
        addButton = new JButton("Add Match");

        addButton.setFont(new Font("SansSerif", Font.BOLD, 14));

        panel.add(createLabel("Match Result"));
        panel.add(createLabel("Opponent Rank"));
        panel.add(createLabel("LP Change"));
        panel.add(new JLabel(""));

        panel.add(resultBox);
        panel.add(rankBox);
        panel.add(lpField);
        panel.add(addButton);

        return panel;
    }

    private JLabel createLabel(String text) {

        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);

        return label;
    }

    private void addMatch() {

        boolean win = resultBox.getSelectedItem().equals("Victory");
        String rank = (String) rankBox.getSelectedItem();

        int lpChange;

        try {
            lpChange = Integer.parseInt(lpField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid LP change.");
            return;
        }

        if (lpChange == Integer.MIN_VALUE) {
            JOptionPane.showMessageDialog(this,
                    "LP change is outside the valid range.");
            return;
        }

        if (win) {
            lpChange = Math.abs(lpChange);
        } else {
            lpChange = -Math.abs(lpChange);
        }

        Match match = new Match(win, rank, lpChange);

        manager.addMatch(match);

        try {
            manager.saveMatches();
        } catch (IOException e) {
            manager.getMatches().remove(match);

            JOptionPane.showMessageDialog(this,
                    "Error saving match.");
            return;
        }

        lpField.setText("");

        filterBox.setSelectedItem("All Ranks");

        refreshDashboard();
    }

    private void deleteMatch() {

        int selectedRow = matchTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a match to delete.");
            return;
        }

        int modelRow = matchTable.convertRowIndexToModel(selectedRow);

        int matchNumber = (int) tableModel.getValueAt(modelRow, 0);
        int matchIndex = matchNumber - 1;

        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this match?",
                "Delete Match",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        Match removedMatch = manager.getMatches().remove(matchIndex);

        try {
            manager.saveMatches();
        } catch (IOException e) {

            manager.getMatches().add(matchIndex, removedMatch);

            JOptionPane.showMessageDialog(this,
                    "Error deleting match.");
            return;
        }

        refreshDashboard();
    }

    private void refreshDashboard() {

        ArrayList<Match> matches = manager.getMatches();

        totalMatchesLabel.setText(
                String.valueOf(matches.size()));

        double winRate = Statistics.getWinRate(matches);
        int totalLP = Statistics.getTotalLP(matches);
        double averageLP = Statistics.getAverageLP(matches);

        winRateLabel.setText(String.format("%.2f%%", winRate));

        totalLPLabel.setText(
                totalLP > 0 ? "+" + totalLP : String.valueOf(totalLP));

        averageLPLabel.setText(String.format("%+.2f", averageLP));

        if (matches.isEmpty()) {
            winRateLabel.setForeground(gold);
        } else {
            winRateLabel.setForeground(winRate >= 50 ? green : red);
        }

        totalLPLabel.setForeground(
                totalLP == 0 ? gold : totalLP > 0 ? green : red);

        averageLPLabel.setForeground(
                averageLP == 0 ? gold : averageLP > 0 ? green : red);

        refreshTable();
        refreshRankStatistics();
    }

    private String normalizeRank(String rank) {

        rank = rank.trim();

        if (rank.equalsIgnoreCase("plat")) {
            return "Platinum";
        }

        for (String validRank : ranks) {
            if (validRank.equalsIgnoreCase(rank)) {
                return validRank;
            }
        }

        return rank;
    }

    private void refreshTable() {

        ArrayList<Match> matches = manager.getMatches();

        String selectedRank = (String) filterBox.getSelectedItem();

        tableModel.setRowCount(0);

        for (int i = matches.size() - 1; i >= 0; i--) {

            Match match = matches.get(i);
            String rank = normalizeRank(match.getOpponentRank());

            if (!"All Ranks".equals(selectedRank)
                    && !rank.equalsIgnoreCase(selectedRank)) {
                continue;
            }

            tableModel.addRow(new Object[]{
                    i + 1,
                    match.isWin() ? "Victory" : "Defeat",
                    rank,
                    match.getLpChange()
            });
        }
    }

    private void refreshRankStatistics() {

        ArrayList<Match> normalizedMatches = new ArrayList<>();

        for (Match match : manager.getMatches()) {

            normalizedMatches.add(new Match(
                    match.isWin(),
                    normalizeRank(match.getOpponentRank()),
                    match.getLpChange()
            ));
        }

        for (int i = 0; i < ranks.length; i++) {

            String rank = ranks[i];

            int count = Statistics.getMatchCountByRank(
                    normalizedMatches, rank);

            double rate = Statistics.getWinRateByRank(
                    normalizedMatches, rank);

            rankCountLabels[i].setText(
                    count + (count == 1 ? " game" : " games"));

            if (count == 0) {

                rankBars[i].setValue(0);
                rankBars[i].setForeground(gray);

                rankPercentageLabels[i].setText("N/A");
                rankPercentageLabels[i].setForeground(gray);

            } else {

                Color rateColor = rate >= 50 ? green : red;

                rankBars[i].setValue((int) Math.round(rate));
                rankBars[i].setForeground(rateColor);

                rankPercentageLabels[i].setText(
                        String.format("%.1f%%", rate));

                rankPercentageLabels[i].setForeground(rateColor);
            }
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            MatchmakingGUI gui = new MatchmakingGUI();
            gui.setVisible(true);
        });
    }
}