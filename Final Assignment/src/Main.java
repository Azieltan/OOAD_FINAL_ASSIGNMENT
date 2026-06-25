import gui.RentalAppGUI;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        // Use system look and feel for a more modern native look
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fallback to default cross-platform look and feel if system fails
        }

        SwingUtilities.invokeLater(() -> {
            RentalAppGUI app = new RentalAppGUI();
            app.setVisible(true);
        });
    }
}
