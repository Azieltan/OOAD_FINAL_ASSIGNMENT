package hms.service;

import hms.model.Patient;

import java.util.List;

public class PatientService {
    private final HospitalRepository repository;

    public PatientService(HospitalRepository repository) {
        this.repository = repository;
    }

    public Patient addPatient(String name, int age, String gender, String medicalHistory) {
        return repository.addPatient(name, age, gender, medicalHistory);
    }

    public void updatePatient(int patientId, String name, int age, String gender, String medicalHistory) {
        findPatient(patientId).update(name, age, gender, medicalHistory);
    }

    public Patient findPatient(int patientId) {
        return repository.getPatients().stream()
                .filter(patient -> patient.getId() == patientId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Patient not found."));
    }

    public List<Patient> getPatients() {
        return repository.getPatients();
    }
}
