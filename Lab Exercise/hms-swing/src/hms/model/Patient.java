package hms.model;

public class Patient {
    private final int id;
    private String name;
    private int age;
    private String gender;
    private String medicalHistory;

    public Patient(int id, String name, int age, String gender, String medicalHistory) {
        this.id = id;
        update(name, age, gender, medicalHistory);
    }

    public void update(String name, int age, String gender, String medicalHistory) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Patient name is required.");
        }
        if (age <= 0) {
            throw new IllegalArgumentException("Patient age must be greater than zero.");
        }
        if (gender == null || gender.isBlank()) {
            throw new IllegalArgumentException("Patient gender is required.");
        }
        this.name = name.trim();
        this.age = age;
        this.gender = gender.trim();
        this.medicalHistory = medicalHistory == null ? "" : medicalHistory.trim();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    @Override
    public String toString() {
        return id + " - " + name;
    }
}
