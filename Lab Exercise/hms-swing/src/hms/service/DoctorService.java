package hms.service;

import hms.model.Doctor;

import java.util.List;

public class DoctorService {
    private final HospitalRepository repository;

    public DoctorService(HospitalRepository repository) {
        this.repository = repository;
    }

    public Doctor addDoctor(String name, String specialization) {
        return repository.addDoctor(name, specialization);
    }

    public Doctor findDoctor(int doctorId) {
        return repository.getDoctors().stream()
                .filter(doctor -> doctor.getId() == doctorId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found."));
    }

    public List<Doctor> getDoctors() {
        return repository.getDoctors();
    }
}
