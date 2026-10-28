package com.college.studentranking;

import com.college.studentranking.model.AcademicRecord;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class StudentRankingApplicationTests {
    @Test void percentageAndGradeAreCalculatedCorrectly() {
        AcademicRecord record=new AcademicRecord();
        record.setMarksObtained(new BigDecimal("87"));
        record.setMaxMarks(new BigDecimal("100"));
        assertEquals(new BigDecimal("87.00"),record.getPercentage());
        assertEquals("A",record.getGrade());
    }
    @Test void percentageHandlesDifferentMaximumMarks() {
        AcademicRecord record=new AcademicRecord();
        record.setMarksObtained(new BigDecimal("45"));
        record.setMaxMarks(new BigDecimal("50"));
        assertEquals(new BigDecimal("90.00"),record.getPercentage());
        assertEquals("A+",record.getGrade());
    }
}
