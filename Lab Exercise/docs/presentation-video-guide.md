# Presentation Video Guide

Follow this exact order because the PDF says the video must follow the assessment rubric order.

## Section 1: Introduction (1 minute)

- Introduce all group members.
- State the project: Hospital Management System using Java Swing.
- Mention the main modules: login, appointment booking, patient records, doctor management, and reporting.

## Section 2: Class Diagram (3 minutes)

- Open `uml/class-diagram.puml`.
- Explain `Patient`, `Doctor`, `Appointment`, `User`, and `SystemController`.
- Explain inheritance: `AdminUser`, `DoctorUser`, and `ReceptionistUser` extend `User`.
- Explain associations:
  - Appointment links one patient and one doctor.
  - SystemController uses services.
  - Services use HospitalRepository.
- Explain why the controller facade keeps the GUI clean.

## Section 3: Sequence Diagrams (3 minutes)

- Open the sequence diagrams in this order:
  - `uml/sequence-login.puml`
  - `uml/sequence-book-appointment.puml`
  - `uml/sequence-add-patient.puml`
  - `uml/sequence-generate-report.puml`
- For each diagram, explain the actor, GUI, controller, service, and repository flow.
- For appointment booking, highlight duplicate slot checking.

## Section 4: System Demonstration (5-6 minutes)

- Run the app with:

```powershell
java -cp hms-swing/out hms.app.HospitalManagementApp
```

- Login as Admin using `admin` / `admin123`.
- Add a new patient.
- Add a new doctor.
- Book an appointment.
- Try booking the same doctor, date, and time again to show duplicate prevention.
- Open Reports and show updated totals and doctor schedules.
- Optional: login as Doctor to show role-based restrictions.

## Section 5: Code Explanation (2 minutes)

- Show `hms-swing/src/hms/model/User.java` and subclasses for inheritance and polymorphism.
- Show `hms-swing/src/hms/model/Patient.java` for encapsulation.
- Show `hms-swing/src/hms/controller/SystemController.java` for abstraction between GUI and services.
- Show `hms-swing/src/hms/service/AppointmentService.java` for duplicate appointment business logic.
