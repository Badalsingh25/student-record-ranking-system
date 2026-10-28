package com.college.studentranking.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "students", indexes = {
        @Index(name = "idx_student_class", columnList = "class_name"),
        @Index(name = "idx_student_name", columnList = "full_name")
})
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "roll_number", nullable = false, unique = true, length = 30)
    private String rollNumber;
    
    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;
    
    @Column(nullable = false, unique = true, length = 160)
    private String email;
    
    @Column(length = 20)
    private String phone;
    
    @Column(name = "class_name", nullable = false, length = 40)
    private String className;
    
    @Column(nullable = false, length = 80)
    private String department;
    
    @Column(name = "year_of_study", nullable = false)
    private Integer yearOfStudy;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StudentStatus status = StudentStatus.ACTIVE;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 50)
    private List<AcademicRecord> records = new ArrayList<>();
    
    public void addRecord(AcademicRecord record) {
        records.add(record);
        record.setStudent(this);
    }
    
    public void replaceRecords(List<AcademicRecord> newRecords) {
        records.clear();
        if (newRecords != null) {
            newRecords.forEach(this::addRecord);
        }
    }
}
