package model;

public class User {
    public enum UserType {
        STUDENT,
        STAFF,
        FINAL_YEAR_STUDENT
    }

    private String userId;
    private String name;
    private UserType type;

    public User(String userId, String name, UserType type) {
        this.userId = userId;
        this.name = name;
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public UserType getType() {
        return type;
    }

    @Override
    public String toString() {
        return name + " (" + type + ")";
    }
}
