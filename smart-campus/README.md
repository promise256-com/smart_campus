# Smart Campus – Student Management and Academic Information System

Java Spring Boot web application for managing students, lecturers, courses,
attendance, marks, grades and transcripts with role-based access.

## Tech Stack
- Java 17, Spring Boot 3.2
- Spring MVC + Thymeleaf + Bootstrap 5
- Spring Data JPA (Hibernate)
- Spring Security (role-based: ADMIN / LECTURER / STUDENT)
- MySQL (SQLite supported via commented config in `application.properties`)

## Roles
| Role | Capabilities |
|------|--------------|
| Administrator | Manage students, courses, lecturers, programs, departments |
| Lecturer | View assigned courses, record attendance, enter coursework & exam marks |
| Student | View profile, register courses, view attendance, results and GPA |

## Running the Project

1. Create the database (auto-created if `createDatabaseIfNotExist=true`):
   ```sql
   CREATE DATABASE smart_campus;

2. Update src/main/resources/application.properties with your MySQL credentials.

3.Build and run:

bash
mvn spring-boot:run
4.Open http://localhost:8080

Demo Accounts
Username	Password	Role
admin	admin123	Administrator
lecturer	lect123	Lecturer
student	stud123	Student
Grading Scale
Total	Grade	Points
80–100	A	    5.0
70–79	B	4.0
60–69	C	3.0
50–59	D	2.0
40–49	E	1.0
0–39	F	0.0
GPA = Σ(Grade Point × Credit Units) ÷ Σ(Credit Units)

text
