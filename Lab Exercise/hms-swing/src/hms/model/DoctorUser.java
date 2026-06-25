package hms.model;

public class DoctorUser extends User {
    public DoctorUser(String username, String password, String displayName) {
        super(username, password, displayName, Role.DOCTOR);
    }

    @Override
    public String getHomeMessage() {
        return "Doctor can review patients, appointments, and schedules.";
    }
}
