package hms.service;

import hms.model.User;

public class AuthenticationService {
    private final HospitalRepository repository;

    public AuthenticationService(HospitalRepository repository) {
        this.repository = repository;
    }

    public User login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return null;
        }
        for (User user : repository.getUsers()) {
            if (user.getUsername().equalsIgnoreCase(username.trim()) && user.matchesPassword(password)) {
                return user;
            }
        }
        return null;
    }
}
