package gui.dialog;

import gui.AppContext;
import gui.util.MessageHelper;
import service.AuthenticationService;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;

public class LoginDialog extends JDialog {
    private final AppContext context;
    private boolean loggedIn;

    public LoginDialog(Frame owner, AppContext context) {
        super(owner, "Login", true);
        this.context = context;
        buildUi();
        setSize(360, 220);
        setLocationRelativeTo(owner);
    }

    private void buildUi() {
        JTextField userField = new JTextField(16);
        JPasswordField passwordField = new JPasswordField(16);
        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        form.add(new JLabel("Username or ID:"));
        form.add(userField);
        form.add(new JLabel("Password:"));
        form.add(passwordField);

        JButton loginBtn = new JButton("Login");
        JButton cancelBtn = new JButton("Cancel");
        loginBtn.addActionListener(e -> attemptLogin(userField.getText(), new String(passwordField.getPassword())));
        cancelBtn.addActionListener(e -> dispose());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(loginBtn);
        buttons.add(cancelBtn);

        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
    }

    private void attemptLogin(String userInput, String password) {
        if (userInput == null || userInput.isBlank()) {
            MessageHelper.error(this, "Username or ID cannot be empty.");
            return;
        }
        boolean success;
        String trimmed = userInput.trim();
        AuthenticationService auth = context.getAuthService();
        try {
            int id = Integer.parseInt(trimmed);
            success = auth.login(id, password);
        } catch (NumberFormatException ex) {
            success = auth.login(trimmed, password);
        }
        if (success) {
            loggedIn = true;
            dispose();
        } else {
            MessageHelper.error(this, "Login failed. Check credentials or logout first.");
        }
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }
}
