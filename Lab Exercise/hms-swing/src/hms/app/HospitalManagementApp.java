package hms.app;

import hms.controller.SystemController;
import hms.service.HospitalRepository;
import hms.view.LoginFrame;

import javax.swing.SwingUtilities;

public class HospitalManagementApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SystemController controller = new SystemController(HospitalRepository.seeded());
            LoginFrame loginFrame = new LoginFrame(controller);
            loginFrame.setVisible(true);
        });
    }
}
