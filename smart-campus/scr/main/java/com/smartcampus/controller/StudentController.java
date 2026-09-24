package com.smartcampus.controller;

import com.smartcampus.entity.*;
import com.smartcampus.repository.*;
import com.smartcampus.service.GradeService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AttendanceRepository attendanceRepository;
    private final MarkRepository markRepository;
    private final GradeService gradeService;

    public StudentController(StudentRepository studentRepository,
            CourseRepository courseRepository,
            EnrollmentRepository enrollmentRepository,
            AttendanceRepository attendanceRepository,
            MarkRepository markRepository,
            GradeService gradeService) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.attendanceRepository = attendanceRepository;
        this.markRepository = markRepository;
        this.gradeService = gradeService;
    }

    private Student currentStudent(Authentication auth) {
        return studentRepository.findByUserUsername(auth.getName()).orElseThrow();
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        Student student = currentStudent(auth);
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(student.getId());
        List<Mark> marks = markRepository.findByEnrollmentStudentId(student.getId());

        model.addAttribute("student", student);
        model.addAttribute("enrollments", enrollments);
        model.addAttribute("gpa", gradeService.calculateGpa(marks));
        return "student/dashboard";
    }

    // ---------------- Course registration ----------------

    @GetMapping("/courses")
    public String courses(Authentication auth, Model model) {
        Student student = currentStudent(auth);
        model.addAttribute("student", student);
        model.addAttribute("courses", courseRepository.findAll());
        model.addAttribute("enrollments", enrollmentRepository.findByStudentId(student.getId()));
        return "student/courses";
    }

    @PostMapping("/courses/register")
    public String register(@RequestParam Long courseId, Authentication auth) {
        Student student = currentStudent(auth);

        if (!enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), courseId)) {
            Enrollment enrollment = new Enrollment();
            enrollment.setStudent(student);
            enrollment.setCourse(courseRepository.findById(courseId).orElseThrow());
            enrollmentRepository.save(enrollment);
        }
        return "redirect:/student/courses?registered";
    }

    // ---------------- Attendance ----------------

    @GetMapping("/attendance")
    public String attendance(Authentication auth, Model model) {
        Student student = currentStudent(auth);
        model.addAttribute("student", student);
        model.addAttribute("records", attendanceRepository.findByEnrollmentStudentId(student.getId()));
        return "student/attendance";
    }

    // ---------------- Results & transcript ----------------

    @GetMapping("/results")
    public String results(Authentication auth, Model model) {
        Student student = currentStudent(auth);
        List<Mark> marks = markRepository.findByEnrollmentStudentId(student.getId());

        model.addAttribute("student", student);
        model.addAttribute("marks", marks);
        model.addAttribute("gpa", gradeService.calculateGpa(marks));
        return "student/results";
    }
}