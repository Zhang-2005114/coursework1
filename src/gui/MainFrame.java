package gui;

import gui.dialog.LoginDialog;
import gui.panel.DataManagementPanel;
import gui.panel.EquipmentStatsPanel;
import gui.panel.HeroDetailsPanel;
import gui.panel.LeaderboardPanel;
import gui.panel.MatchHistoryPanel;
import gui.panel.PlayerLookupPanel;
import gui.panel.ProfileEditPanel;
import gui.panel.RefreshablePanel;
import gui.panel.TeamOverviewPanel;
import gui.util.MessageHelper;
import model.*;
import service.AuthenticationService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {
    public static final String CARD_PLAYER = "player";
    public static final String CARD_TEAM = "team";
    public static final String CARD_HERO = "hero";
    public static final String CARD_EQUIPMENT = "equipment";
    public static final String CARD_MATCH = "match";
    public static final String CARD_LEADERBOARD = "leaderboard";
    public static final String CARD_MANAGEMENT = "management";

    private final AppContext context;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private final JLabel statusLabel = new JLabel();
    private final JButton managementButton = new JButton("7 Data Management");
    private final List<RefreshablePanel> refreshablePanels = new ArrayList<>();

    private DataManagementPanel dataManagementPanel;
    private ProfileEditPanel profileEditPanel;

    public MainFrame(AppContext context) {
        super("Honor of Kings IMS");
        this.context = context;
        buildUi();
        updateSessionUi();
    }

    private void buildUi() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 640);
        setLocationRelativeTo(null);

        setJMenuBar(buildMenuBar());

        JPanel navPanel = new JPanel();
        navPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        navPanel.add(createNavButton("1 Player Lookup", CARD_PLAYER));
        navPanel.add(createNavButton("2 Team Overview", CARD_TEAM));
        navPanel.add(createNavButton("3 Hero Details", CARD_HERO));
        navPanel.add(createNavButton("4 Equipment Stats", CARD_EQUIPMENT));
        navPanel.add(createNavButton("5 Match History", CARD_MATCH));
        navPanel.add(createNavButton("6 Leaderboard", CARD_LEADERBOARD));
        managementButton.addActionListener(e -> showManagementPanel());
        navPanel.add(managementButton);

        registerPanel(new PlayerLookupPanel(context), CARD_PLAYER);
        registerPanel(new TeamOverviewPanel(context), CARD_TEAM);
        registerPanel(new HeroDetailsPanel(context), CARD_HERO);
        registerPanel(new EquipmentStatsPanel(context), CARD_EQUIPMENT);
        registerPanel(new MatchHistoryPanel(context), CARD_MATCH);
        registerPanel(new LeaderboardPanel(context), CARD_LEADERBOARD);

        dataManagementPanel = new DataManagementPanel(context);
        profileEditPanel = new ProfileEditPanel(context);
        contentPanel.add(dataManagementPanel, "admin-mgmt");
        contentPanel.add(profileEditPanel, "player-mgmt");

        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        statusBar.add(statusLabel, BorderLayout.WEST);

        JPanel center = new JPanel(new BorderLayout());
        center.add(navPanel, BorderLayout.NORTH);
        center.add(contentPanel, BorderLayout.CENTER);
        center.add(statusBar, BorderLayout.SOUTH);

        add(center, BorderLayout.CENTER);
        cardLayout.show(contentPanel, CARD_PLAYER);
    }

    private JMenuBar buildMenuBar() {
        JMenuBar bar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.addActionListener(e -> handleSave());
        JMenuItem loadItem = new JMenuItem("Load");
        loadItem.addActionListener(e -> handleLoad());
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> handleExit());
        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
        fileMenu.add(exitItem);

        JMenu sessionMenu = new JMenu("Session");
        JMenuItem loginItem = new JMenuItem("Login");
        loginItem.addActionListener(e -> handleLogin());
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> handleLogout());
        sessionMenu.add(loginItem);
        sessionMenu.add(logoutItem);

        bar.add(fileMenu);
        bar.add(sessionMenu);
        return bar;
    }

    private JButton createNavButton(String label, String card) {
        JButton button = new JButton(label);
        button.addActionListener(e -> cardLayout.show(contentPanel, card));
        return button;
    }

    private void registerPanel(RefreshablePanel panel, String card) {
        contentPanel.add(panel, card);
        refreshablePanels.add(panel);
    }

    private void showManagementPanel() {
        AuthenticationService auth = context.getAuthService();
        if (auth.getCurrentUser() == null) {
            MessageHelper.info(this, "Please log in first (Session -> Login).");
            return;
        }
        if (auth.isAdmin()) {
            dataManagementPanel.refreshPanel();
            cardLayout.show(contentPanel, "admin-mgmt");
        } else if (auth.isPlayer()) {
            profileEditPanel.refreshPanel();
            cardLayout.show(contentPanel, "player-mgmt");
        }
    }

    public void updateSessionUi() {
        Person user = context.getAuthService().getCurrentUser();
        if (user != null) {
            statusLabel.setText("Session: " + user.getName() + " (" + user.getRole() + ")");
        } else {
            statusLabel.setText("Session: not logged in");
        }
        managementButton.setEnabled(user != null);
    }

    private void handleLogin() {
        if (context.getAuthService().getCurrentUser() != null) {
            MessageHelper.info(this, "Already logged in. Use Logout first.");
            return;
        }
        LoginDialog dialog = new LoginDialog(this, context);
        dialog.setVisible(true);
        if (dialog.isLoggedIn()) {
            updateSessionUi();
        }
    }

    private void handleLogout() {
        context.getAuthService().logout();
        updateSessionUi();
    }

    private void handleSave() {
        try {
            context.saveAll();
            MessageHelper.info(this, "Data saved to " + context.getFileStorageService().getSavePath());
        } catch (IOException e) {
            MessageHelper.error(this, "Save failed: " + e.getMessage());
        }
    }

    private void handleLoad() {
        try {
            context.reloadData();
            refreshAllPanels();
            updateSessionUi();
            MessageHelper.info(this, "Data loaded from " + context.getFileStorageService().getSavePath());
        } catch (IOException e) {
            MessageHelper.error(this, "Load failed: " + e.getMessage());
        }
    }

    private void handleExit() {
        if (MessageHelper.confirm(this, "Save data before exit?")) {
            try {
                context.saveAll();
            } catch (IOException e) {
                MessageHelper.error(this, "Save on exit failed: " + e.getMessage());
            }
        }
        dispose();
        System.exit(0);
    }

    private void refreshAllPanels() {
        for (RefreshablePanel panel : refreshablePanels) {
            panel.refreshPanel();
        }
        dataManagementPanel.refreshPanel();
        profileEditPanel.refreshPanel();
    }
}
