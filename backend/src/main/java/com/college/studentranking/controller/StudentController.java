package com.college.studentranking.controller;

import com.college.studentranking.dto.*;
import com.college.studentranking.service.DsaService;
import com.college.studentranking.service.RankingService;
import com.college.studentranking.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class StudentController {
    
    private final StudentService service;
    private final RankingService rankingService;
    private final DsaService dsaService;
    
    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of("status", "UP",
                "service", "Student Record & Ranking API"
        );
    }
    
    // ---- CRUD
    @GetMapping("/students")
    public Page<StudentResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String className,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "rollNumber") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        return service.list(page, size, search, className, status, sortBy, direction);
    }
    
    @GetMapping("/students/{id}")
    public StudentResponse get(@PathVariable Long id) {
        return service.get(id);
    }
    
    @PostMapping("/students")
    public ResponseEntity<StudentResponse> create(@Valid @RequestBody StudentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }
    
    @PutMapping("/students/{id}")
    public StudentResponse update(@PathVariable Long id, @Valid @RequestBody StudentRequest request) {
        return service.update(id, request);
    }
    
    @DeleteMapping("/students/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/classes")
    public List<String> classes() {
        return service.classNames();
    }
    
    // ---- ranking
    @GetMapping("/rankings")
    public List<StudentResponse> rankings(@RequestParam(required = false) String className) {
        return rankingService.leaderboard(className);
    }
    
    @GetMapping("/rankings/top")
    public List<StudentResponse> top(@RequestParam(defaultValue = "10") int limit,
                                     @RequestParam(required = false) String className) {
        return rankingService.top(limit, className);
    }
    
    @GetMapping("/dashboard")
    public DashboardResponse dashboard() {
        return service.dashboard();
    }
    
    // ---- algorithm lab
    @GetMapping("/dsa/sort")
    public DsaResponses.SortResponse sort(
            @RequestParam(defaultValue = "merge") String algorithm,
            @RequestParam(defaultValue = "marks") String field,
            @RequestParam(required = false) String order,
            @RequestParam(required = false) String className) {
        return dsaService.sort(algorithm, field, order, className);
    }
    
    @GetMapping("/dsa/search")
    public DsaResponses.SearchResponse search(@RequestParam(defaultValue = "hash") String mode,
                                              @RequestParam(defaultValue = "") String q) {
        return dsaService.search(mode, q);
    }
}
