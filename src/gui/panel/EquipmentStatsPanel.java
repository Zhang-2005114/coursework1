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

public class EquipmentStatsPanel extends RefreshablePanel {
    private final AppContext context;
    private final JComboBox<String> sortChoice = new JComboBox<>(new String[]{
            "Usage count", "Win rate contribution", "Hero count", "Custom score", "View formulas"
    });
    private final JTextField formulaField = new JTextField(30);
    private final JTextArea outputArea = new JTextArea(14, 50);

    public EquipmentStatsPanel(AppContext context) {
        this.context = context;
        formulaField.setText(context.getRankingService().getDefaultEquipmentFormula());
        buildUi();
    }

    private void buildUi() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Sort by:"));
        top.add(sortChoice);
        top.add(new JLabel("Formula:"));
        top.add(formulaField);
        JButton runBtn = new JButton("Rank");
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
            outputArea.setText(formatEquipmentFormulas(ranking));
            return;
        }

        List<Equipment> ranked;
        String sortLabel;
        String formula = null;
        switch (choice) {
            case 0:
                ranked = ranking.rankEquipmentByUsage();
                sortLabel = "Usage count";
                break;
            case 1:
                ranked = ranking.rankEquipmentByWinRateContribution();
                sortLabel = "Win rate contribution";
                break;
            case 2:
                ranked = ranking.rankEquipmentByHeroCount();
                sortLabel = "Compatible hero count";
                break;
            case 3:
                formula = formulaField.getText();
                ranked = ranking.rankEquipmentByCustomScore(formula);
                sortLabel = "Custom score";
                break;
            default:
                outputArea.setText("Invalid choice.");
                return;
        }
        outputArea.setText(formatEquipmentRanking(ranked, sortLabel, formula, ranking));
    }

    static String formatEquipmentFormulas(RankingService ranking) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Equipment Sorting Formulas ===\n");
        sb.append("1. Usage count: rank by usageCount (descending).\n");
        sb.append("2. Win rate contribution: rank by winRateContribution (descending).\n");
        sb.append("3. Hero count: rank by compatible hero count (descending).\n");
        sb.append("4. Custom score: ").append(ranking.getDefaultEquipmentFormula()).append("\n");
        sb.append("   usage = usageCount, winRate = winRateContribution, heroCount = compatible heroes.\n");
        sb.append("Tie-break (all sorts): lower equipment ID ranks higher.\n");
        return sb.toString();
    }

    static String formatEquipmentRanking(List<Equipment> ranked, String sortLabel,
                                         String formula, RankingService ranking) {
        StringBuilder sb = new StringBuilder();
        sb.append("--- Equipment ranking by ").append(sortLabel).append(" ---\n");
        if (ranked.isEmpty()) {
            sb.append("No equipment found.\n");
            return sb.toString();
        }
        boolean showScore = formula != null;
        int rank = 1;
        for (Equipment equipment : ranked) {
            sb.append(rank++).append(". ").append(equipment.getName())
                    .append(" (ID: ").append(equipment.getId()).append(")\n");
            if (showScore) {
                double score = ranking.calculateEquipmentScore(equipment, formula);
                sb.append(String.format("   Score: %.2f | Type: %s | Usage: %d | Win rate contrib: %.2f | Heroes: %d%n",
                        score, equipment.getType(), equipment.getUsageCount(),
                        equipment.getWinRateContribution(), equipment.getHeroUsageCount()));
            } else {
                sb.append(String.format("   Type: %s | Usage: %d | Win rate contrib: %.2f | Heroes: %d%n",
                        equipment.getType(), equipment.getUsageCount(),
                        equipment.getWinRateContribution(), equipment.getHeroUsageCount()));
            }
        }
        return sb.toString();
    }

    @Override
    public void refreshPanel() {
        outputArea.setText("");
    }
}
