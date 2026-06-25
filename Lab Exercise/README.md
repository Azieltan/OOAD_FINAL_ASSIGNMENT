# Hospital Management System Lab Exercise

This folder contains a Java Swing MVP for `Lab Exercise 2026.pdf`.

## Requirements Covered

- Java Swing only.
- Login with Admin, Doctor, and Receptionist roles.
- Appointment booking with patient, doctor, date, and time.
- Duplicate doctor time-slot prevention.
- Patient record add/update/view.
- Doctor add/view with specialization.
- Basic reporting: total patients, total appointments, and doctor schedules.
- OO concepts: encapsulation, inheritance, polymorphism, abstraction.
- UML diagrams: class diagram and sequence diagrams for login, appointment booking, add patient, and generate report.

## Demo Login

| Role | Username | Password |
| --- | --- | --- |
| Admin | `admin` | `admin123` |
| Doctor | `doctor` | `doctor123` |
| Receptionist | `reception` | `reception123` |

## Compile and Run

Run these commands from `OOAD/Lab Exercise/`:

```powershell
Remove-Item -Recurse -Force hms-swing/out -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force hms-swing/out
javac -d hms-swing/out (Get-ChildItem -Recurse -Filter *.java hms-swing/src,hms-swing/test | ForEach-Object { $_.FullName })
java -cp hms-swing/out hms.tests.HospitalSystemTest
java -cp hms-swing/out hms.app.HospitalManagementApp
```

## Suggested ZIP Contents

- `README.md`
- `Lab Exercise 2026.pdf`
- `team-workload.md`
- `docs/`
- `uml/`
- `hms-swing/src/`
- `hms-swing/test/`
- Presentation video file, named according to tutor instruction.

Do not include `hms-swing/out/` in the final ZIP because it is generated compile output.
