package hms.model;

public class AdminUser extends User {
    public AdminUser(String username, String password, String displayName) {
        super(username, password, displayName, Role.ADMIN);
    }

    @Override
    public String getHomeMessage() {
        return "Admin can manage doctors, patients, appointments, and reports.";
    }
}
