package gui;

import facade.RentalSystemFacade;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class LoginFrame extends JFrame {
    private RentalSystemFacade facade;
    
    private JTextField userIdField;
    private JTextField userNameField;
    private JComboBox<String> userTypeCombo;

    public LoginFrame(RentalSystemFacade facade) {
        super("Campus Smart Equipment Rental - Sign In");
        this.facade = facade;
        initializeUI();
    }

    private void initializeUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(550, 420);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header Panel (Blue banner)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("CAMPUS SMART RENTAL KIOSK", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        JLabel subLabel = new JLabel("Fast, Multi-Item Checkout & Return", SwingConstants.CENTER);
        subLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        subLabel.setForeground(new Color(236, 240, 241));
        headerPanel.add(subLabel, BorderLayout.SOUTH);
        
        add(headerPanel, BorderLayout.NORTH);

        // Center Panel (Self-Service Login Form)
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Banner text
        JLabel infoLabel = new JLabel("<html><center>Enter your ID and Name to begin self-service checkout.<br/><i>Use the exact same details next time to return items.</i></center></html>", SwingConstants.CENTER);
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoLabel.setForeground(Color.DARK_GRAY);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        centerPanel.add(infoLabel, gbc);

        // User ID
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        centerPanel.add(new JLabel("User ID / Student ID:"), gbc);
        
        userIdField = new JTextField(15);
        userIdField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        centerPanel.add(userIdField, gbc);

        // User Name
        gbc.gridy = 2;
        gbc.gridx = 0;
        centerPanel.add(new JLabel("Your Full Name:"), gbc);
        
        userNameField = new JTextField(15);
        userNameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        centerPanel.add(userNameField, gbc);

        // User Type
        gbc.gridy = 3;
        gbc.gridx = 0;
        centerPanel.add(new JLabel("Role Category:"), gbc);
        
        userTypeCombo = new JComboBox<>(new String[]{"Student", "Staff", "Final Year Student"});
        userTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 1;
        centerPanel.add(userTypeCombo, gbc);

        // Login Button
        JButton loginBtn = new JButton("Enter Self-Service Kiosk");
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.setBackground(new Color(46, 204, 113));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.addActionListener(e -> handleUserLogin());
        
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        centerPanel.add(loginBtn, gbc);

        add(centerPanel, BorderLayout.CENTER);

        // Footer Panel (Admin login link)
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 10));
        
        JLabel adminLink = new JLabel("<html><u>Admin Panel Access</u></html>");
        adminLink.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        adminLink.setForeground(new Color(127, 140, 141));
        adminLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        adminLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showAdminLoginDialog();
            }
        });
        footerPanel.add(adminLink);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private void handleUserLogin() {
        String id = userIdField.getText().trim();
        String name = userNameField.getText().trim();
        String type = (String) userTypeCombo.getSelectedItem();

        String result = facade.userLogin(id, name, type);
        if ("Success".equals(result)) {
            // Open user dashboard
            RentalAppGUI dashboard = new RentalAppGUI(facade, false);
            dashboard.setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, result, "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAdminLoginDialog() {
        JDialog dialog = new JDialog(this, "Admin Verification", true);
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(new JLabel("Admin ID:"), gbc);
        
        JTextField adminIdField = new JTextField(12);
        gbc.gridx = 1;
        dialog.add(adminIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(new JLabel("Password:"), gbc);
        
        JPasswordField adminPassField = new JPasswordField(12);
        gbc.gridx = 1;
        dialog.add(adminPassField, gbc);

        JButton verifyBtn = new JButton("Login as Admin");
        verifyBtn.addActionListener(e -> {
            String adminId = adminIdField.getText().trim();
            String password = new String(adminPassField.getPassword());
            
            if (facade.adminLogin(adminId, password)) {
                dialog.dispose();
                // Open admin dashboard
                RentalAppGUI dashboard = new RentalAppGUI(facade, true);
                dashboard.setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Invalid Admin credentials.", "Access Denied", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        dialog.add(verifyBtn, gbc);

        dialog.setVisible(true);
    }
}
