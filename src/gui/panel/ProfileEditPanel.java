package gui.panel;

import gui.AppContext;
import gui.util.MessageHelper;
import model.*;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.GridLayout;

public class ProfileEditPanel extends RefreshablePanel {
    private final AppContext context;
    private final JLabel currentInfo = new JLabel(" ");
    private final JTextField nameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);

    public ProfileEditPanel(AppContext context) {
        this.context = context;
        buildUi();
    }

    private void buildUi() {
        setLayout(new GridLayout(0, 1, 8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(new JLabel("=== Player Profile Edit ==="));
        add(currentInfo);
        add(new JLabel("New name (blank to keep):"));
        add(nameField);
        add(new JLabel("New password (blank to keep):"));
        add(passwordField);
        JButton saveBtn = new JButton("Save Profile");
        saveBtn.addActionListener(e -> saveProfile());
        add(saveBtn);
    }

    private void saveProfile() {
        if (!context.getAuthService().isPlayer()) {
            MessageHelper.error(this, "Only players can edit profile here.");
            return;
        }
        Player player = (Player) context.getAuthService().getCurrentUser();
        String newName = nameField.getText();
        String newPassword = new String(passwordField.getPassword());
        player.editBasicInfo(
                newName.isBlank() ? null : newName,
                newPassword.isBlank() ? null : newPassword);
        if (context.getDataManager().updatePlayer(player)) {
            MessageHelper.info(this, "Profile updated.");
            refreshPanel();
        } else {
            MessageHelper.error(this, "Failed to update profile.");
        }
    }

    @Override
    public void refreshPanel() {
        Person user = context.getAuthService().getCurrentUser();
        if (user instanceof Player) {
            Player player = (Player) user;
            currentInfo.setText(player.getInfo());
        } else {
            currentInfo.setText("Not logged in as player.");
        }
        nameField.setText("");
        passwordField.setText("");
    }
}
