package hms.model;

public class ReceptionistUser extends User {
    public ReceptionistUser(String username, String password, String displayName) {
        super(username, password, displayName, Role.RECEPTIONIST);
    }

    @Override
    public String getHomeMessage() {
        return "Receptionist can register patients and book appointments.";
    }
}
