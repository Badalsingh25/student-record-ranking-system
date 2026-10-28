package com.college.studentranking.dsa;

import java.util.Comparator;
import java.util.function.Function;

/**
 * Array-based searching algorithms.
 */
public final class SearchAlgorithms {
    private SearchAlgorithms() {
    }
    
    /**
     * Result of a search: index (-1 when missing) and how many comparisons it took.
     */
    public record Result(int index, long comparisons) {
        public boolean found() {
            return index >= 0;
        }
    }
    
    /**
     * O(n) scan; works on unsorted data.
     */
    public static <T> Result linear(T[] a, T target, Comparator<? super T> cmp) {
        long steps = 0;
        for (int i = 0; i < a.length; i++) {
            steps++;
            if (cmp.compare(a[i], target) == 0) {
                return new Result(i, steps);
            }
        }
        return new Result(-1, steps);
    }
    
    /**
     * O(log n) binary search; the array MUST already be sorted by the same comparator.
     */
    public static <T> Result binary(T[] a, T target, Comparator<? super T> cmp) {
        int lo = 0, hi = a.length - 1;
        long steps = 0;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            steps++;
            int c = cmp.compare(a[mid], target);
            if (c == 0) {
                return new Result(mid, steps);
            }
            if (c < 0) {
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }
        return new Result(-1, steps);
    }
    
    /**
     * Lower-bound binary search: the first index whose key is >= prefix (array sorted by key, case-insensitive).
     * Used together with a forward scan to find every entry that starts with a prefix in O(log n + k).
     */
    public static <T> int lowerBound(T[] a, String prefix, Function<T, String> key) {
        int lo = 0, hi = a.length;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (key.apply(a[mid]).compareToIgnoreCase(prefix) < 0) {
                lo = mid + 1;
            } else {
                hi = mid;
            }
        }
        return lo;
    }
}
