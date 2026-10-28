package com.college.studentranking.dto;

import com.college.studentranking.model.StudentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record StudentResponse(
        Long id,
        String rollNumber,
        String fullName,
        String email,
        String phone,
        String className,
        String department,
        Integer yearOfStudy,
        StudentStatus status,
        BigDecimal averagePercentage,
        String overallGrade,
        Integer rank,
        LocalDateTime createdAt,
        List<RecordResponse> records
) {
    public record RecordResponse(Long id, String term, String subject, BigDecimal marksObtained,
                                 BigDecimal maxMarks, BigDecimal percentage, String grade) {
    }
}
