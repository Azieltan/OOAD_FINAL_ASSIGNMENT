package gui;

import facade.RentalSystemFacade;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import model.Equipment;
import model.RentalRecord;

public class RentalAppGUI extends JFrame {
    private RentalSystemFacade facade;
    private boolean isAdmin;
    
    // UI Elements - Admin Mode
    private JTable adminInventoryTable;
    private DefaultTableModel adminInventoryModel;
    private JTextField addIdField, addNameField, addRateField;
    private JComboBox<String> addCategoryCombo;
    
    private JTextField updateNameField, updateRateField;
    private JComboBox<String> updateStatusCombo;
    
    private JTable adminRentalsTable;
    private DefaultTableModel adminRentalsModel;

    // UI Elements - User Mode
    private JTable userCatalogTable;
    private DefaultTableModel userCatalogModel;
    private JTextField rentDurationField;
    private JLabel cartSummaryLabel;
    
    private JTable userRentalsTable;
    private DefaultTableModel userRentalsModel;
    private JTextField returnDaysField;
    private JCheckBox damageCheck;
    private JTextArea receiptArea;

    public RentalAppGUI(RentalSystemFacade facade, boolean isAdmin) {
        this.facade = facade;
        this.isAdmin = isAdmin;
        initializeUI();
        refreshAllData();
    }

    private void initializeUI() {
        setTitle(isAdmin ? "Smart Rental System - ADMIN PANEL" : "Smart Rental System - SELF-SERVICE KIOSK");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Colors & Fonts
        Color headerColor = isAdmin ? new Color(192, 57, 43) : new Color(41, 128, 185); // Red for admin, Blue for user
        Font titleFont = new Font("Segoe UI", Font.BOLD, 18);
        
        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(headerColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        String welcomeText = isAdmin 
                ? "ADMINISTRATIVE CONTROL CONSOLE" 
                : "WELCOME, " + facade.getCurrentUser().getName().toUpperCase() + " (" + facade.getCurrentUser().getType() + ")";
        
        JLabel titleLabel = new JLabel(welcomeText);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(titleFont);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JButton logoutBtn = new JButton("Log Out");
        logoutBtn.setFocusPainted(false);
        logoutBtn.addActionListener(e -> handleLogout());
        headerPanel.add(logoutBtn, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        if (isAdmin) {
            tabbedPane.addTab("Manage Inventory", createAdminInventoryTab());
            tabbedPane.addTab("System Rental Logs", createAdminRentalsTab());
        } else {
            tabbedPane.addTab("Rent Equipment", createUserRentTab());
            tabbedPane.addTab("My Active Rentals", createUserReturnTab());
        }
        
        add(tabbedPane, BorderLayout.CENTER);
    }

    private void handleLogout() {
        facade.logout();
        LoginFrame loginFrame = new LoginFrame(facade);
        loginFrame.setVisible(true);
        this.dispose();
    }

    // ==========================================
    // ADMIN TABS CREATION
    // ==========================================

    private JPanel createAdminInventoryTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Table
        String[] columns = {"ID", "Name", "Category", "Rate ($/day)", "Status"};
        adminInventoryModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        adminInventoryTable = new JTable(adminInventoryModel);
        adminInventoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        adminInventoryTable.getSelectionModel().addListSelectionListener(e -> handleInventorySelect());
        panel.add(new JScrollPane(adminInventoryTable), BorderLayout.CENTER);
        
        // Operations panel (Add and Edit forms side by side)
        JPanel opsPanel = new JPanel(new GridLayout(1, 2, 15, 10));
        
        // 1. Add Form
        JPanel addForm = new JPanel(new GridLayout(5, 2, 5, 5));
        addForm.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Add Equipment", TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12)
        ));
        
        addForm.add(new JLabel("ID:"));
        addIdField = new JTextField();
        addForm.add(addIdField);
        
        addForm.add(new JLabel("Name:"));
        addNameField = new JTextField();
        addForm.add(addNameField);
        
