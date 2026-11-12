# Fourth-Year Project Management System

A web-based project management platform designed to support the fourth-year engineering project process at Carleton University.

The system will provide a centralized platform for students, professors, and project coordinators to manage project topics, student applications, oral presentations, and final report submissions.

## Project Overview

The Fourth-Year Project Management System aims to simplify and organize the administration of fourth-year engineering projects.

The application will support three main user roles:

- **Students:** Browse available project topics, apply to projects, provide presentation availability, and submit final reports.
- **Professors:** Create, manage, archive, and delete project topics.
- **Project Coordinators:** Monitor student project selections, send reminders, coordinate oral presentations, and manage submission deadlines.

## Planned Features

- Project topic creation and management.
- Project descriptions, program restrictions, and student capacity.
- Student project browsing and application.
- Application capacity enforcement.
- Identification of students without assigned projects.
- Coordinator reminders for students without projects.
- Oral presentation availability management.
- Presentation room allocation and scheduling.
- Online final report submissions.
- Coordinator-defined submission deadlines and deadline enforcement.

## Technology Stack

The project is being developed using:

| Technology | Purpose |
|---|---|
| Java | Application development |
| Spring Boot | Application framework |
| Spring MVC | Web application architecture |
| Spring Data JPA | Database interaction |
| Maven | Dependency management and build automation |
| Thymeleaf | Server-side HTML rendering |
| H2 | Temporary development database |
| PostgreSQL | Planned production database |
| GitHub | Version control and collaboration |
| GitHub Actions | Planned continuous integration |
| Microsoft Azure | Planned application hosting |

*The technology stack and deployment configuration are subject to change during development.*

## Getting Started

### Prerequisites

- Java JDK compatible with the project's Spring Boot version.
- Git.
- Maven (optional, as the repository includes the Maven Wrapper).

### Clone the Repository

```bash
git clone https://github.com/GabrielleFarahexe/SYSC-4806-4th-Year-Project-Management-System.git
cd SYSC-4806-4th-Year-Project-Management-System
```

### Build the Application

Windows:

```powershell
.\mvnw.cmd clean verify
```

macOS/Linux:

```bash
./mvnw clean verify
```

### Run the Application

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

macOS/Linux:

```bash
./mvnw spring-boot:run
```

Once running, access the application at:

http://localhost:8080

### Development Database

Run the application from the repository root. H2 stores development data in
`data/projectdb.mv.db`, so saved availability survives application restarts.
The `data/` directory is excluded from Git. Tests use a separate in-memory database.

To inspect saved rows, open `http://localhost:8080/h2-console` after signing in,
or configure an IntelliJ H2 data source with the repository root as its working directory:

- JDBC URL: `jdbc:h2:file:./data/projectdb;AUTO_SERVER=TRUE`
- User: `sa`
- Password: leave blank

Run `SELECT * FROM AVAILABILITY;` to inspect the selections. For an external
connection without the same working directory, replace `./data/projectdb` with
the absolute path to the repository's `data/projectdb`.
The previous `jdbc:h2:mem:projectdb` URL created a separate database per process;
its data was temporary and cannot be recovered after the application stops.

## Development Process

Development will be organized into three two-week sprints.

Each sprint will focus on implementing and testing a portion of the system's functionality.

## Repository Structure

```text
SYSC-4806-4th-Year-Project-Management-System/
│
├── .mvn/
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   └── test/
│
├── pom.xml
├── mvnw
├── mvnw.cmd
├── README.md
└── .gitignore
```

## Course Project

**Course:** SYSC 4806 – Software Engineering Project

**Institution:** Carleton University

**Project:** Fourth-Year Project Management System
