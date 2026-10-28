package com.college.studentranking.dto;

import com.college.studentranking.model.StudentStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public record StudentRequest(
        @NotBlank @Size(max = 30) String rollNumber,
        @NotBlank @Size(max = 120) String fullName,
        @NotBlank @Email @Size(max = 160) String email,
        @Pattern(regexp = "^$|^[0-9+() -]{7,20}$", message = "Phone number format is invalid") String phone,
        @NotBlank @Size(max = 40) String className,
        @NotBlank @Size(max = 80) String department,
        @NotNull @Min(1) @Max(8) Integer yearOfStudy,
        @NotNull StudentStatus status,
        @Valid List<RecordRequest> records
) {
}
