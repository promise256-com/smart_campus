package com.smartcampus.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Mark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "enrollment_id", unique = true)
    private Enrollment enrollment;

    private Double coursework = 0.0;
    private Double exam = 0.0;
    private Double total = 0.0;

    @Column(length = 2)
    private String grade;

    private Double gradePoint = 0.0;
}