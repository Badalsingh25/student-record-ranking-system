package com.college.studentranking.repository;

import com.college.studentranking.model.Student;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {
    boolean existsByRollNumberIgnoreCase(String rollNumber);
    
    boolean existsByRollNumberIgnoreCaseAndIdNot(String rollNumber, Long id);
    
    boolean existsByEmailIgnoreCase(String email);
    
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
    
    /**
     * Loads students together with their records in one query (avoids N+1 when ranking everyone).
     */
    @EntityGraph(attributePaths = "records")
    List<Student> findAllWithRecordsBy();
    
    @Query("select distinct s.className from Student s order by s.className")
    List<String> findDistinctClassNames();
}
