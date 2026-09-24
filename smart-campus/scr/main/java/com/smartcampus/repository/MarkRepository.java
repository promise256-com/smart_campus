package com.smartcampus.repository;

import com.smartcampus.entity.Mark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MarkRepository extends JpaRepository<Mark, Long> {
    Optional<Mark> findByEnrollmentId(Long enrollmentId);

    List<Mark> findByEnrollmentStudentId(Long studentId);
}