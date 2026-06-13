package gui.panel;

import enums.*;
import gui.AppContext;
import gui.util.MessageHelper;
import gui.util.TeamStatsRefresher;
import model.*;
import util.CsvUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;

public class DataManagementPanel extends RefreshablePanel {
    private final AppContext context;
    private final JTabbedPane tabs = new JTabbedPane();

    public DataManagementPanel(AppContext context) {
        this.context = context;
        buildUi();
    }

    private void buildUi() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        tabs.addTab("Players", buildEntityTab(
                new String[]{"ID", "Name", "Level", "Win Rate", "Team ID", "Matches"},
                this::loadPlayers,
                this::addPlayer,
                this::deletePlayer,
                this::editPlayer));
        tabs.addTab("Heroes", buildEntityTab(
                new String[]{"ID", "Name", "Type"},
                this::loadHeroes,
                this::addHero,
                this::deleteHero,
                this::editHero));
        tabs.addTab("Equipment", buildEntityTab(
                new String[]{"ID", "Name", "Type", "Usage", "Win Rate Contrib"},
                this::loadEquipment,
                this::addEquipment,
                this::deleteEquipment,
                this::editEquipment));
        tabs.addTab("Teams", buildEntityTab(
                new String[]{"ID", "Name", "Members", "Avg Level", "Win Rate"},
                this::loadTeams,
                this::addTeam,
                this::deleteTeam,
                this::editTeam));
        tabs.addTab("Matches", buildEntityTab(
                new String[]{"ID", "Date", "Player", "Team", "Opponent", "Result"},
                this::loadMatches,
                this::addMatch,
                this::deleteMatch,
                this::editMatch));
        add(tabs, BorderLayout.CENTER);
        refreshPanel();
    }

    private JPanel buildEntityTab(String[] columns, Supplier<Object[][]> loader,
                                  Runnable addAction, Runnable deleteAction, Runnable editAction) {
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setName(columns[0]);

        JPanel panel = new JPanel(new BorderLayout(4, 4));
        panel.putClientProperty("tableModel", model);
        panel.putClientProperty("loader", loader);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> reloadTable(model, loader));
        JButton addBtn = new JButton("Add");
        addBtn.addActionListener(e -> {
            if (requireAdmin()) {
                addAction.run();
                reloadTable(model, loader);
            }
        });
        JButton deleteBtn = new JButton("Delete");
        deleteBtn.addActionListener(e -> {
            if (requireAdmin() && table.getSelectedRow() >= 0) {
                deleteAction.run();
                reloadTable(model, loader);
            } else if (table.getSelectedRow() < 0) {
                MessageHelper.info(this, "Select a row first.");
            }
        });
        JButton editBtn = new JButton("Edit");
        editBtn.addActionListener(e -> {
            if (requireAdmin() && table.getSelectedRow() >= 0) {
                editAction.run();
                reloadTable(model, loader);
            } else if (table.getSelectedRow() < 0) {
                MessageHelper.info(this, "Select a row first.");
            }
        });
        buttons.add(refreshBtn);
        buttons.add(addBtn);
        buttons.add(deleteBtn);
        buttons.add(editBtn);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private void reloadTable(DefaultTableModel model, Supplier<Object[][]> loader) {
        model.setRowCount(0);
        for (Object[] row : loader.get()) {
            model.addRow(row);
        }
    }

    private boolean requireAdmin() {
        if (!context.getAuthService().requireAdmin()) {
            MessageHelper.error(this, "Admin privileges required.");
            return false;
        }
        return true;
    }

    private int getSelectedId(JTabbedPane tabbedPane) {
        JPanel tab = (JPanel) tabbedPane.getSelectedComponent();
        JScrollPane scroll = (JScrollPane) tab.getComponent(0);
        JTable table = (JTable) scroll.getViewport().getView();
        int row = table.getSelectedRow();
        if (row < 0) {
            return -1;
        }
        return (Integer) table.getValueAt(row, 0);
    }

    private Object[][] loadPlayers() {
        List<Player> players = context.getDataManager().getAllPlayers();
        Object[][] rows = new Object[players.size()][6];
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            rows[i] = new Object[]{p.getId(), p.getName(), p.getLevel(), p.getWinRate(), p.getTeamId(), p.getMatchCount()};
        }
        return rows;
    }

    private Object[][] loadHeroes() {
        List<Hero> heroes = context.getDataManager().getAllHeroes();
        Object[][] rows = new Object[heroes.size()][3];
        for (int i = 0; i < heroes.size(); i++) {
            Hero h = heroes.get(i);
            rows[i] = new Object[]{h.getId(), h.getName(), h.getType()};
        }
        return rows;
    }

    private Object[][] loadEquipment() {
        List<Equipment> list = context.getDataManager().getAllEquipments();
        Object[][] rows = new Object[list.size()][5];
        for (int i = 0; i < list.size(); i++) {
            Equipment e = list.get(i);
            rows[i] = new Object[]{e.getId(), e.getName(), e.getType(), e.getUsageCount(), e.getWinRateContribution()};
        }
        return rows;
    }

    private Object[][] loadTeams() {
        List<Team> teams = context.getDataManager().getAllTeams();
        Object[][] rows = new Object[teams.size()][5];
        for (int i = 0; i < teams.size(); i++) {
            Team t = teams.get(i);
            rows[i] = new Object[]{t.getId(), t.getName(), t.getPlayerList().size(), t.getAvgLevel(), t.getWinRate()};
        }
        return rows;
    }

    private Object[][] loadMatches() {
        List<MatchRecord> matches = context.getDataManager().getAllMatchRecords();
        Object[][] rows = new Object[matches.size()][6];
        for (int i = 0; i < matches.size(); i++) {
            MatchRecord m = matches.get(i);
            rows[i] = new Object[]{m.getId(), m.getDate(), m.getPlayerId(), m.getTeamId(), m.getOpponent(), m.getResult()};
        }
        return rows;
    }

    private void addPlayer() {
        try {
            int id = Integer.parseInt(prompt("Player ID:"));
            if (context.getDataManager().getPlayerById(id) != null) {
                MessageHelper.error(this, "Player ID already exists.");
                return;
            }
            String name = prompt("Name:");
            String password = prompt("Password:");
            int level = Integer.parseInt(prompt("Level:"));
            double winRate = Double.parseDouble(prompt("Win rate (0-1):"));
            int teamId = Integer.parseInt(prompt("Team ID:"));
            int matchCount = Integer.parseInt(prompt("Match count:"));
            Player player = new Player(id, name, password, level, winRate, teamId, matchCount);
            context.getDataManager().addPlayer(player);
            Team team = context.getDataManager().getTeamById(teamId);
            if (team != null) {
                team.addPlayer(id);
            }
            TeamStatsRefresher.refreshAll(context);
            MessageHelper.info(this, "Player added.");
        } catch (NumberFormatException e) {
            MessageHelper.error(this, "Invalid number input.");
        }
    }

    private void deletePlayer() {
        int id = getSelectedId(tabs);
        if (id < 0) {
            return;
        }
        if (context.getDataManager().removePlayer(id)) {
            TeamStatsRefresher.refreshAll(context);
            MessageHelper.info(this, "Player deleted.");
        } else {
            MessageHelper.error(this, "Delete failed.");
        }
    }

    private void editPlayer() {
        int id = getSelectedId(tabs);
        Player player = context.getDataManager().getPlayerById(id);
        if (player == null) {
            MessageHelper.error(this, "Player not found.");
            return;
        }
        String name = prompt("Name [" + player.getName() + "]:");
        if (!name.isBlank()) {
            player.setName(name);
        }
        String password = prompt("Password [hidden]:");
        if (!password.isBlank()) {
            player.setPassword(password);
        }
        String levelStr = prompt("Level [" + player.getLevel() + "]:");
        if (!levelStr.isBlank()) {
            player.setLevel(Integer.parseInt(levelStr));
        }
        context.getDataManager().updatePlayer(player);
        TeamStatsRefresher.refreshAll(context);
        MessageHelper.info(this, "Player updated.");
    }

    private void addHero() {
        try {
            int id = Integer.parseInt(prompt("Hero ID:"));
            if (context.getDataManager().getHeroById(id) != null) {
                MessageHelper.error(this, "Hero ID already exists.");
                return;
            }
            String name = prompt("Name:");
            HeroType type = HeroType.fromString(prompt("Type (MAGE/SHOOTER/TANK/JUNGLER/SUPPORT):"));
            if (type == null) {
                MessageHelper.error(this, "Invalid hero type.");
                return;
            }
            Hero hero = new Hero(id, name, type);
            hero.setStat("attack", Integer.parseInt(prompt("Attack:")));
            hero.setStat("defense", Integer.parseInt(prompt("Defense:")));
            hero.setStat("hp", Integer.parseInt(prompt("HP:")));
            context.getDataManager().addHero(hero);
            MessageHelper.info(this, "Hero added.");
        } catch (NumberFormatException e) {
            MessageHelper.error(this, "Invalid number input.");
        }
    }

    private void deleteHero() {
        int id = getSelectedId(tabs);
        if (context.getDataManager().removeHero(id)) {
            MessageHelper.info(this, "Hero deleted.");
        } else {
            MessageHelper.error(this, "Delete failed.");
        }
    }

    private void editHero() {
        int id = getSelectedId(tabs);
        Hero hero = context.getDataManager().getHeroById(id);
        if (hero == null) {
            return;
        }
        String name = prompt("Name [" + hero.getName() + "]:");
        if (!name.isBlank()) {
            hero.setName(name);
        }
        context.getDataManager().updateHero(hero);
        MessageHelper.info(this, "Hero updated.");
    }

    private void addEquipment() {
        try {
            int id = Integer.parseInt(prompt("Equipment ID:"));
            if (context.getDataManager().getEquipmentById(id) != null) {
                MessageHelper.error(this, "Equipment ID already exists.");
                return;
            }
            String name = prompt("Name:");
            EquipmentType type = EquipmentType.fromString(prompt("Type (ATTACK/DEFENSE/SPELL):"));
            if (type == null) {
                MessageHelper.error(this, "Invalid equipment type.");
                return;
            }
            int usage = Integer.parseInt(prompt("Usage count:"));
            double winRate = Double.parseDouble(prompt("Win rate contribution:"));
            context.getDataManager().addEquipment(new Equipment(id, name, type, usage, winRate));
            MessageHelper.info(this, "Equipment added.");
        } catch (NumberFormatException e) {
            MessageHelper.error(this, "Invalid number input.");
        }
    }

    private void deleteEquipment() {
        int id = getSelectedId(tabs);
        if (context.getDataManager().removeEquipment(id)) {
            MessageHelper.info(this, "Equipment deleted.");
        } else {
            MessageHelper.error(this, "Delete failed.");
        }
    }

    private void editEquipment() {
        int id = getSelectedId(tabs);
        Equipment equipment = context.getDataManager().getEquipmentById(id);
        if (equipment == null) {
            return;
        }
        String name = prompt("Name [" + equipment.getName() + "]:");
        if (!name.isBlank()) {
            equipment.setName(name);
        }
        context.getDataManager().updateEquipment(equipment);
        MessageHelper.info(this, "Equipment updated.");
    }

    private void addTeam() {
        try {
            int id = Integer.parseInt(prompt("Team ID:"));
            if (context.getDataManager().getTeamById(id) != null) {
                MessageHelper.error(this, "Team ID already exists.");
                return;
            }
            String name = prompt("Name:");
            context.getDataManager().addTeam(new Team(id, name));
            MessageHelper.info(this, "Team added.");
        } catch (NumberFormatException e) {
            MessageHelper.error(this, "Invalid number input.");
        }
    }

    private void deleteTeam() {
        int id = getSelectedId(tabs);
        if (context.getDataManager().removeTeam(id)) {
            TeamStatsRefresher.refreshAll(context);
            MessageHelper.info(this, "Team deleted.");
        } else {
            MessageHelper.error(this, "Delete failed.");
        }
    }

    private void editTeam() {
        int id = getSelectedId(tabs);
        Team team = context.getDataManager().getTeamById(id);
        if (team == null) {
            return;
        }
        String name = prompt("Name [" + team.getName() + "]:");
        if (!name.isBlank()) {
            team.setName(name);
        }
        context.getDataManager().updateTeam(team);
        MessageHelper.info(this, "Team updated.");
    }

    private void addMatch() {
        try {
            int id = Integer.parseInt(prompt("Match ID:"));
            if (context.getDataManager().getMatchRecordById(id) != null) {
                MessageHelper.error(this, "Match ID already exists.");
                return;
            }
            LocalDate date = LocalDate.parse(prompt("Date (yyyy-MM-dd):"));
            int playerId = Integer.parseInt(prompt("Player ID:"));
            int teamId = Integer.parseInt(prompt("Team ID:"));
            String opponent = prompt("Opponent:");
            MatchResult result = MatchResult.fromString(prompt("Result (WIN/LOSE):"));
            if (result == null) {
                MessageHelper.error(this, "Invalid result.");
                return;
            }
            List<Integer> heroes = CsvUtil.parseIntList(
                    prompt("Hero IDs (semicolon-separated):"), ";");
            context.getDataManager().addMatchRecord(
                    new MatchRecord(id, date, playerId, teamId, opponent, result, heroes));
            TeamStatsRefresher.refreshAll(context);
            MessageHelper.info(this, "Match record added.");
        } catch (Exception e) {
            MessageHelper.error(this, "Invalid input: " + e.getMessage());
        }
    }

    private void deleteMatch() {
        int id = getSelectedId(tabs);
        if (context.getDataManager().removeMatchRecord(id)) {
            TeamStatsRefresher.refreshAll(context);
            MessageHelper.info(this, "Match deleted.");
        } else {
            MessageHelper.error(this, "Delete failed.");
        }
    }

    private void editMatch() {
        int id = getSelectedId(tabs);
        MatchRecord record = context.getDataManager().getMatchRecordById(id);
        if (record == null) {
            return;
        }
        String opponent = prompt("Opponent [" + record.getOpponent() + "]:");
        if (!opponent.isBlank()) {
            record.setOpponent(opponent);
        }
        context.getDataManager().updateMatchRecord(record);
        TeamStatsRefresher.refreshAll(context);
        MessageHelper.info(this, "Match updated.");
    }

    private String prompt(String message) {
        return JOptionPane.showInputDialog(this, message);
    }

    @Override
    public void refreshPanel() {
        for (int i = 0; i < tabs.getTabCount(); i++) {
            JPanel tab = (JPanel) tabs.getComponentAt(i);
            DefaultTableModel model = (DefaultTableModel) tab.getClientProperty("tableModel");
            @SuppressWarnings("unchecked")
            Supplier<Object[][]> loader = (Supplier<Object[][]>) tab.getClientProperty("loader");
            reloadTable(model, loader);
        }
    }
}
