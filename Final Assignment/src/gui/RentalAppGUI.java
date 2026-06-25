package gui;

import facade.RentalSystemFacade;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
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
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import model.Equipment;
import model.RentalRecord;

public class RentalAppGUI extends JFrame {
    private RentalSystemFacade facade;
    
    // UI Elements - Catalog Tab
    private JTable equipmentTable;
    private DefaultTableModel catalogModel;
    private JTextField addIdField, addNameField, addRateField;
    private JComboBox<String> addCategoryCombo;
    
    // UI Elements - Rent Tab
    private JTextField rentUserIdField, rentUserNameField, rentDurationField;
    private JComboBox<String> rentUserTypeCombo;
    private JComboBox<String> rentEquipmentCombo;
    
    // UI Elements - Return Tab
    private JTable activeRentalsTable;
    private DefaultTableModel activeRentalsModel;
    private JTextField returnDaysField;
    private JCheckBox damageCheck;
    private JTextArea receiptArea;

    public RentalAppGUI() {
        this.facade = new RentalSystemFacade();
        initializeUI();
        refreshAllData();
    }

    private void initializeUI() {
        setTitle("Smart Equipment Rental & Billing System");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Colors & Fonts
        Color headerColor = new Color(41, 128, 185);
        Font titleFont = new Font("Segoe UI", Font.BOLD, 18);
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(headerColor);
        JLabel titleLabel = new JLabel("CAMPUS SMART EQUIPMENT RENTAL & BILLING SYSTEM");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(titleFont);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);
        
        // Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        tabbedPane.addTab("Equipment Catalog", createCatalogTab());
        tabbedPane.addTab("Rent Equipment", createRentTab());
        tabbedPane.addTab("Return & Billing", createReturnTab());
        
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createCatalogTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Equipment Table
        String[] columns = {"ID", "Name", "Category", "Daily Rate ($)", "Availability"};
        catalogModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        equipmentTable = new JTable(catalogModel);
        panel.add(new JScrollPane(equipmentTable), BorderLayout.CENTER);
        
