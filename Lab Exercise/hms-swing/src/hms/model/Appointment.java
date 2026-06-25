package hms.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Appointment {
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private final int id;
    private final Patient patient;
    private final Doctor doctor;
    private final LocalDate date;
    private final LocalTime time;

    public Appointment(int id, Patient patient, Doctor doctor, LocalDate date, LocalTime time) {
        if (patient == null) {
            throw new IllegalArgumentException("Patient is required.");
        }
        if (doctor == null) {
            throw new IllegalArgumentException("Doctor is required.");
        }
        if (date == null) {
            throw new IllegalArgumentException("Date is required.");
        }
        if (time == null) {
            throw new IllegalArgumentException("Time is required.");
        }
        this.id = id;
        this.patient = patient;
        this.doctor = doctor;
        this.date = date;
        this.time = time;
    }

    public boolean isSameDoctorSlot(int doctorId, LocalDate date, LocalTime time) {
        return doctor.getId() == doctorId && this.date.equals(date) && this.time.equals(time);
    }

    public String toScheduleLine() {
        return date + " " + time.format(TIME_FORMAT) + " - " + doctor.getName() + " with " + patient.getName();
    }

    public int getId() {
        return id;
    }

    public Patient getPatient() {
        return patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }
}
