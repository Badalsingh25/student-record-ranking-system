package com.college.studentranking.service;

import com.college.studentranking.dsa.MinHeap;
import com.college.studentranking.dsa.SortStats;
import com.college.studentranking.dsa.SortingAlgorithms;
import com.college.studentranking.dto.StudentResponse;
import com.college.studentranking.mapper.StudentMapper;
import com.college.studentranking.model.Student;
import com.college.studentranking.model.StudentStatus;
import com.college.studentranking.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Ranking logic built on our own DSA code:
 *  - students are held in an array and sorted with merge sort (stable),
 *  - ranks are assigned in one linear pass (competition ranking: 1, 1, 3),
 *  - the top-K list is selected with a min-heap in O(n log k).
 */
@Service
@Transactional(readOnly = true)
public class RankingService {
    /** Best first: higher average, then name A-Z, then roll number (fully deterministic order). */
    static final Comparator<Student> BY_RANK = (a, b) -> {
        int c = PerformanceCalculator.average(b).compareTo(PerformanceCalculator.average(a));
        if (c != 0) return c;
        c = a.getFullName().compareToIgnoreCase(b.getFullName());
        return c != 0 ? c : a.getRollNumber().compareToIgnoreCase(b.getRollNumber());
    };

    private final StudentRepository repo;
    private final StudentMapper mapper;

    public RankingService(StudentRepository repo, StudentMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    /** Full leaderboard. Inactive students and students without any marks are not ranked. */
    public List<StudentResponse> leaderboard(String className) {
        Student[] arr = rankable(className);
        SortingAlgorithms.mergeSort(arr, BY_RANK, new SortStats());
        return assignRanks(arr);
    }

    /** Top-K performers selected with a bounded min-heap (no full sort). */
    public List<StudentResponse> top(int limit, String className) {
        int k = Math.max(1, Math.min(limit, 100));
        // "better" students compare greater, so the heap root is always the weakest of the current top K
        MinHeap<Student> heap = new MinHeap<>(k, BY_RANK.reversed());
        for (Student s : rankable(className)) heap.offer(s);
        Student[] best = new Student[heap.size()];
        for (int i = best.length - 1; i >= 0; i--) best[i] = heap.poll();   // polled worst -> best, fill from the end
        return assignRanks(best);
    }

    /** Dense array of ranked students, in repository order. */
    Student[] rankable(String className) {
        List<Student> list = new ArrayList<>();
        for (Student s : repo.findAllWithRecordsBy()) {
            if (s.getStatus() == StudentStatus.INACTIVE || s.getRecords().isEmpty()) continue;
            if (className != null && !className.isBlank() && !s.getClassName().equalsIgnoreCase(className.trim())) continue;
            list.add(s);
        }
        return list.toArray(new Student[0]);
    }

    /** Competition ranking over an already sorted array: equal averages share a rank. */
    List<StudentResponse> assignRanks(Student[] sorted) {
        List<StudentResponse> out = new ArrayList<>(sorted.length);
        BigDecimal previous = null;
        int rank = 0;
        for (int i = 0; i < sorted.length; i++) {
            BigDecimal avg = PerformanceCalculator.average(sorted[i]);
            if (previous == null || avg.compareTo(previous) != 0) rank = i + 1;
            out.add(mapper.toResponse(sorted[i], rank));
            previous = avg;
        }
        return out;
    }
}
