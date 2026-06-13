package gui.panel;

import gui.AppContext;
import model.*;
import service.*;

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

public class TeamOverviewPanel extends RefreshablePanel {
    private final AppContext context;
    private final JComboBox<String> searchType = new JComboBox<>(new String[]{"By ID", "By Name"});
    private final JTextField queryField = new JTextField(20);
    private final JTextArea outputArea = new JTextArea(16, 50);

    public TeamOverviewPanel(AppContext context) {
        this.context = context;
        buildUi();
    }

    private void buildUi() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Search:"));
        top.add(searchType);
        top.add(queryField);
        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> doSearch());
        top.add(searchBtn);

        outputArea.setEditable(false);
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);
    }

    private void doSearch() {
        SearchService search = context.getSearchService();
        Team team;
        if (searchType.getSelectedIndex() == 0) {
            try {
                team = search.findTeamById(Integer.parseInt(queryField.getText().trim()));
            } catch (NumberFormatException e) {
                outputArea.setText("Invalid team ID.");
                return;
            }
        } else {
            team = search.findTeamByName(queryField.getText().trim());
        }
        outputArea.setText(formatTeamOverview(team, context.getDataManager()));
    }

    static String formatTeamOverview(Team t, GameDataManager dataManager) {
        if (t == null) {
            return "Team not found.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("=== Team Overview ===\n");
        sb.append("ID: ").append(t.getId()).append("\n");
        sb.append("Name: ").append(t.getName()).append("\n");
        sb.append(String.format("Average Level: %.2f%n", t.getAvgLevel()));
        sb.append("Total Matches: ").append(t.getTotalMatches()).append("\n");
        sb.append(String.format("Win Rate: %.2f%%%n", t.getWinRate() * 100));

        Player topPlayer = dataManager.getPlayerById(t.getTopPlayer());
        if (topPlayer != null) {
            sb.append("Top Player: ").append(topPlayer.getName())
                    .append(" (ID: ").append(topPlayer.getId()).append(")\n");
        } else {
            sb.append("Top Player: None\n");
        }

        sb.append("\nTeam Members (").append(t.getPlayerList().size()).append("):\n");
        if (t.getPlayerList().isEmpty()) {
            sb.append("  None\n");
        } else {
            for (Integer playerId : t.getPlayerList()) {
                Player player = dataManager.getPlayerById(playerId);
                if (player != null) {
                    sb.append("  - ").append(player.getName())
                            .append(" (ID: ").append(playerId)
                            .append(", Level: ").append(player.getLevel()).append(")\n");
                }
            }
        }
        return sb.toString();
    }

    @Override
    public void refreshPanel() {
        outputArea.setText("");
        queryField.setText("");
    }
}
