package com.college.studentranking.service;

import com.college.studentranking.dto.*;
import com.college.studentranking.mapper.StudentMapper;
import com.college.studentranking.model.*;
import com.college.studentranking.repository.StudentRepository;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@Transactional
public class StudentService {
    private static final Set<String> SORTABLE = Set.of("rollNumber", "fullName", "className", "department", "yearOfStudy", "status", "email");

    private final StudentRepository repo;
    private final StudentMapper mapper;
    private final RankingService rankingService;

    public StudentService(StudentRepository repo, StudentMapper mapper, RankingService rankingService) {
        this.repo = repo;
        this.mapper = mapper;
        this.rankingService = rankingService;
    }

    // ------------------------------------------------------------------ queries
    @Transactional(readOnly = true)
    public Page<StudentResponse> list(int page, int size, String search, String className, String status, String sortBy, String direction) {
        String sortField = SORTABLE.contains(sortBy) ? sortBy : "rollNumber";
        Sort sort = "desc".equalsIgnoreCase(direction) ? Sort.by(sortField).descending() : Sort.by(sortField).ascending();
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, Math.min(size, 100)), sort);
        return repo.findAll(buildFilter(search, className, status), pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public StudentResponse get(Long id) {
        return mapper.toResponse(find(id));
    }

    @Transactional(readOnly = true)
    public List<String> classNames() {
        return repo.findDistinctClassNames();
    }

    // ----------------------------------------------------------------- commands
    public StudentResponse create(StudentRequest req) {
        if (repo.existsByRollNumberIgnoreCase(req.rollNumber().trim())) throw new IllegalArgumentException("Roll number already exists.");
        if (repo.existsByEmailIgnoreCase(req.email().trim())) throw new IllegalArgumentException("Email already exists.");
        Student student = new Student();
        apply(student, req);
        return mapper.toResponse(repo.save(student));
    }

    public StudentResponse update(Long id, StudentRequest req) {
        Student student = find(id);
        if (repo.existsByRollNumberIgnoreCaseAndIdNot(req.rollNumber().trim(), id)) throw new IllegalArgumentException("Roll number already exists.");
        if (repo.existsByEmailIgnoreCaseAndIdNot(req.email().trim(), id)) throw new IllegalArgumentException("Email already exists.");
        apply(student, req);
        return mapper.toResponse(repo.save(student));
    }

    public void delete(Long id) {
        repo.delete(find(id));
    }

    // ---------------------------------------------------------------- dashboard
    @Transactional(readOnly = true)
    public DashboardResponse dashboard() {
        List<Student> all = repo.findAllWithRecordsBy();

        Map<String, Long> grades = new LinkedHashMap<>();
        PerformanceCalculator.GRADE_ORDER.forEach(g -> grades.put(g, 0L));
        Map<String, List<BigDecimal>> bySubject = new TreeMap<>();
        Set<String> classes = new HashSet<>();
        BigDecimal total = BigDecimal.ZERO;
        long graded = 0, active = 0, recordCount = 0;

        for (Student s : all) {
            classes.add(s.getClassName());
            if (s.getStatus() == StudentStatus.ACTIVE) active++;
            recordCount += s.getRecords().size();
            for (AcademicRecord r : s.getRecords()) {
                bySubject.computeIfAbsent(r.getSubject(), k -> new ArrayList<>()).add(r.getPercentage());
            }
            if (s.getRecords().isEmpty()) continue;           // students without marks do not distort the averages
            BigDecimal avg = PerformanceCalculator.average(s);
            total = total.add(avg);
            graded++;
            grades.merge(PerformanceCalculator.grade(avg), 1L, Long::sum);
        }

        BigDecimal overall = graded == 0 ? BigDecimal.ZERO.setScale(2) : total.divide(BigDecimal.valueOf(graded), 2, RoundingMode.HALF_UP);
        List<DashboardResponse.SubjectAverage> subjects = new ArrayList<>();
        bySubject.forEach((subject, values) -> {
            BigDecimal sum = values.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            subjects.add(new DashboardResponse.SubjectAverage(subject, sum.divide(BigDecimal.valueOf(values.size()), 2, RoundingMode.HALF_UP)));
        });
        return new DashboardResponse(all.size(), active, classes.size(), overall, recordCount,
            rankingService.top(5, null), grades, subjects);
    }

    // ------------------------------------------------------------------ helpers
    private Specification<Student> buildFilter(String search, String className, String status) {
        Specification<Student> spec = (root, q, cb) -> cb.conjunction();
        if (search != null && !search.isBlank()) {
            String term = "%" + search.trim().toLowerCase() + "%";
            spec = spec.and((r, q, cb) -> cb.or(
                cb.like(cb.lower(r.get("fullName")), term),
                cb.like(cb.lower(r.get("rollNumber")), term),
                cb.like(cb.lower(r.get("email")), term)));
        }
        if (className != null && !className.isBlank()) spec = spec.and((r, q, cb) -> cb.equal(r.get("className"), className));
        if (status != null && !status.isBlank()) {
            try {
                StudentStatus parsed = StudentStatus.valueOf(status.toUpperCase());
                spec = spec.and((r, q, cb) -> cb.equal(r.get("status"), parsed));
            } catch (IllegalArgumentException ignored) {
                // unknown status value: ignore the filter instead of failing the request
            }
        }
        return spec;
    }

    private Student find(Long id) {
        return repo.findById(id).orElseThrow(() -> new NoSuchElementException("Student not found: " + id));
    }

    private void apply(Student s, StudentRequest r) {
        s.setRollNumber(r.rollNumber().trim());
        s.setFullName(r.fullName().trim());
        s.setEmail(r.email().trim().toLowerCase());
        s.setPhone(r.phone() == null ? "" : r.phone().trim());
        s.setClassName(r.className().trim());
        s.setDepartment(r.department().trim());
        s.setYearOfStudy(r.yearOfStudy());
        s.setStatus(r.status());

        List<AcademicRecord> records = new ArrayList<>();
        if (r.records() != null) {
            for (RecordRequest rr : r.records()) {
                if (rr.marksObtained().compareTo(rr.maxMarks()) > 0) throw new IllegalArgumentException("Marks obtained cannot exceed maximum marks.");
                AcademicRecord a = new AcademicRecord();
                a.setTerm(rr.term().trim());
                a.setSubject(rr.subject().trim());
                a.setMarksObtained(rr.marksObtained());
                a.setMaxMarks(rr.maxMarks());
                records.add(a);
            }
        }
        s.replaceRecords(records);
    }
}
