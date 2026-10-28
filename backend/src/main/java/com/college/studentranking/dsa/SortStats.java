package com.college.studentranking.dsa;

/**
 * Mutable counter used to measure how much work a sorting/searching algorithm performs.
 */
public final class SortStats {
    private long comparisons;
    private long swaps;
    
    public void compare() {
        comparisons++;
    }
    
    public void swap() {
        swaps++;
    }
    
    public long comparisons() {
        return comparisons;
    }
    
    public long swaps() {
        return swaps;
    }
}
