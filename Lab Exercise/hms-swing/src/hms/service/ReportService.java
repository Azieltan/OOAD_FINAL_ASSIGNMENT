package hms.service;

import java.util.Comparator;
import java.util.List;

public class ReportService {
    private final HospitalRepository repository;

    public ReportService(HospitalRepository repository) {
        this.repository = repository;
    }

    public ReportSummary generateReport() {
        List<String> scheduleLines = repository.getAppointments().stream()
                .sorted(Comparator.comparing(appointment -> appointment.getDate().atTime(appointment.getTime())))
                .map(appointment -> appointment.toScheduleLine())
                .toList();

        return new ReportSummary(
                repository.getPatients().size(),
                repository.getAppointments().size(),
                scheduleLines
        );
    }
}
