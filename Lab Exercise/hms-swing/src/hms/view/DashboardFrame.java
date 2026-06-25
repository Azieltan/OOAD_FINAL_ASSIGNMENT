package hms.view;

import hms.controller.SystemController;
import hms.model.Appointment;
import hms.model.Doctor;
import hms.model.Patient;
import hms.model.Role;
import hms.model.User;
import hms.service.DuplicateAppointmentException;
import hms.service.ReportSummary;

import javax.swing.JButton;
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
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class DashboardFrame extends JFrame {
    private final SystemController controller;
    private final User currentUser;

    private final DefaultTableModel patientModel = new DefaultTableModel(new String[]{"ID", "Name", "Age", "Gender", "History"}, 0);
    private final JTable patientTable = new JTable(patientModel);
    private final JTextField patientNameField = new JTextField(16);
    private final JTextField patientAgeField = new JTextField(5);
    private final JTextField patientGenderField = new JTextField(10);
    private final JTextField patientHistoryField = new JTextField(24);
    private int selectedPatientId = -1;

    private final DefaultTableModel doctorModel = new DefaultTableModel(new String[]{"ID", "Name", "Specialization"}, 0);
    private final JTextField doctorNameField = new JTextField(16);
    private final JTextField specializationField = new JTextField(16);

    private final DefaultTableModel appointmentModel = new DefaultTableModel(new String[]{"ID", "Patient", "Doctor", "Date", "Time"}, 0);
    private final JComboBox<Patient> patientCombo = new JComboBox<>();
    private final JComboBox<Doctor> doctorCombo = new JComboBox<>();
    private final JTextField dateField = new JTextField("2026-06-22", 10);
    private final JTextField timeField = new JTextField("10:00", 8);

    private final JTextArea reportArea = new JTextArea(12, 48);

    public DashboardFrame(SystemController controller, User currentUser) {
        super("Hospital Management System - Dashboard");
        this.controller = controller;
        this.currentUser = currentUser;
        buildUi();
        refreshAll();
    }

    private void buildUi() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 620);
        setLocationRelativeTo(null);

        JLabel header = new JLabel("Logged in as " + currentUser.getDisplayName() + " (" + currentUser.getRole() + ") - " + currentUser.getHomeMessage());
        header.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 12, 10, 12));
        add(header, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Patients", buildPatientPanel());
        tabs.addTab("Doctors", buildDoctorPanel());
        tabs.addTab("Appointments", buildAppointmentPanel());
        tabs.addTab("Reports", buildReportPanel());

        if (currentUser.getRole() == Role.DOCTOR) {
            tabs.setEnabledAt(1, false);
        }

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildPatientPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        patientTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                int row = patientTable.getSelectedRow();
                if (row >= 0) {
                    selectedPatientId = (int) patientModel.getValueAt(row, 0);
                    patientNameField.setText((String) patientModel.getValueAt(row, 1));
                    patientAgeField.setText(String.valueOf(patientModel.getValueAt(row, 2)));
                    patientGenderField.setText((String) patientModel.getValueAt(row, 3));
                    patientHistoryField.setText((String) patientModel.getValueAt(row, 4));
                }
            }
        });
        panel.add(new JScrollPane(patientTable), BorderLayout.CENTER);

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.add(new JLabel("Name"));
        form.add(patientNameField);
        form.add(new JLabel("Age"));
        form.add(patientAgeField);
        form.add(new JLabel("Gender"));
        form.add(patientGenderField);
        form.add(new JLabel("History"));
        form.add(patientHistoryField);

        JButton addButton = new JButton("Add");
        addButton.addActionListener(event -> addPatient());
        JButton updateButton = new JButton("Update Selected");
        updateButton.addActionListener(event -> updatePatient());
        form.add(addButton);
        form.add(updateButton);

        if (currentUser.getRole() == Role.DOCTOR) {
            addButton.setEnabled(false);
            updateButton.setEnabled(false);
        }

        panel.add(form, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildDoctorPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        JTable doctorTable = new JTable(doctorModel);
        panel.add(new JScrollPane(doctorTable), BorderLayout.CENTER);

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.add(new JLabel("Name"));
        form.add(doctorNameField);
        form.add(new JLabel("Specialization"));
        form.add(specializationField);

        JButton addButton = new JButton("Add Doctor");
        addButton.addActionListener(event -> addDoctor());
        form.add(addButton);
        panel.add(form, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildAppointmentPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        JTable appointmentTable = new JTable(appointmentModel);
        panel.add(new JScrollPane(appointmentTable), BorderLayout.CENTER);

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.add(new JLabel("Patient"));
        form.add(patientCombo);
        form.add(new JLabel("Doctor"));
        form.add(doctorCombo);
        form.add(new JLabel("Date"));
        form.add(dateField);
        form.add(new JLabel("Time"));
        form.add(timeField);

        JButton bookButton = new JButton("Book");
        bookButton.addActionListener(event -> bookAppointment());
        form.add(bookButton);

        if (currentUser.getRole() == Role.DOCTOR) {
            bookButton.setEnabled(false);
        }

        panel.add(form, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        reportArea.setEditable(false);
        panel.add(new JScrollPane(reportArea), BorderLayout.CENTER);

        JPanel actions = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        JButton refreshButton = new JButton("Generate Report");
        refreshButton.addActionListener(event -> refreshReport());
        actions.add(refreshButton, gbc);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private void addPatient() {
        try {
            controller.addPatient(
                    patientNameField.getText(),
                    Integer.parseInt(patientAgeField.getText().trim()),
                    patientGenderField.getText(),
                    patientHistoryField.getText()
            );
            clearPatientForm();
            refreshAll();
        } catch (RuntimeException ex) {
            showError(ex.getMessage());
        }
    }

    private void updatePatient() {
        if (selectedPatientId < 0) {
            showError("Select a patient row first.");
            return;
        }
        try {
            controller.updatePatient(
                    selectedPatientId,
                    patientNameField.getText(),
                    Integer.parseInt(patientAgeField.getText().trim()),
                    patientGenderField.getText(),
                    patientHistoryField.getText()
            );
            clearPatientForm();
            refreshAll();
        } catch (RuntimeException ex) {
            showError(ex.getMessage());
        }
    }

    private void addDoctor() {
        try {
            controller.addDoctor(doctorNameField.getText(), specializationField.getText());
            doctorNameField.setText("");
            specializationField.setText("");
            refreshAll();
        } catch (RuntimeException ex) {
            showError(ex.getMessage());
        }
    }

    private void bookAppointment() {
        Patient patient = (Patient) patientCombo.getSelectedItem();
        Doctor doctor = (Doctor) doctorCombo.getSelectedItem();
        if (patient == null || doctor == null) {
            showError("Add at least one patient and one doctor first.");
            return;
        }
        try {
            controller.bookAppointment(
                    patient.getId(),
                    doctor.getId(),
                    LocalDate.parse(dateField.getText().trim()),
                    LocalTime.parse(timeField.getText().trim())
            );
            refreshAll();
        } catch (DuplicateAppointmentException ex) {
            showError(ex.getMessage());
        } catch (DateTimeParseException ex) {
            showError("Use date format YYYY-MM-DD and time format HH:MM.");
        } catch (RuntimeException ex) {
            showError(ex.getMessage());
        }
    }

    private void refreshAll() {
        refreshPatients();
        refreshDoctors();
        refreshAppointments();
        refreshReport();
    }

    private void refreshPatients() {
        patientModel.setRowCount(0);
        patientCombo.removeAllItems();
        for (Patient patient : controller.getPatients()) {
            patientModel.addRow(new Object[]{
                    patient.getId(),
                    patient.getName(),
                    patient.getAge(),
                    patient.getGender(),
                    patient.getMedicalHistory()
            });
            patientCombo.addItem(patient);
        }
    }

    private void refreshDoctors() {
        doctorModel.setRowCount(0);
        doctorCombo.removeAllItems();
        for (Doctor doctor : controller.getDoctors()) {
            doctorModel.addRow(new Object[]{doctor.getId(), doctor.getName(), doctor.getSpecialization()});
            doctorCombo.addItem(doctor);
        }
    }

    private void refreshAppointments() {
        appointmentModel.setRowCount(0);
        for (Appointment appointment : controller.getAppointments()) {
            appointmentModel.addRow(new Object[]{
                    appointment.getId(),
                    appointment.getPatient().getName(),
                    appointment.getDoctor().getName(),
                    appointment.getDate(),
                    appointment.getTime()
            });
        }
    }

    private void refreshReport() {
        ReportSummary summary = controller.generateReport();
        StringBuilder builder = new StringBuilder();
        builder.append("Total Patients: ").append(summary.getTotalPatients()).append('\n');
        builder.append("Total Appointments: ").append(summary.getTotalAppointments()).append('\n');
        builder.append('\n').append("Doctor Schedules").append('\n');
        if (summary.getDoctorScheduleLines().isEmpty()) {
            builder.append("No appointments booked.");
        } else {
            for (String line : summary.getDoctorScheduleLines()) {
                builder.append("- ").append(line).append('\n');
            }
        }
        reportArea.setText(builder.toString());
    }

    private void clearPatientForm() {
        selectedPatientId = -1;
        patientNameField.setText("");
        patientAgeField.setText("");
        patientGenderField.setText("");
        patientHistoryField.setText("");
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Input Error", JOptionPane.ERROR_MESSAGE);
    }
}
