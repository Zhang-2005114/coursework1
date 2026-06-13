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

public class PlayerLookupPanel extends RefreshablePanel {
    private final AppContext context;
    private final JComboBox<String> searchType = new JComboBox<>(new String[]{"By ID", "By Name"});
    private final JTextField queryField = new JTextField(20);
    private final JTextArea outputArea = new JTextArea(16, 50);

    public PlayerLookupPanel(AppContext context) {
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
        Player player;
        if (searchType.getSelectedIndex() == 0) {
            try {
                int id = Integer.parseInt(queryField.getText().trim());
                player = search.findPlayerById(id);
            } catch (NumberFormatException e) {
                outputArea.setText("Invalid player ID.");
                return;
            }
        } else {
            player = search.findPlayerByName(queryField.getText().trim());
        }
        outputArea.setText(formatPlayerDetails(player, context.getDataManager()));
    }

    static String formatPlayerDetails(Player p, GameDataManager dataManager) {
        if (p == null) {
            return "Player not found.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("=== Player Details ===\n");
        sb.append("ID: ").append(p.getId()).append("\n");
        sb.append("Name: ").append(p.getName()).append("\n");
        sb.append("Level: ").append(p.getLevel()).append("\n");
        sb.append(String.format("Win Rate: %.2f%%%n", p.getWinRate() * 100));
        sb.append("Match Count: ").append(p.getMatchCount()).append("\n");

        Team team = dataManager.getTeamById(p.getTeamId());
        if (team != null) {
            sb.append("Team: ").append(team.getName()).append(" (ID: ").append(team.getId()).append(")\n");
        } else {
            sb.append("Team: None\n");
        }

        sb.append("\nOwned Heroes:\n");
        if (p.getOwnedHeroes().isEmpty()) {
            sb.append("  None\n");
        } else {
            for (Integer heroId : p.getOwnedHeroes()) {
                Hero hero = dataManager.getHeroById(heroId);
                if (hero != null) {
                    sb.append("  - ").append(hero.getName()).append(" (ID: ").append(heroId).append(")\n");
                }
            }
        }

        sb.append("\nEquipped Items by Hero:\n");
        boolean hasEquipment = false;
        for (var entry : p.getEquippedItemsByHeroId().entrySet()) {
            Hero hero = dataManager.getHeroById(entry.getKey());
            String heroName = hero != null ? hero.getName() : "Unknown Hero";
            sb.append("  ").append(heroName).append(" (ID: ").append(entry.getKey()).append("):\n");
            for (Integer equipId : entry.getValue()) {
                Equipment equip = dataManager.getEquipmentById(equipId);
                if (equip != null) {
                    sb.append("    - ").append(equip.getName()).append(" (ID: ").append(equipId).append(")\n");
                    hasEquipment = true;
                }
            }
        }
        if (!hasEquipment) {
            sb.append("  None\n");
        }
        return sb.toString();
    }

    @Override
    public void refreshPanel() {
        outputArea.setText("");
        queryField.setText("");
    }
}
