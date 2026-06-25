package hms.service;

import hms.model.Appointment;
import hms.model.Doctor;
import hms.model.Patient;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AppointmentService {
    private final HospitalRepository repository;
    private final PatientService patientService;
    private final DoctorService doctorService;

    public AppointmentService(HospitalRepository repository, PatientService patientService, DoctorService doctorService) {
        this.repository = repository;
        this.patientService = patientService;
        this.doctorService = doctorService;
    }

    public Appointment bookAppointment(int patientId, int doctorId, LocalDate date, LocalTime time) {
        for (Appointment appointment : repository.getAppointments()) {
            if (appointment.isSameDoctorSlot(doctorId, date, time)) {
                throw new DuplicateAppointmentException("Doctor already has an appointment at this date and time.");
            }
        }

        Patient patient = patientService.findPatient(patientId);
        Doctor doctor = doctorService.findDoctor(doctorId);
        return repository.addAppointment(patient, doctor, date, time);
    }

    public List<Appointment> getAppointments() {
        return repository.getAppointments();
    }
}
