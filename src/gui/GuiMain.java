package gui;


import javax.swing.SwingUtilities;

public class GuiMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AppContext context = AppContext.createFromStartup();
            MainFrame frame = new MainFrame(context);
            frame.setVisible(true);
        });
    }
}
