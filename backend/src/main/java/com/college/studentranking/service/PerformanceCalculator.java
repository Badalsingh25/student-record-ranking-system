package com.college.studentranking.service;

import com.college.studentranking.model.AcademicRecord;
import com.college.studentranking.model.Student;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/** Single source of truth for percentage averages and grade boundaries. */
public final class PerformanceCalculator {
    private static final BigDecimal[] CUTOFFS = {
        BigDecimal.valueOf(90), BigDecimal.valueOf(80), BigDecimal.valueOf(70), BigDecimal.valueOf(60), BigDecimal.valueOf(50)
    };
    private static final String[] GRADES = {"A+", "A", "B", "C", "D"};
    public static final List<String> GRADE_ORDER = List.of("A+", "A", "B", "C", "D", "F");

    private PerformanceCalculator() {}

    public static String grade(BigDecimal percentage) {
        for (int i = 0; i < CUTOFFS.length; i++) {
            if (percentage.compareTo(CUTOFFS[i]) >= 0) return GRADES[i];
        }
        return "F";
    }

    /** Average of all record percentages, rounded to 2 decimals; zero when the student has no records. */
    public static BigDecimal average(Student student) {
        List<AcademicRecord> records = student.getRecords();
        if (records.isEmpty()) return BigDecimal.ZERO.setScale(2);
        BigDecimal sum = BigDecimal.ZERO;
        for (AcademicRecord r : records) sum = sum.add(r.getPercentage());
        return sum.divide(BigDecimal.valueOf(records.size()), 2, RoundingMode.HALF_UP);
    }
}
