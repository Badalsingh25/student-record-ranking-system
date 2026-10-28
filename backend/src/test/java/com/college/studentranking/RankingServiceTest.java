package com.college.studentranking;

import com.college.studentranking.dto.StudentResponse;
import com.college.studentranking.mapper.StudentMapper;
import com.college.studentranking.model.*;
import com.college.studentranking.repository.StudentRepository;
import com.college.studentranking.service.RankingService;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RankingServiceTest {
    private static Student student(String name, String roll, StudentStatus status, int... marks) {
        Student s = new Student();
        s.setFullName(name); s.setRollNumber(roll); s.setClassName("CSE-A"); s.setStatus(status);
        for (int m : marks) {
            AcademicRecord r = new AcademicRecord();
            r.setMarksObtained(BigDecimal.valueOf(m)); r.setMaxMarks(BigDecimal.valueOf(100));
            s.addRecord(r);
        }
        return s;
    }

    private RankingService serviceWith(Student... students) {
        StudentRepository repo = mock(StudentRepository.class);
        when(repo.findAllWithRecordsBy()).thenReturn(List.of(students));
        return new RankingService(repo, new StudentMapper());
    }

    @Test void tiedStudentsShareARankAndNextRankIsSkipped() {
        RankingService service = serviceWith(
            student("Bob", "R2", StudentStatus.ACTIVE, 90),
            student("Amy", "R1", StudentStatus.ACTIVE, 90),
            student("Cara", "R3", StudentStatus.ACTIVE, 70));
        List<StudentResponse> board = service.leaderboard(null);
        assertEquals(List.of("Amy", "Bob", "Cara"), board.stream().map(StudentResponse::fullName).toList());
        assertEquals(List.of(1, 1, 3), board.stream().map(StudentResponse::rank).toList());
    }

    @Test void inactiveAndUnmarkedStudentsAreNotRanked() {
        RankingService service = serviceWith(
            student("Active", "R1", StudentStatus.ACTIVE, 80),
            student("Inactive", "R2", StudentStatus.INACTIVE, 99),
            student("NoMarks", "R3", StudentStatus.ACTIVE));
        assertEquals(1, service.leaderboard(null).size());
    }

    @Test void topKMatchesTheFullLeaderboardPrefix() {
        Student[] all = new Student[25];
        for (int i = 0; i < all.length; i++) all[i] = student("S" + String.format("%02d", i), "R" + i, StudentStatus.ACTIVE, 40 + (i * 7) % 60);
        RankingService service = serviceWith(all);
        List<StudentResponse> top = service.top(10, null);
        List<StudentResponse> full = service.leaderboard(null).subList(0, 10);
        assertEquals(full.stream().map(StudentResponse::rollNumber).toList(), top.stream().map(StudentResponse::rollNumber).toList());
        assertEquals(full.stream().map(StudentResponse::rank).toList(), top.stream().map(StudentResponse::rank).toList());
    }
}
