# Hospital Management System MVP Design

## Source of Requirements

The implementation follows `Lab Exercise 2026.pdf`: Java Swing only, role-based login, appointment booking, patient records, doctor management, basic reports, UML diagrams, and a 10-15 minute presentation video in rubric order.

The GitHub repositories shared by the team are treated only as high-level references for common HMS screens. No source code is copied.

## MVP Scope

The app is a Java Swing desktop program with in-memory data. This keeps the demo easy to run and explain without database setup.

Supported workflows:
- Login as Admin, Doctor, or Receptionist.
- Add and update patients with name, age, gender, and medical history.
- Add and view doctors with specialization.
- Book appointments by patient, doctor, date, and time.
- Reject duplicate doctor date/time slots.
- Show report totals and doctor schedules.

## OOAD Structure

Packages:
- `hms.model`: domain classes such as `Patient`, `Doctor`, `Appointment`, and `User`.
- `hms.service`: business rules for authentication, appointments, patients, doctors, and reporting.
- `hms.controller`: `SystemController` facade used by the GUI.
- `hms.view`: Swing frames and panels.
- `hms.app`: application entry point.
- `hms.tests`: simple command-line tests without external libraries.

OOP concepts:
- Encapsulation: fields are private and accessed through methods.
- Inheritance: `AdminUser`, `DoctorUser`, and `ReceptionistUser` extend `User`.
- Polymorphism: login returns a `User` reference whose concrete subtype determines role behavior.
- Abstraction: GUI depends on `SystemController`, not on low-level storage lists.

## Demo Credentials

- Admin: `admin` / `admin123`
- Doctor: `doctor` / `doctor123`
- Receptionist: `reception` / `reception123`

## Presentation Fit

The code and diagrams are intentionally compact so every team member can explain the design in the required video order: introduction, class diagram, sequence diagrams, system demo, and code explanation.
