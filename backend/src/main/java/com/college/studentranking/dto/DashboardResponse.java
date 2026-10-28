package com.college.studentranking.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record DashboardResponse(
        long totalStudents,
        long activeStudents,
        long classCount,
        BigDecimal averagePercentage,
        long academicRecords,
        List<StudentResponse> topStudents,
        Map<String, Long> gradeDistribution,
        List<SubjectAverage> subjectAverages
) {
    public record SubjectAverage(String subject, BigDecimal averagePercentage) {
    }
}
