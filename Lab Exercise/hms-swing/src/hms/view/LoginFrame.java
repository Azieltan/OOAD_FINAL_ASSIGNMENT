package hms.view;

import hms.controller.SystemController;
import hms.model.User;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class LoginFrame extends JFrame {
    private final SystemController controller;
    private final JTextField usernameField = new JTextField("admin", 18);
    private final JPasswordField passwordField = new JPasswordField("admin123", 18);

    public LoginFrame(SystemController controller) {
        super("Hospital Management System - Login");
        this.controller = controller;
        buildUi();
    }

    private void buildUi() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(430, 260);
        setLocationRelativeTo(null);

        JLabel title = new JLabel("Hospital Management System", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(20f));
        add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(new JLabel("Username"), gbc);
        gbc.gridx = 1;
        form.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        form.add(new JLabel("Password"), gbc);
        gbc.gridx = 1;
        form.add(passwordField, gbc);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(event -> attemptLogin());
        getRootPane().setDefaultButton(loginButton);

        gbc.gridx = 1;
        gbc.gridy = 2;
        form.add(loginButton, gbc);
        add(form, BorderLayout.CENTER);

        JLabel hint = new JLabel("Demo: admin/admin123, doctor/doctor123, reception/reception123", SwingConstants.CENTER);
        add(hint, BorderLayout.SOUTH);
    }

    private void attemptLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        User user = controller.login(username, password);

        if (user == null) {
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DashboardFrame dashboardFrame = new DashboardFrame(controller, user);
        dashboardFrame.setVisible(true);
        dispose();
    }
}
