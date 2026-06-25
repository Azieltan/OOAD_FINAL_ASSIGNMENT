package hms.model;

public abstract class User {
    private final String username;
    private final String password;
    private final String displayName;
    private final Role role;

    protected User(String username, String password, String displayName, Role role) {
        this.username = username;
        this.password = password;
        this.displayName = displayName;
        this.role = role;
    }

    public boolean matchesPassword(String passwordAttempt) {
        return password.equals(passwordAttempt);
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Role getRole() {
        return role;
    }

    public abstract String getHomeMessage();
}