        addForm.add(new JLabel("Category:"));
        addCategoryCombo = new JComboBox<>(new String[]{"Electronics", "Media Equipment", "Laboratory Equipment"});
        addForm.add(addCategoryCombo);
        
        addForm.add(new JLabel("Daily Rate ($):"));
        addRateField = new JTextField();
        addForm.add(addRateField);
        
        JButton addBtn = new JButton("Add Equipment");
        addBtn.addActionListener(e -> handleAddEquipment());
        addForm.add(addBtn);
        opsPanel.add(addForm);
        
        // 2. Edit/Update Form
        JPanel editForm = new JPanel(new GridLayout(5, 2, 5, 5));
        editForm.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Edit Selected Equipment", TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12)
        ));
        
        editForm.add(new JLabel("Name:"));
        updateNameField = new JTextField();
        editForm.add(updateNameField);
        
        editForm.add(new JLabel("Daily Rate ($):"));
        updateRateField = new JTextField();
        editForm.add(updateRateField);
        
        editForm.add(new JLabel("Status:"));
        updateStatusCombo = new JComboBox<>(new String[]{"AVAILABLE", "MAINTENANCE", "DAMAGED"});
        editForm.add(updateStatusCombo);
        
        JButton updateBtn = new JButton("Update Info");
        updateBtn.addActionListener(e -> handleUpdateEquipment());
        editForm.add(updateBtn);
        
        JButton removeBtn = new JButton("Remove Item");
        removeBtn.setBackground(new Color(231, 76, 60));
        removeBtn.setForeground(Color.WHITE);
        removeBtn.addActionListener(e -> handleRemoveEquipment());
        editForm.add(removeBtn);
        
        opsPanel.add(editForm);
        
        panel.add(opsPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createAdminRentalsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        String[] columns = {"Record ID", "Renter ID", "Renter Name", "Equipment ID", "Equipment Name", "Duration (Days)", "Status", "Deposit ($)"};
        adminRentalsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        adminRentalsTable = new JTable(adminRentalsModel);
        panel.add(new JScrollPane(adminRentalsTable), BorderLayout.CENTER);
        
        return panel;
    }

    // ==========================================
    // USER TABS CREATION (Self-Service)
    // ==========================================

    private JPanel createUserRentTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Available Catalog Table
        String[] columns = {"ID", "Name", "Category", "Daily Rate ($)"};
        userCatalogModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        userCatalogTable = new JTable(userCatalogModel);
        userCatalogTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        userCatalogTable.getSelectionModel().addListSelectionListener(e -> updateCartSummary());
        
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.add(new JLabel("Select items to rent (Hold Ctrl to select multiple items):"), BorderLayout.NORTH);
        leftPanel.add(new JScrollPane(userCatalogTable), BorderLayout.CENTER);
        panel.add(leftPanel, BorderLayout.CENTER);
        
        // Checkout details
        JPanel checkoutPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        checkoutPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Self-Service Checkout", TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 13)
        ));
        
        JPanel durPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        durPanel.add(new JLabel("Rental Duration (Days): "));
        rentDurationField = new JTextField("3", 5);
        rentDurationField.addActionListener(e -> updateCartSummary());
        durPanel.add(rentDurationField);
        checkoutPanel.add(durPanel);
        
        cartSummaryLabel = new JLabel("<html>Items Selected: 0<br/>Total Deposit ($50/item): $0.00<br/>Estimated Rental Fee: $0.00<br/><b>Total Pay Now: $0.00</b></html>");
        cartSummaryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        checkoutPanel.add(cartSummaryLabel);
        
        JButton recalculateBtn = new JButton("Recalculate Estimate");
        recalculateBtn.addActionListener(e -> updateCartSummary());
        checkoutPanel.add(recalculateBtn);

        JButton checkoutBtn = new JButton("Confirm Pay & Checkout");
        checkoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        checkoutBtn.setBackground(new Color(46, 204, 113));
        checkoutBtn.setForeground(Color.WHITE);
        checkoutBtn.addActionListener(e -> handleCheckout());
        checkoutPanel.add(checkoutBtn);
        
        panel.add(checkoutPanel, BorderLayout.EAST);
        
        return panel;
    }

    private JPanel createUserReturnTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        String[] columns = {"Record ID", "Equipment Name", "Planned Duration (Days)", "Rent Date", "Deposit ($)"};
        userRentalsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        userRentalsTable = new JTable(userRentalsModel);
        userRentalsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.add(new JLabel("Your Active Rentals:"), BorderLayout.NORTH);
        leftPanel.add(new JScrollPane(userRentalsTable), BorderLayout.CENTER);
        
        // Return details
        JPanel returnForm = new JPanel(new GridLayout(4, 2, 5, 5));
        returnForm.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Return Settlement Form", TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12)
        ));
        
        returnForm.add(new JLabel("Actual Duration (Days):"));
        returnDaysField = new JTextField();
        returnForm.add(returnDaysField);
        
        returnForm.add(new JLabel("Is Item Damaged?"));
        damageCheck = new JCheckBox("Yes");
        returnForm.add(damageCheck);
        
        JButton returnBtn = new JButton("Process Settle & Return");
        returnBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        returnBtn.setBackground(new Color(41, 128, 185));
        returnBtn.setForeground(Color.WHITE);
        returnBtn.addActionListener(e -> handleReturn());
        returnForm.add(returnBtn);
        
        leftPanel.add(returnForm, BorderLayout.SOUTH);
        panel.add(leftPanel, BorderLayout.CENTER);
        
        // Receipt display
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Billing Settlement Receipt", TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12)
        ));
        receiptArea = new JTextArea(15, 25);
        receiptArea.setEditable(false);
        receiptArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        rightPanel.add(new JScrollPane(receiptArea), BorderLayout.CENTER);
        
        panel.add(rightPanel, BorderLayout.EAST);
        
        return panel;
    }

    // ==========================================
    // EVENT & DATA ACTIONS
    // ==========================================

    private void handleInventorySelect() {
        int selected = adminInventoryTable.getSelectedRow();
        if (selected != -1) {
            String name = (String) adminInventoryModel.getValueAt(selected, 1);
            String rateStr = (String) adminInventoryModel.getValueAt(selected, 3);
            String status = (String) adminInventoryModel.getValueAt(selected, 4);
            
            updateNameField.setText(name);
            updateRateField.setText(rateStr);
            updateStatusCombo.setSelectedItem(status);
        }
    }

    private void handleAddEquipment() {
        String id = addIdField.getText().trim();
        String name = addNameField.getText().trim();
        String category = (String) addCategoryCombo.getSelectedItem();
        String rateStr = addRateField.getText().trim();
        
        if (id.isEmpty() || name.isEmpty() || rateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            double rate = Double.parseDouble(rateStr);
            facade.addEquipment(id, name, category, rate);
            JOptionPane.showMessageDialog(this, "Equipment added successfully!");
            addIdField.setText("");
            addNameField.setText("");
            addRateField.setText("");
            refreshAllData();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Daily rate must be a valid decimal.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdateEquipment() {
        int selected = adminInventoryTable.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to update.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = (String) adminInventoryModel.getValueAt(selected, 0);
        String name = updateNameField.getText().trim();
        String rateStr = updateRateField.getText().trim();
        String status = (String) updateStatusCombo.getSelectedItem();
        
        if (name.isEmpty() || rateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fields cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            double rate = Double.parseDouble(rateStr);
            facade.updateEquipment(id, name, rate, status);
            JOptionPane.showMessageDialog(this, "Equipment updated!");
            refreshAllData();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid rate.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRemoveEquipment() {
        int selected = adminInventoryTable.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to remove.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = (String) adminInventoryModel.getValueAt(selected, 0);
        facade.removeEquipment(id);
        JOptionPane.showMessageDialog(this, "Equipment removed.");
        refreshAllData();
    }

    private void updateCartSummary() {
        int[] rows = userCatalogTable.getSelectedRows();
        String durStr = rentDurationField.getText().trim();
        int days = 1;
        try {
            days = Integer.parseInt(durStr);
        } catch (NumberFormatException e) {
            // Keep 1 day
        }
        
        double estRent = 0.0;
        double deposit = rows.length * 50.00;
        
        for (int row : rows) {
            String id = (String) userCatalogModel.getValueAt(row, 0);
            Equipment eq = facade.getAllEquipment().stream().filter(e -> e.getEquipmentId().equals(id)).findFirst().orElse(null);
            if (eq != null) {
                double base = eq.calculateBaseFee(days);
                // Simple discount preview
                double discount = 0.0;
                if (facade.getCurrentUser().getType() == model.User.UserType.STAFF) {
                    discount = base * 0.20;
                } else if (facade.getCurrentUser().getType() == model.User.UserType.FINAL_YEAR_STUDENT) {
                    discount = base * 0.10;
                }
                estRent += (base - discount);
            }
        }
        
        cartSummaryLabel.setText(String.format(
            "<html>Items Selected: %d<br/>Total Deposit ($50/item): $%.2f<br/>Estimated Rental Fee: $%.2f<br/><b>Total Pay Now: $%.2f</b></html>",
            rows.length, deposit, estRent, deposit + estRent
        ));
    }

    private void handleCheckout() {
        int[] rows = userCatalogTable.getSelectedRows();
        if (rows.length == 0) {
            JOptionPane.showMessageDialog(this, "Please select at least 1 item to rent.", "Cart Empty", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String durStr = rentDurationField.getText().trim();
        try {
            int duration = Integer.parseInt(durStr);
            List<String> ids = new ArrayList<>();
            for (int r : rows) {
                ids.add((String) userCatalogModel.getValueAt(r, 0));
            }
            
            String response = facade.rentEquipmentList(ids, duration);
            JOptionPane.showMessageDialog(this, response);
            refreshAllData();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid duration value.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleReturn() {
        int selected = userRentalsTable.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Please select a record from the table.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String recordId = (String) userRentalsModel.getValueAt(selected, 0);
        String actDaysStr = returnDaysField.getText().trim();
        boolean damaged = damageCheck.isSelected();
        
        try {
            int days = Integer.parseInt(actDaysStr);
            String result = facade.returnEquipment(recordId, days, damaged);
            receiptArea.setText(result);
            returnDaysField.setText("");
            damageCheck.setSelected(false);
            refreshAllData();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid duration days.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshAllData() {
        if (isAdmin) {
            // Admin Table
            adminInventoryModel.setRowCount(0);
            for (Equipment eq : facade.getAllEquipment()) {
                adminInventoryModel.addRow(new Object[]{
                    eq.getEquipmentId(),
                    eq.getName(),
                    eq.getCategory(),
                    String.format("%.2f", eq.getDailyRentalRate()),
                    eq.getStatus().toString()
                });
            }
            
            // Logs
            adminRentalsModel.setRowCount(0);
            for (RentalRecord r : facade.getAllRentals()) {
                adminRentalsModel.addRow(new Object[]{
                    r.getRecordId(),
                    r.getUser().getUserId(),
                    r.getUser().getName(),
                    r.getEquipment().getEquipmentId(),
                    r.getEquipment().getName(),
                    r.getPlannedDurationDays(),
                    r.getStatus().toString(),
                    String.format("%.2f", r.getDepositPaid())
                });
            }
        } else {
            // User Catalog
            userCatalogModel.setRowCount(0);
            for (Equipment eq : facade.getAvailableEquipment()) {
                userCatalogModel.addRow(new Object[]{
                    eq.getEquipmentId(),
                    eq.getName(),
                    eq.getCategory(),
                    String.format("%.2f", eq.getDailyRentalRate())
                });
            }
            updateCartSummary();
            
            // User Rentals
            userRentalsModel.setRowCount(0);
            for (RentalRecord r : facade.getCurrentUserActiveRentals()) {
                userRentalsModel.addRow(new Object[]{
                    r.getRecordId(),
                    r.getEquipment().getName(),
                    r.getPlannedDurationDays(),
                    r.getRentDate().toString(),
                    String.format("%.2f", r.getDepositPaid())
                });
            }
        }
    }
}
