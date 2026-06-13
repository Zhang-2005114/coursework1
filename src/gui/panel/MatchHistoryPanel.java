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
import java.util.List;
import java.util.Map;

public class MatchHistoryPanel extends RefreshablePanel {
    private final AppContext context;
    private final JComboBox<String> queryType = new JComboBox<>(new String[]{"By player", "By team"});
    private final JComboBox<String> searchType = new JComboBox<>(new String[]{"By ID", "By Name"});
    private final JTextField queryField = new JTextField(12);
    private final JTextField countField = new JTextField("3", 4);
    private final JTextArea outputArea = new JTextArea(16, 50);

    public MatchHistoryPanel(AppContext context) {
        this.context = context;
        buildUi();
    }

    private void buildUi() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Query:"));
        top.add(queryType);
        top.add(searchType);
        top.add(queryField);
        top.add(new JLabel("Recent n:"));
        top.add(countField);
        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> doSearch());
        top.add(searchBtn);

        outputArea.setEditable(false);
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);
    }

    private void doSearch() {
        int n;
        try {
            n = Integer.parseInt(countField.getText().trim());
        } catch (NumberFormatException e) {
            outputArea.setText("Invalid n.");
            return;
        }

        SearchService search = context.getSearchService();
        MatchHistoryService history = context.getMatchHistoryService();
        GameDataManager data = context.getDataManager();

        List<MatchRecord> matches;
        String subject;
        if (queryType.getSelectedIndex() == 0) {
            Player player = findPlayer(search);
            if (player == null) {
                outputArea.setText("Player not found.");
                return;
            }
            matches = history.getRecentMatchesForPlayer(player.getId(), n);
            subject = "Player: " + player.getName();
        } else {
            Team team = findTeam(search);
            if (team == null) {
                outputArea.setText("Team not found.");
                return;
            }
            matches = history.getRecentMatchesForTeam(team.getId(), n);
            subject = "Team: " + team.getName();
        }
        outputArea.setText(formatMatchHistory(subject, n, matches, history, data));
    }

    private Player findPlayer(SearchService search) {
        if (searchType.getSelectedIndex() == 0) {
            try {
                return search.findPlayerById(Integer.parseInt(queryField.getText().trim()));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return search.findPlayerByName(queryField.getText().trim());
    }

    private Team findTeam(SearchService search) {
        if (searchType.getSelectedIndex() == 0) {
            try {
                return search.findTeamById(Integer.parseInt(queryField.getText().trim()));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return search.findTeamByName(queryField.getText().trim());
    }

    static String formatMatchHistory(String subject, int requested, List<MatchRecord> matches,
                                     MatchHistoryService history, GameDataManager data) {
        StringBuilder sb = new StringBuilder();
        sb.append("--- ").append(subject).append(" ---\n");
        if (matches.size() < requested) {
            sb.append("Note: only ").append(matches.size())
                    .append(" match(es) on record (requested ").append(requested).append(").\n\n");
        }
        if (matches.isEmpty()) {
            sb.append("No match records found.\n");
            return sb.toString();
        }

        sb.append("Recent ").append(matches.size()).append(" game(s):\n");
        for (MatchRecord record : matches) {
            sb.append("  Date: ").append(record.getDate()).append("\n");
            sb.append("  Opponent: ").append(record.getOpponent()).append("\n");
            sb.append("  Result: ").append(record.getResultDisplay()).append("\n");
            sb.append("  Heroes picked: ").append(formatHeroNames(record.getPickedHeroes(), data)).append("\n\n");
        }

        MatchHistoryService.WinLossRecord winLoss = history.calculateWinLossRecord(matches);
        sb.append(String.format("Win rate (this sample): %.2f%% (%dW / %dL / %d total)%n",
                winLoss.getWinRate(), winLoss.getWins(), winLoss.getLosses(), winLoss.getTotal()));

        Map<Integer, Double> pickRates = history.calculateHeroPickRate(matches);
        if (pickRates.isEmpty()) {
            sb.append("Hero pick rate: N/A\n");
        } else {
            sb.append("Hero pick rate:\n");
            for (Map.Entry<Integer, Double> entry : pickRates.entrySet()) {
                Hero hero = data.getHeroById(entry.getKey());
                String name = hero != null ? hero.getName() : "Hero#" + entry.getKey();
                sb.append(String.format("  - %s: %.2f%%%n", name, entry.getValue()));
            }
        }
        return sb.toString();
    }

    private static String formatHeroNames(List<Integer> heroIds, GameDataManager data) {
        if (heroIds == null || heroIds.isEmpty()) {
            return "None";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < heroIds.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            Hero hero = data.getHeroById(heroIds.get(i));
            sb.append(hero != null ? hero.getName() : "Hero#" + heroIds.get(i));
        }
        return sb.toString();
    }

    @Override
    public void refreshPanel() {
        outputArea.setText("");
    }
}
