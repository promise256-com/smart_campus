package com.smartcampus.controller;

import com.smartcampus.entity.*;
import com.smartcampus.repository.*;
import com.smartcampus.service.GradeService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/lecturer")
public class LecturerController {

    private final LecturerRepository lecturerRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AttendanceRepository attendanceRepository;
    private final MarkRepository markRepository;
    private final GradeService gradeService;

    public LecturerController(LecturerRepository lecturerRepository,
            CourseRepository courseRepository,
            EnrollmentRepository enrollmentRepository,
            AttendanceRepository attendanceRepository,
            MarkRepository markRepository,
            GradeService gradeService) {
        this.lecturerRepository = lecturerRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.attendanceRepository = attendanceRepository;
        this.markRepository = markRepository;
        this.gradeService = gradeService;
    }

    private Lecturer currentLecturer(Authentication auth) {
        return lecturerRepository.findByUserUsername(auth.getName()).orElseThrow();
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        Lecturer lecturer = currentLecturer(auth);
        List<Course> courses = courseRepository.findByLecturerId(lecturer.getId());

        Map<Long, Long> counts = new HashMap<>();
        for (Course c : courses) {
            counts.put(c.getId(), enrollmentRepository.countByCourseId(c.getId()));
        }

        model.addAttribute("lecturer", lecturer);
        model.addAttribute("courses", courses);
        model.addAttribute("counts", counts);
        return "lecturer/dashboard";
    }

    // ---------------- Attendance ----------------

    @GetMapping("/attendance/{courseId}")
    public String attendance(@PathVariable Long courseId, Model model) {
        Course course = courseRepository.findById(courseId).orElseThrow();
        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(courseId);

        Map<Long, List<Attendance>> history = new HashMap<>();
        for (Enrollment e : enrollments) {
            history.put(e.getId(), attendanceRepository.findByEnrollmentId(e.getId()));
        }

        model.addAttribute("course", course);
        model.addAttribute("enrollments", enrollments);
        model.addAttribute("history", history);
        model.addAttribute("today", LocalDate.now());
        return "lecturer/attendance";
    }

    @PostMapping("/attendance/{courseId}")
    public String saveAttendance(@PathVariable Long courseId,
            @RequestParam String date,
            @RequestParam Map<String, String> params) {

        LocalDate attendanceDate = LocalDate.parse(date);
        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(courseId);

        for (Enrollment e : enrollments) {
            String value = params.get("status_" + e.getId());
            if (value == null || value.isBlank())
                continue;

            Attendance a = new Attendance();
            a.setEnrollment(e);
            a.setDate(attendanceDate);
            a.setStatus(Attendance.Status.valueOf(value));
            attendanceRepository.save(a);
        }
        return "redirect:/lecturer/attendance/" + courseId + "?saved";
    }

    // ---------------- Marks ----------------

    @GetMapping("/marks/{courseId}")
    public String marks(@PathVariable Long courseId, Model model) {
        Course course = courseRepository.findById(courseId).orElseThrow();
        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(courseId);

        Map<Long, Mark> marks = new HashMap<>();
        for (Enrollment e : enrollments) {
            markRepository.findByEnrollmentId(e.getId()).ifPresent(m -> marks.put(e.getId(), m));
        }

        model.addAttribute("course", course);
        model.addAttribute("enrollments", enrollments);
        model.addAttribute("marks", marks);
        return "lecturer/marks";
    }

    @PostMapping("/marks/{courseId}")
    public String saveMarks(@PathVariable Long courseId,
            @RequestParam Map<String, String> params) {

        for (Enrollment e : enrollmentRepository.findByCourseId(courseId)) {
            String cw = params.get("coursework_" + e.getId());
            String ex = params.get("exam_" + e.getId());
            if ((cw == null || cw.isBlank()) && (ex == null || ex.isBlank()))
                continue;

            Mark mark = markRepository.findByEnrollmentId(e.getId()).orElseGet(() -> {
                Mark m = new Mark();
                m.setEnrollment(e);
                return m;
            });

            mark.setCoursework(parse(cw));
            mark.setExam(parse(ex));
            gradeService.applyGrade(mark);
            markRepository.save(mark);
        }
        return "redirect:/lecturer/marks/" + courseId + "?saved";
    }

    private double parse(String value) {
        if (value == null || value.isBlank())
            return 0.0;
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            return 0.0;
        }
    }
}