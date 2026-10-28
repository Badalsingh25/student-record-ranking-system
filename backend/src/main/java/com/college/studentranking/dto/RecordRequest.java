package com.college.studentranking.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record RecordRequest(
    @NotBlank @Size(max=40) String term,
    @NotBlank @Size(max=100) String subject,
    @NotNull @DecimalMin("0.0") BigDecimal marksObtained,
    @NotNull @DecimalMin("0.01") BigDecimal maxMarks
) {}
