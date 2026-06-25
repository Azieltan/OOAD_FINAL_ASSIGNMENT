import facade.RentalSystemFacade;
import gui.LoginFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fallback
        }

        SwingUtilities.invokeLater(() -> {
            RentalSystemFacade facade = new RentalSystemFacade();
            LoginFrame loginFrame = new LoginFrame(facade);
            loginFrame.setVisible(true);
        });
    }
}
