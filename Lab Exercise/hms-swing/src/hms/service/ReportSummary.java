package hms.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReportSummary {
    private final int totalPatients;
    private final int totalAppointments;
    private final List<String> doctorScheduleLines;

    public ReportSummary(int totalPatients, int totalAppointments, List<String> doctorScheduleLines) {
        this.totalPatients = totalPatients;
        this.totalAppointments = totalAppointments;
        this.doctorScheduleLines = new ArrayList<>(doctorScheduleLines);
    }

    public int getTotalPatients() {
        return totalPatients;
    }

    public int getTotalAppointments() {
        return totalAppointments;
    }

    public List<String> getDoctorScheduleLines() {
        return Collections.unmodifiableList(doctorScheduleLines);
    }
}