        // Add Equipment Form
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Add New Equipment", TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12)
        ));
        
        formPanel.add(new JLabel("Equipment ID:"));
        addIdField = new JTextField();
        formPanel.add(addIdField);
        
        formPanel.add(new JLabel("Name:"));
        addNameField = new JTextField();
        formPanel.add(addNameField);
        
        formPanel.add(new JLabel("Category:"));
        addCategoryCombo = new JComboBox<>(new String[]{"Electronics", "Media Equipment", "Laboratory Equipment"});
        formPanel.add(addCategoryCombo);
        
        formPanel.add(new JLabel("Daily Rental Rate ($):"));
        addRateField = new JTextField();
        formPanel.add(addRateField);
        
        JButton addBtn = new JButton("Add Equipment");
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addBtn.addActionListener(e -> handleAddEquipment());
        formPanel.add(addBtn);
        
        panel.add(formPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createRentTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Rental Registration Form", TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14)
        ));
        
        formPanel.add(new JLabel("Renter User ID:"));
        rentUserIdField = new JTextField();
        formPanel.add(rentUserIdField);
        
        formPanel.add(new JLabel("Renter Name:"));
        rentUserNameField = new JTextField();
        formPanel.add(rentUserNameField);
        
        formPanel.add(new JLabel("User Type:"));
        rentUserTypeCombo = new JComboBox<>(new String[]{"Student", "Staff", "Final Year Student"});
        formPanel.add(rentUserTypeCombo);
        
        formPanel.add(new JLabel("Select Equipment:"));
        rentEquipmentCombo = new JComboBox<>();
        formPanel.add(rentEquipmentCombo);
        
        formPanel.add(new JLabel("Planned Duration (Days):"));
        rentDurationField = new JTextField();
        formPanel.add(rentDurationField);
        
        JButton rentBtn = new JButton("Process Rental Checkout");
        rentBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        rentBtn.addActionListener(e -> handleRentEquipment());
        formPanel.add(rentBtn);
        
        panel.add(formPanel, BorderLayout.NORTH);
        return panel;
    }

    private JPanel createReturnTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Active rentals table
        String[] columns = {"Record ID", "Renter Name", "Equipment Name", "Planned Days", "Status"};
        activeRentalsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        activeRentalsTable = new JTable(activeRentalsModel);
        
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.add(new JLabel("Active Rental Transactions:"), BorderLayout.NORTH);
        leftPanel.add(new JScrollPane(activeRentalsTable), BorderLayout.CENTER);
        
        // Return processing form
        JPanel returnForm = new JPanel(new GridLayout(4, 2, 5, 5));
        returnForm.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Return & Penalty Processing", TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12)
        ));
        
        returnForm.add(new JLabel("Actual Rental Duration (Days):"));
        returnDaysField = new JTextField();
        returnForm.add(returnDaysField);
        
        returnForm.add(new JLabel("Equipment Damaged?"));
        damageCheck = new JCheckBox("Yes (Apply damage penalties)");
        returnForm.add(damageCheck);
        
        JButton returnBtn = new JButton("Process Return & Billing");
        returnBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        returnBtn.addActionListener(e -> handleReturnEquipment());
        returnForm.add(returnBtn);
        
        leftPanel.add(returnForm, BorderLayout.SOUTH);
        panel.add(leftPanel, BorderLayout.CENTER);
        
        // Receipt Output Display
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Billing & Detailed Receipt Output", TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12)
        ));
        receiptArea = new JTextArea(15, 25);
        receiptArea.setEditable(false);
        receiptArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        rightPanel.add(new JScrollPane(receiptArea), BorderLayout.CENTER);
        
        panel.add(rightPanel, BorderLayout.EAST);
        
        return panel;
    }

    // --- Action Handlers ---
    
    private void handleAddEquipment() {
        String id = addIdField.getText().trim();
        String name = addNameField.getText().trim();
        String category = (String) addCategoryCombo.getSelectedItem();
        String rateStr = addRateField.getText().trim();
        
        if (id.isEmpty() || name.isEmpty() || rateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields to add equipment.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            double rate = Double.parseDouble(rateStr);
            if (rate <= 0) {
                JOptionPane.showMessageDialog(this, "Daily rate must be greater than zero.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            facade.addEquipment(id, name, category, rate);
            JOptionPane.showMessageDialog(this, "Equipment added successfully!");
            
            // Clear fields
            addIdField.setText("");
            addNameField.setText("");
            addRateField.setText("");
            
            refreshAllData();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Daily rate must be a valid decimal number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRentEquipment() {
        String userId = rentUserIdField.getText().trim();
        String name = rentUserNameField.getText().trim();
        String userType = (String) rentUserTypeCombo.getSelectedItem();
        String equipSelection = (String) rentEquipmentCombo.getSelectedItem();
        String durationStr = rentDurationField.getText().trim();
        
        if (equipSelection == null) {
            JOptionPane.showMessageDialog(this, "No available equipment selected.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String equipId = equipSelection.split(" - ")[0];
        
        try {
            int duration = Integer.parseInt(durationStr);
            String response = facade.rentEquipment(userId, name, userType, equipId, duration);
            JOptionPane.showMessageDialog(this, response);
            
            if (response.startsWith("Success")) {
                rentUserIdField.setText("");
                rentUserNameField.setText("");
                rentDurationField.setText("");
                refreshAllData();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Duration must be a valid integer number of days.", "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleReturnEquipment() {
        int selectedRow = activeRentalsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an active rental transaction from the table.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String recordId = (String) activeRentalsModel.getValueAt(selectedRow, 0);
        String actualDaysStr = returnDaysField.getText().trim();
        boolean isDamaged = damageCheck.isSelected();
        
        try {
            int actualDays = Integer.parseInt(actualDaysStr);
            String result = facade.returnEquipment(recordId, actualDays, isDamaged);
            receiptArea.setText(result);
            
            if (result.startsWith("Success")) {
                returnDaysField.setText("");
                damageCheck.setSelected(false);
                refreshAllData();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Actual rental duration must be a valid integer number of days.", "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshAllData() {
        // 1. Refresh Catalog Table
        catalogModel.setRowCount(0);
        List<Equipment> allEquip = facade.getAllEquipment();
        for (Equipment eq : allEquip) {
            catalogModel.addRow(new Object[]{
                eq.getEquipmentId(),
                eq.getName(),
                eq.getCategory(),
                String.format("%.2f", eq.getDailyRentalRate()),
                eq.isAvailable() ? "Available" : "Rented"
            });
        }
        
        // 2. Refresh Rent Combo Box
        rentEquipmentCombo.removeAllItems();
        List<Equipment> availEquip = facade.getAvailableEquipment();
        for (Equipment eq : availEquip) {
            rentEquipmentCombo.addItem(eq.getEquipmentId() + " - " + eq.getName());
        }
        
        // 3. Refresh Active Rentals Table
        activeRentalsModel.setRowCount(0);
        List<RentalRecord> activeRentals = facade.getActiveRentals();
        for (RentalRecord r : activeRentals) {
            activeRentalsModel.addRow(new Object[]{
                r.getRecordId(),
                r.getUser().getName(),
                r.getEquipment().getName(),
                r.getPlannedDurationDays(),
                r.getStatus().toString()
            });
        }
    }
}
