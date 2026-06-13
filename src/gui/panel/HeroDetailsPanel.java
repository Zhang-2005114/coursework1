package gui.panel;

import gui.AppContext;
import model.*;
import service.GameDataManager;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

public class HeroDetailsPanel extends RefreshablePanel {
    private final AppContext context;
    private final JTextField nameField = new JTextField(20);
    private final JTextArea outputArea = new JTextArea(16, 50);

    public HeroDetailsPanel(AppContext context) {
        this.context = context;
        buildUi();
    }

    private void buildUi() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Hero name:"));
        top.add(nameField);
        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> doSearch());
        top.add(searchBtn);

        outputArea.setEditable(false);
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);
    }

    private void doSearch() {
        Hero hero = context.getSearchService().findHeroByName(nameField.getText().trim());
        outputArea.setText(formatHeroDetails(hero, context.getDataManager()));
    }

    static String formatHeroDetails(Hero h, GameDataManager dataManager) {
        if (h == null) {
            return "Hero not found.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("=== Hero Details ===\n");
        sb.append("ID: ").append(h.getId()).append("\n");
        sb.append("Name: ").append(h.getName()).append("\n");
        sb.append("Type: ").append(h.getType() != null ? h.getType().name() : "Unknown").append("\n");

        sb.append("\nBase Stats:\n");
        if (h.getBaseStats().isEmpty()) {
            sb.append("  None\n");
        } else {
            for (var entry : h.getBaseStats().entrySet()) {
                sb.append("  - ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        }

        sb.append("\nCompatible Equipments:\n");
        if (h.getCompatibleEquipments().isEmpty()) {
            sb.append("  None\n");
        } else {
            for (Integer equipId : h.getCompatibleEquipments()) {
                Equipment equip = dataManager.getEquipmentById(equipId);
                if (equip != null) {
                    sb.append("  - ").append(equip.getName()).append(" (ID: ").append(equipId).append(")\n");
                }
            }
        }

        sb.append("\nOwner Players (").append(h.getOwnerPlayers().size()).append("):\n");
        if (h.getOwnerPlayers().isEmpty()) {
            sb.append("  None\n");
        } else {
            for (Integer ownerId : h.getOwnerPlayers()) {
                Player player = dataManager.getPlayerById(ownerId);
                if (player != null) {
                    sb.append("  - ").append(player.getName()).append(" (ID: ").append(ownerId).append(")\n");
                }
            }
        }
        return sb.toString();
    }

    @Override
    public void refreshPanel() {
        outputArea.setText("");
        nameField.setText("");
    }
}
