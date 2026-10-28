package com.college.studentranking.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Setter
@Entity
@Table(name = "academic_records", indexes = @Index(name = "idx_record_term_subject", columnList = "term, subject"))
public class AcademicRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    
    @Column(nullable = false, length = 40)
    private String term;
    
    @Column(nullable = false, length = 100)
    private String subject;
    
    @Column(name = "marks_obtained", nullable = false, precision = 7, scale = 2)
    private BigDecimal marksObtained;
    
    @Column(name = "max_marks", nullable = false, precision = 7, scale = 2)
    private BigDecimal maxMarks;
    
    public BigDecimal getPercentage() {
        if (maxMarks == null || maxMarks.signum() == 0) {
            return BigDecimal.ZERO;
        }
        return marksObtained.multiply(BigDecimal.valueOf(100)).divide(maxMarks, 2, RoundingMode.HALF_UP);
    }
    
    public String getGrade() {
        return com.college.studentranking.service.PerformanceCalculator.grade(getPercentage());
    }
}
