package gui.panel;

import gui.AppContext;
import model.*;
import service.RankingService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

public class LeaderboardPanel extends RefreshablePanel {
    private final AppContext context;
    private final JComboBox<String> sortChoice = new JComboBox<>(new String[]{
            "Win rate", "Level", "Match count", "Custom score", "Explain tie-breaking"
    });
    private final JTextField topCountField = new JTextField("5", 4);
    private final JTextArea outputArea = new JTextArea(16, 50);

    public LeaderboardPanel(AppContext context) {
        this.context = context;
        buildUi();
    }

    private void buildUi() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Rank by:"));
        top.add(sortChoice);
        top.add(new JLabel("Top x:"));
        top.add(topCountField);
        JButton runBtn = new JButton("Show");
        runBtn.addActionListener(e -> doRank());
        top.add(runBtn);

        outputArea.setEditable(false);
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);
    }

    private void doRank() {
        RankingService ranking = context.getRankingService();
        int choice = sortChoice.getSelectedIndex();
        if (choice == 4) {
            outputArea.setText(formatTieBreaking(ranking));
            return;
        }

        int x;
        try {
            x = Integer.parseInt(topCountField.getText().trim());
        } catch (NumberFormatException e) {
            outputArea.setText("Invalid top count.");
            return;
        }

        List<Player> top;
        String label;
        boolean showCustomScore = false;
        switch (choice) {
            case 0:
                top = ranking.getTopPlayersByWinRate(x);
                label = "Win rate";
                break;
            case 1:
                top = ranking.getTopPlayersByLevel(x);
                label = "Level";
                break;
            case 2:
                top = ranking.getTopPlayersByMatchCount(x);
                label = "Match count";
                break;
            case 3:
                top = ranking.getTopPlayersByCustomScore(x);
                label = "Custom score";
                showCustomScore = true;
                break;
            default:
                outputArea.setText("Invalid choice.");
                return;
        }

        StringBuilder sb = new StringBuilder();
        if (showCustomScore) {
            sb.append(formatTieBreaking(ranking)).append("\n");
        }
        sb.append(formatPlayerLeaderboard(top, label, showCustomScore, ranking));
        outputArea.setText(sb.toString());
    }

    static String formatTieBreaking(RankingService ranking) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Tie-Breaking Rules ===\n");
        sb.append("Equipment: same score -> lower equipment ID ranks higher.\n");
        sb.append("Players (win rate / match count): higher level, then higher win rate,\n");
        sb.append("  then lower player ID.\n");
        sb.append("Players (level): higher win rate, then lower player ID.\n");
        sb.append("Players (custom score): higher level, then higher win rate,\n");
        sb.append("  then lower player ID.\n\n");
        sb.append("Default equipment formula: ").append(ranking.getDefaultEquipmentFormula()).append("\n");
        sb.append("  (usage = usageCount, winRate = winRateContribution, heroCount = compatible heroes)\n");
        sb.append("Default player formula: ").append(ranking.getDefaultPlayerFormula()).append("\n");
        sb.append("  (level, winRate, matchCount)\n");
        return sb.toString();
    }

    static String formatPlayerLeaderboard(List<Player> players, String label,
                                          boolean showCustomScore, RankingService ranking) {
        StringBuilder sb = new StringBuilder();
        sb.append("--- Top players by ").append(label).append(" ---\n");
        if (players.isEmpty()) {
            sb.append("No players found.\n");
            return sb.toString();
        }
        int rank = 1;
        for (Player player : players) {
            if (showCustomScore) {
                sb.append(String.format("%d. %s (ID: %d) | Score: %.2f | Level: %d | Win rate: %.2f%% | Matches: %d%n",
                        rank++, player.getName(), player.getId(), ranking.calculatePlayerScore(player),
                        player.getLevel(), player.getWinRate() * 100, player.getMatchCount()));
            } else {
                sb.append(String.format("%d. %s (ID: %d) | Level: %d | Win rate: %.2f%% | Matches: %d%n",
                        rank++, player.getName(), player.getId(), player.getLevel(),
                        player.getWinRate() * 100, player.getMatchCount()));
            }
        }
        return sb.toString();
    }

    @Override
    public void refreshPanel() {
        outputArea.setText("");
    }
}
