package hms.model;

public class Doctor {
    private final int id;
    private String name;
    private String specialization;

    public Doctor(int id, String name, String specialization) {
        this.id = id;
        update(name, specialization);
    }

    public void update(String name, String specialization) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Doctor name is required.");
        }
        if (specialization == null || specialization.isBlank()) {
            throw new IllegalArgumentException("Specialization is required.");
        }
        this.name = name.trim();
        this.specialization = specialization.trim();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSpecialization() {
        return specialization;
    }

    @Override
    public String toString() {
        return id + " - " + name + " (" + specialization + ")";
    }
}
