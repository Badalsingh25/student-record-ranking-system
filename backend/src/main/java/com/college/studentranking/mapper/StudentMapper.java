package com.college.studentranking.mapper;

import com.college.studentranking.dto.StudentResponse;
import com.college.studentranking.model.Student;
import com.college.studentranking.service.PerformanceCalculator;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class StudentMapper {
    public StudentResponse toResponse(Student s) {
        return toResponse(s, null);
    }
    
    public StudentResponse toResponse(Student s, Integer rank) {
        List<StudentResponse.RecordResponse> records = s.getRecords().stream()
                .map(r -> new StudentResponse.RecordResponse(r.getId(), r.getTerm(), r.getSubject(),
                        r.getMarksObtained(), r.getMaxMarks(), r.getPercentage(), r.getGrade()))
                .toList();
        BigDecimal avg = PerformanceCalculator.average(s);
        return new StudentResponse(s.getId(), s.getRollNumber(), s.getFullName(), s.getEmail(), s.getPhone(),
                s.getClassName(), s.getDepartment(), s.getYearOfStudy(), s.getStatus(), avg,
                s.getRecords().isEmpty() ? "N/A" : PerformanceCalculator.grade(avg), rank, s.getCreatedAt(), records);
    }
}
