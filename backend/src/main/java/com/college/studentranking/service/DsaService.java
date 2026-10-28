package com.college.studentranking.service;

import com.college.studentranking.dsa.RollNumberIndex;
import com.college.studentranking.dsa.SearchAlgorithms;
import com.college.studentranking.dsa.SearchAlgorithms.Result;
import com.college.studentranking.dsa.SortStats;
import com.college.studentranking.dsa.SortingAlgorithms;
import com.college.studentranking.dsa.SortingAlgorithms.Algorithm;
import com.college.studentranking.dto.DsaResponses.*;
import com.college.studentranking.dto.StudentResponse;
import com.college.studentranking.mapper.StudentMapper;
import com.college.studentranking.model.Student;
import com.college.studentranking.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/** Powers the "Algorithm Lab": sort and search the student array with our own algorithms and report the work done. */
@Service
@Transactional(readOnly = true)
public class DsaService {
    private final StudentRepository repo;
    private final StudentMapper mapper;

    public DsaService(StudentRepository repo, StudentMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    // ------------------------------------------------------------------ sorting
    public SortResponse sort(String algorithm, String field, String order, String className) {
        Algorithm algo = Algorithm.from(algorithm);
        Comparator<Student> cmp = comparator(field, order);
        Student[] data = load(className);
        SortRun run = runSort(data, cmp, algo);
        List<StudentResponse> students = new ArrayList<>(data.length);
        for (Student s : data) students.add(mapper.toResponse(s));
        return new SortResponse(run, normalizeField(field), resolveOrder(field, order), students);
    }

    private SortRun runSort(Student[] data, Comparator<Student> cmp, Algorithm algo) {
        SortStats stats = new SortStats();
        long start = System.nanoTime();
        SortingAlgorithms.sort(data, cmp, algo, stats);
        long micros = (System.nanoTime() - start) / 1_000;
        return new SortRun(algo.name(), algo.label, algo.timeComplexity, algo.spaceComplexity, algo.stable,
            data.length, stats.comparisons(), stats.swaps(), micros);
    }

    // ---------------------------------------------------------------- searching
    public SearchResponse search(String mode, String query) {
        String q = query == null ? "" : query.trim();
        Student[] data = load(null);
        long start = System.nanoTime();
        List<Student> matches = new ArrayList<>();
        long comparisons;
        String label, complexity, m;

        switch (mode == null ? "hash" : mode.toLowerCase()) {
            case "linear" -> {
                m = "linear"; label = "Linear search (roll number)"; complexity = "O(n)";
                Student probe = probe(q);
                Result r = SearchAlgorithms.linear(data, probe, ROLL_ORDER);
                comparisons = r.comparisons();
                if (r.found()) matches.add(data[r.index()]);
            }
            case "binary" -> {
                m = "binary"; label = "Binary search (roll number, sorted array)"; complexity = "O(log n)";
                SortStats ignored = new SortStats();
                SortingAlgorithms.mergeSort(data, ROLL_ORDER, ignored);     // binary search needs sorted input
                Result r = SearchAlgorithms.binary(data, probe(q), ROLL_ORDER);
                comparisons = r.comparisons();
                if (r.found()) matches.add(data[r.index()]);
            }
            case "prefix" -> {
                m = "prefix"; label = "Name prefix search (binary lower bound)"; complexity = "O(log n + k)";
                Comparator<Student> byName = Comparator.comparing(Student::getFullName, String.CASE_INSENSITIVE_ORDER);
                SortingAlgorithms.mergeSort(data, byName, new SortStats());
                int i = SearchAlgorithms.lowerBound(data, q, Student::getFullName);
                comparisons = (long) Math.ceil(Math.log(Math.max(2, data.length)) / Math.log(2));
                while (!q.isEmpty() && i < data.length && data[i].getFullName().toLowerCase().startsWith(q.toLowerCase())) {
                    matches.add(data[i++]);
                    comparisons++;
                }
            }
            default -> {
                m = "hash"; label = "Hash map lookup (roll number)"; complexity = "O(1) average";
                RollNumberIndex<Student> index = RollNumberIndex.build(Arrays.asList(data), Student::getRollNumber);
                comparisons = 1;
                index.find(q).ifPresent(matches::add);
            }
        }
        long micros = (System.nanoTime() - start) / 1_000;
        return new SearchResponse(m, label, complexity, q, data.length, comparisons, micros,
            matches.stream().map(mapper::toResponse).toList());
    }

    // ------------------------------------------------------------------ helpers
    private static final Comparator<Student> ROLL_ORDER = Comparator.comparing(Student::getRollNumber, String.CASE_INSENSITIVE_ORDER);

    private static Student probe(String rollNumber) {
        Student s = new Student();
        s.setRollNumber(rollNumber);
        return s;
    }

    private Student[] load(String className) {
        List<Student> all = repo.findAllWithRecordsBy();
        if (className != null && !className.isBlank()) {
            all = all.stream().filter(s -> s.getClassName().equalsIgnoreCase(className.trim())).toList();
        }
        return all.toArray(new Student[0]);
    }

    private static String normalizeField(String field) {
        return switch (field == null ? "" : field.toLowerCase()) {
            case "name" -> "name";
            case "roll" -> "roll";
            default -> "marks";
        };
    }

    private static Comparator<Student> comparator(String field, String order) {
        Comparator<Student> base = switch (normalizeField(field)) {
            case "name" -> Comparator.comparing(Student::getFullName, String.CASE_INSENSITIVE_ORDER);
            case "roll" -> ROLL_ORDER;
            default -> Comparator.comparing(PerformanceCalculator::average);
        };
        return resolveOrder(field, order).equals("desc") ? base.reversed() : base;
    }

    /** Marks default to "best first" (desc); name and roll number default to ascending. */
    private static String resolveOrder(String field, String order) {
        if (order == null || order.isBlank()) return normalizeField(field).equals("marks") ? "desc" : "asc";
        return "desc".equalsIgnoreCase(order) ? "desc" : "asc";
    }
}
