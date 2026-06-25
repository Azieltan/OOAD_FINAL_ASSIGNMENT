package hms.service;

public class DuplicateAppointmentException extends RuntimeException {
    public DuplicateAppointmentException(String message) {
        super(message);
    }
}
