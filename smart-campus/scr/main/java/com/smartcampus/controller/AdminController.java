package com.smartcampus.controller;

import com.smartcampus.entity.*;
import com.smartcampus.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final StudentRepository studentRepository;
    private final LecturerRepository lecturerRepository;
    private final ProgramRepository programRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public AdminController(StudentRepository studentRepository,
            LecturerRepository lecturerRepository,
            ProgramRepository programRepository,
            CourseRepository courseRepository,
            UserRepository userRepository,
            PasswordEncoder encoder) {
        this.studentRepository = studentRepository;
        this.lecturerRepository = lecturerRepository;
        this.programRepository = programRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("studentCount", studentRepository.count());
        model.addAttribute("lecturerCount", lecturerRepository.count());
        model.addAttribute("programCount", programRepository.count());
        model.addAttribute("courseCount", courseRepository.count());
        return "admin/dashboard";
    }

    // ---------------- Students ----------------

    @GetMapping("/students")
    public String students(Model model) {
        model.addAttribute("students", studentRepository.findAll());
        model.addAttribute("programs", programRepository.findAll());
        return "admin/students";
    }

    @PostMapping("/students/add")
    public String addStudent(@RequestParam String studentNo,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(defaultValue = "1") int yearOfStudy,
            @RequestParam Long programId,
            @RequestParam String username,
            @RequestParam String password) {

        if (userRepository.findByUsername(username).isPresent()) {
            return "redirect:/admin/students?userExists";
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(encoder.encode(password));
        user.setRole(Role.STUDENT);
        userRepository.save(user);

        Student student = new Student();
        student.setStudentNo(studentNo);
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setEmail(email);
        student.setPhone(phone);
        student.setYearOfStudy(yearOfStudy);
        student.setUser(user);
        student.setProgram(programRepository.findById(programId).orElse(null));
        studentRepository.save(student);

        return "redirect:/admin/students?added";
    }

    // ---------------- Courses ----------------

    @GetMapping("/courses")
    public String courses(Model model) {
        model.addAttribute("courses", courseRepository.findAll());
        model.addAttribute("programs", programRepository.findAll());
        model.addAttribute("lecturers", lecturerRepository.findAll());
        return "admin/courses";
    }

    @PostMapping("/courses/add")
    public String addCourse(@RequestParam String code,
            @RequestParam String title,
            @RequestParam(defaultValue = "3") int creditUnits,
            @RequestParam(defaultValue = "1") int semester,
            @RequestParam(defaultValue = "2025/2026") String academicYear,
            @RequestParam Long programId,
            @RequestParam(required = false) Long lecturerId) {

        Course course = new Course();
        course.setCode(code);
        course.setTitle(title);
        course.setCreditUnits(creditUnits);
        course.setSemester(semester);
        course.setAcademicYear(academicYear);
        course.setProgram(programRepository.findById(programId).orElse(null));
        if (lecturerId != null) {
            course.setLecturer(lecturerRepository.findById(lecturerId).orElse(null));
        }
        courseRepository.save(course);

        return "redirect:/admin/courses?added";
    }
}