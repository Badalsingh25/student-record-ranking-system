package com.college.studentranking.dsa;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Hash-map index: roll number (case-insensitive) -> value. Average O(1) lookup and duplicate detection.
 */
public final class RollNumberIndex<T> {
    private final Map<String, T> index = new HashMap<>();
    
    public static <T> RollNumberIndex<T> build(Iterable<T> items, java.util.function.Function<T, String> key) {
        RollNumberIndex<T> idx = new RollNumberIndex<>();
        for (T item : items) {
            idx.index.put(normalize(key.apply(item)), item);
        }
        return idx;
    }
    
    public Optional<T> find(String rollNumber) {
        return Optional.ofNullable(index.get(normalize(rollNumber)));
    }
    
    public boolean contains(String rollNumber) {
        return index.containsKey(normalize(rollNumber));
    }
    
    public int size() {
        return index.size();
    }
    
    private static String normalize(String s) {
        return s == null ? "" : s.trim().toUpperCase();
    }
}
