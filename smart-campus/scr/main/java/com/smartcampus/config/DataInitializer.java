package com.smartcampus.config;

import com.smartcampus.entity.*;
import com.smartcampus.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final LecturerRepository lecturerRepository;
    private final DepartmentRepository departmentRepository;
    private final ProgramRepository programRepository;
    private final CourseRepository courseRepository;
    private final PasswordEncoder encoder;

    public DataInitializer(UserRepository userRepository,
            StudentRepository studentRepository,
            LecturerRepository lecturerRepository,
            DepartmentRepository departmentRepository,
            ProgramRepository programRepository,
            CourseRepository courseRepository,
            PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.lecturerRepository = lecturerRepository;
        this.departmentRepository = departmentRepository;
        this.programRepository = programRepository;
        this.courseRepository = courseRepository;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0)
            return; // seed only once

        // --- Department & Program ---
        Department dept = new Department();
        dept.setCode("SCIT");
        dept.setName("School of Computing and Informatics");
        departmentRepository.save(dept);

        Program program = new Program();
        program.setCode("BSCS");
        program.setName("BSc Computer Science");
        program.setDurationYears(3);
        program.setDepartment(dept);
        programRepository.save(program);

        // --- Admin ---
        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setPassword(encoder.encode("admin123"));
        adminUser.setRole(Role.ADMIN);
        userRepository.save(adminUser);

        // --- Lecturer ---
        User lecturerUser = new User();
        lecturerUser.setUsername("lecturer");
        lecturerUser.setPassword(encoder.encode("lect123"));
        lecturerUser.setRole(Role.LECTURER);
        userRepository.save(lecturerUser);

        Lecturer lecturer = new Lecturer();
        lecturer.setStaffNo("STF001");
        lecturer.setFirstName("John");
        lecturer.setLastName("Okello");
        lecturer.setEmail("j.okello@campus.ac.ug");
        lecturer.setUser(lecturerUser);
        lecturer.setDepartment(dept);
        lecturerRepository.save(lecturer);

        // --- Student ---
        User studentUser = new User();
        studentUser.setUsername("student");
        studentUser.setPassword(encoder.encode("stud123"));
        studentUser.setRole(Role.STUDENT);
        userRepository.save(studentUser);

        Student student = new Student();
        student.setStudentNo("2025/CS/001");
        student.setFirstName("Mary");
        student.setLastName("Nabirye");
        student.setEmail("m.nabirye@student.campus.ac.ug");
        student.setPhone("+256700000000");
        student.setYearOfStudy(2);
        student.setUser(studentUser);
        student.setProgram(program);
        studentRepository.save(student);

        // --- Courses ---
        courseRepository.save(course("CS101", "Introduction to Programming", 4, 1, program, lecturer));
        courseRepository.save(course("CS102", "Object Oriented Programming", 4, 1, program, lecturer));
        courseRepository.save(course("CS201", "Database Systems", 3, 2, program, lecturer));
        courseRepository.save(course("CS202", "Web Application Development", 3, 2, program, lecturer));
    }

    private Course course(String code, String title, int cu, int sem, Program program, Lecturer lecturer) {
        Course c = new Course();
        c.setCode(code);
        c.setTitle(title);
        c.setCreditUnits(cu);
        c.setSemester(sem);
        c.setAcademicYear("2025/2026");
        c.setProgram(program);
        c.setLecturer(lecturer);
        return c;
    }
}