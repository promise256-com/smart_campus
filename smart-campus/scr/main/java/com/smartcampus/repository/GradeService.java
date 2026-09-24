package com.smartcampus.service;

import com.smartcampus.entity.Mark;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GradeService {

    /**
     * Computes total, letter grade and grade point from coursework + exam marks.
     */
    public void applyGrade(Mark mark) {
        double coursework = mark.getCoursework() == null ? 0.0 : mark.getCoursework();
        double exam = mark.getExam() == null ? 0.0 : mark.getExam();
        double total = coursework + exam;

        String grade;
        double point;

        if (total >= 80) {
            grade = "A";
            point = 5.0;
        } else if (total >= 70) {
            grade = "B";
            point = 4.0;
        } else if (total >= 60) {
            grade = "C";
            point = 3.0;
        } else if (total >= 50) {
            grade = "D";
            point = 2.0;
        } else if (total >= 40) {
            grade = "E";
            point = 1.0;
        } else {
            grade = "F";
            point = 0.0;
        }

        mark.setTotal(total);
        mark.setGrade(grade);
        mark.setGradePoint(point);
    }

    /** Credit-weighted Grade Point Average. */
    public double calculateGpa(List<Mark> marks) {
        double weighted = 0.0;
        int credits = 0;

        for (Mark m : marks) {
            if (m.getEnrollment() == null || m.getEnrollment().getCourse() == null)
                continue;
            int cu = m.getEnrollment().getCourse().getCreditUnits();
            weighted += m.getGradePoint() * cu;
            credits += cu;
        }
        return credits == 0 ? 0.0 : Math.round((weighted / credits) * 100.0) / 100.0;
    }
}