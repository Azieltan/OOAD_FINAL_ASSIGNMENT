package hms.tests;

import hms.controller.SystemController;
import hms.model.Appointment;
import hms.model.Doctor;
import hms.model.Patient;
import hms.model.Role;
import hms.model.User;
import hms.service.DuplicateAppointmentException;
import hms.service.HospitalRepository;
import hms.service.ReportSummary;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class HospitalSystemTest {
    private int passed = 0;
    private int failed = 0;

    public static void main(String[] args) {
        HospitalSystemTest test = new HospitalSystemTest();
        test.run();
    }

    private void run() {
        testLoginAcceptsKnownRoles();
        testPatientCanBeAddedAndUpdated();
        testDuplicateDoctorTimeSlotIsRejected();
        testReportIncludesTotalsAndDoctorSchedules();

        if (failed > 0) {
            throw new AssertionError(failed + " test(s) failed, " + passed + " passed");
        }
        System.out.println("All " + passed + " HMS tests passed.");
    }

    private void testLoginAcceptsKnownRoles() {
        SystemController controller = new SystemController(HospitalRepository.seeded());

        User admin = controller.login("admin", "admin123");
        User doctor = controller.login("doctor", "doctor123");
        User receptionist = controller.login("reception", "reception123");

        check(admin.getRole() == Role.ADMIN, "admin login should return ADMIN role");
        check(doctor.getRole() == Role.DOCTOR, "doctor login should return DOCTOR role");
        check(receptionist.getRole() == Role.RECEPTIONIST, "receptionist login should return RECEPTIONIST role");
        check(controller.login("admin", "wrong") == null, "wrong password should fail login");
    }

    private void testPatientCanBeAddedAndUpdated() {
        SystemController controller = new SystemController(HospitalRepository.empty());

        Patient patient = controller.addPatient("Aina Rahman", 31, "Female", "Asthma");
        controller.updatePatient(patient.getId(), "Aina Rahman", 32, "Female", "Asthma, annual checkup");

        Patient updated = controller.findPatient(patient.getId());
        check(updated.getAge() == 32, "patient age should update");
        check(updated.getMedicalHistory().contains("annual checkup"), "patient history should update");
        check(controller.getPatients().size() == 1, "one patient should be stored");
    }

    private void testDuplicateDoctorTimeSlotIsRejected() {
        SystemController controller = new SystemController(HospitalRepository.empty());
        Patient patient = controller.addPatient("Tan Wei", 44, "Male", "Diabetes");
        Doctor doctor = controller.addDoctor("Dr. Lim", "Cardiology");

        Appointment first = controller.bookAppointment(
                patient.getId(),
                doctor.getId(),
                LocalDate.of(2026, 6, 20),
                LocalTime.of(10, 30)
        );

        check(first.getDoctor().equals(doctor), "first appointment should link selected doctor");

        try {
            controller.bookAppointment(
                    patient.getId(),
                    doctor.getId(),
                    LocalDate.of(2026, 6, 20),
                    LocalTime.of(10, 30)
            );
            fail("duplicate doctor time slot should throw DuplicateAppointmentException");
        } catch (DuplicateAppointmentException expected) {
            pass();
        }
    }

    private void testReportIncludesTotalsAndDoctorSchedules() {
        SystemController controller = new SystemController(HospitalRepository.empty());
        Patient patient = controller.addPatient("Siti Aminah", 26, "Female", "Migraine");
        Doctor doctor = controller.addDoctor("Dr. Kumar", "Neurology");
        controller.bookAppointment(patient.getId(), doctor.getId(), LocalDate.of(2026, 6, 21), LocalTime.of(14, 0));

        ReportSummary summary = controller.generateReport();
        List<String> scheduleLines = summary.getDoctorScheduleLines();

        check(summary.getTotalPatients() == 1, "report should count patients");
        check(summary.getTotalAppointments() == 1, "report should count appointments");
        check(scheduleLines.size() == 1, "report should include one doctor schedule line");
        check(scheduleLines.get(0).contains("Dr. Kumar"), "schedule line should include doctor name");
        check(scheduleLines.get(0).contains("2026-06-21 14:00"), "schedule line should include date and time");
    }

    private void check(boolean condition, String message) {
        if (condition) {
            pass();
        } else {
            fail(message);
        }
    }

    private void pass() {
        passed++;
    }

    private void fail(String message) {
        failed++;
        System.err.println("FAIL: " + message);
    }
}
