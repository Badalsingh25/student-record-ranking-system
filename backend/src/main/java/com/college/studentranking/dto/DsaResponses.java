package com.college.studentranking.dto;

import java.util.List;

/**
 * Response shapes for the "Algorithm Lab" endpoints.
 */
public final class DsaResponses {
    private DsaResponses() {
    }
    
    public record SortRun(String algorithm, String label, String timeComplexity, String spaceComplexity, boolean stable,
                          int n, long comparisons, long swaps, long elapsedMicros) {
    }
    
    public record SortResponse(SortRun run, String field, String order, List<StudentResponse> students) {
    }
    
    public record SearchResponse(String mode, String label, String complexity, String query, int n,
                                 long comparisons, long elapsedMicros, List<StudentResponse> matches) {
    }
}
