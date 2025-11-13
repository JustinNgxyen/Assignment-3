import controller.MainController;
import view.MainFrame;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame view = new MainFrame();
            new MainController(view);
            view.setVisible(true);
        });
    }
}
