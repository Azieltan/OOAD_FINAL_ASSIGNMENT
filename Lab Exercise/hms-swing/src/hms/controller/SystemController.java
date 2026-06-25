package hms.controller;

import hms.model.Appointment;
import hms.model.Doctor;
import hms.model.Patient;
import hms.model.User;
import hms.service.AppointmentService;
import hms.service.AuthenticationService;
import hms.service.DoctorService;
import hms.service.HospitalRepository;
import hms.service.PatientService;
import hms.service.ReportService;
import hms.service.ReportSummary;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class SystemController {
    private final AuthenticationService authenticationService;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final AppointmentService appointmentService;
    private final ReportService reportService;

    public SystemController(HospitalRepository repository) {
        patientService = new PatientService(repository);
        doctorService = new DoctorService(repository);
        authenticationService = new AuthenticationService(repository);
        appointmentService = new AppointmentService(repository, patientService, doctorService);
        reportService = new ReportService(repository);
    }

    public User login(String username, String password) {
        return authenticationService.login(username, password);
    }

    public Patient addPatient(String name, int age, String gender, String medicalHistory) {
        return patientService.addPatient(name, age, gender, medicalHistory);
    }

    public void updatePatient(int patientId, String name, int age, String gender, String medicalHistory) {
        patientService.updatePatient(patientId, name, age, gender, medicalHistory);
    }

    public Patient findPatient(int patientId) {
        return patientService.findPatient(patientId);
    }

    public List<Patient> getPatients() {
        return patientService.getPatients();
    }

    public Doctor addDoctor(String name, String specialization) {
        return doctorService.addDoctor(name, specialization);
    }

    public List<Doctor> getDoctors() {
        return doctorService.getDoctors();
    }

    public Appointment bookAppointment(int patientId, int doctorId, LocalDate date, LocalTime time) {
        return appointmentService.bookAppointment(patientId, doctorId, date, time);
    }

    public List<Appointment> getAppointments() {
        return appointmentService.getAppointments();
    }

    public ReportSummary generateReport() {
        return reportService.generateReport();
    }
}
