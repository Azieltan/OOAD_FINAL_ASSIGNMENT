package hms.service;

import hms.model.AdminUser;
import hms.model.Appointment;
import hms.model.Doctor;
import hms.model.DoctorUser;
import hms.model.Patient;
import hms.model.ReceptionistUser;
import hms.model.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HospitalRepository {
    private final List<User> users = new ArrayList<>();
    private final List<Patient> patients = new ArrayList<>();
    private final List<Doctor> doctors = new ArrayList<>();
    private final List<Appointment> appointments = new ArrayList<>();

    private int nextPatientId = 1;
    private int nextDoctorId = 1;
    private int nextAppointmentId = 1;

    public static HospitalRepository empty() {
        HospitalRepository repository = new HospitalRepository();
        repository.addUser(new AdminUser("admin", "admin123", "Admin User"));
        repository.addUser(new DoctorUser("doctor", "doctor123", "Doctor User"));
        repository.addUser(new ReceptionistUser("reception", "reception123", "Receptionist User"));
        return repository;
    }

    public static HospitalRepository seeded() {
        HospitalRepository repository = empty();
        Patient patient = repository.addPatient("Ali Hassan", 35, "Male", "High blood pressure");
        Doctor doctor = repository.addDoctor("Dr. Kumar", "Cardiology");
        repository.addDoctor("Dr. Aina", "General Medicine");
        repository.addAppointment(patient, doctor, LocalDate.of(2026, 6, 20), LocalTime.of(9, 0));
        return repository;
    }

    public void addUser(User user) {
        users.add(user);
    }

    public Patient addPatient(String name, int age, String gender, String medicalHistory) {
        Patient patient = new Patient(nextPatientId++, name, age, gender, medicalHistory);
        patients.add(patient);
        return patient;
    }

    public Doctor addDoctor(String name, String specialization) {
        Doctor doctor = new Doctor(nextDoctorId++, name, specialization);
        doctors.add(doctor);
        return doctor;
    }

    public Appointment addAppointment(Patient patient, Doctor doctor, LocalDate date, LocalTime time) {
        Appointment appointment = new Appointment(nextAppointmentId++, patient, doctor, date, time);
        appointments.add(appointment);
        return appointment;
    }

    public List<User> getUsers() {
        return Collections.unmodifiableList(users);
    }

    public List<Patient> getPatients() {
        return Collections.unmodifiableList(patients);
    }

    public List<Doctor> getDoctors() {
        return Collections.unmodifiableList(doctors);
    }

    public List<Appointment> getAppointments() {
        return Collections.unmodifiableList(appointments);
    }
}
