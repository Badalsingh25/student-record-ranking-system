package com.college.studentranking.dsa;

import java.util.Comparator;

/**
 * Hand-written, array-based sorting algorithms (no java.util.Arrays.sort / Collections.sort).
 * Every algorithm sorts the given array in place and records comparisons/swaps in {@link SortStats}.
 */
public final class SortingAlgorithms {
    private SortingAlgorithms() {
    }
    
    public enum Algorithm {
        MERGE("Merge Sort", "O(n log n)", "O(n)", true),
        QUICK("Quick Sort", "O(n log n) avg, O(n²) worst", "O(log n)", false),
        HEAP("Heap Sort", "O(n log n)", "O(1)", false),
        INSERTION("Insertion Sort", "O(n²)", "O(1)", true);
        
        public final String label, timeComplexity, spaceComplexity;
        public final boolean stable;
        
        Algorithm(String label, String time, String space, boolean stable) {
            this.label = label;
            this.timeComplexity = time;
            this.spaceComplexity = space;
            this.stable = stable;
        }
        
        public static Algorithm from(String value) {
            if (value == null) {
                return MERGE;
            }
            try {
                return valueOf(value.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                return MERGE;
            }
        }
    }
    
    public static <T> void sort(T[] a, Comparator<? super T> cmp, Algorithm algorithm, SortStats stats) {
        switch (algorithm) {
            case QUICK -> quickSort(a, 0, a.length - 1, cmp, stats);
            case HEAP -> heapSort(a, cmp, stats);
            case INSERTION -> insertionSort(a, cmp, stats);
            default -> mergeSort(a, cmp, stats);
        }
    }
    
    // ---------- Merge sort: stable, always O(n log n) ----------
    @SuppressWarnings("unchecked")
    public static <T> void mergeSort(T[] a, Comparator<? super T> cmp, SortStats stats) {
        if (a.length < 2) {
            return;
        }
        T[] buffer = (T[]) new Object[a.length];
        mergeSort(a, buffer, 0, a.length - 1, cmp, stats);
    }
    
    private static <T> void mergeSort(T[] a, T[] buf, int lo, int hi, Comparator<? super T> cmp, SortStats st) {
        if (lo >= hi) {
            return;
        }
        int mid = lo + (hi - lo) / 2;
        mergeSort(a, buf, lo, mid, cmp, st);
        mergeSort(a, buf, mid + 1, hi, cmp, st);
        int i = lo, j = mid + 1, k = lo;
        while (i <= mid && j <= hi) {
            st.compare();
            // "<= 0" keeps equal elements in original order => stable
            buf[k++] = cmp.compare(a[i], a[j]) <= 0 ? a[i++] : a[j++];
        }
        while (i <= mid) {
            buf[k++] = a[i++];
        }
        while (j <= hi) {
            buf[k++] = a[j++];
        }
        for (k = lo; k <= hi; k++) {
            a[k] = buf[k];
        }
    }
    
    // ---------- Quick sort: median-of-three pivot, Lomuto partition ----------
    public static <T> void quickSort(T[] a, int lo, int hi, Comparator<? super T> cmp, SortStats st) {
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            // median of three moved to hi to avoid O(n²) on already sorted input
            if (less(a[mid], a[lo], cmp, st)) {
                swap(a, mid, lo, st);
            }
            if (less(a[hi], a[lo], cmp, st)) {
                swap(a, hi, lo, st);
            }
            if (less(a[mid], a[hi], cmp, st)) {
                swap(a, mid, hi, st);
            }
            T pivot = a[hi];
            int p = lo;
            for (int i = lo; i < hi; i++) {
                if (less(a[i], pivot, cmp, st)) {
                    swap(a, i, p++, st);
                }
            }
            swap(a, p, hi, st);
            // recurse into the smaller half, loop over the larger => O(log n) stack depth
            if (p - lo < hi - p) {
                quickSort(a, lo, p - 1, cmp, st);
                lo = p + 1;
            } else {
                quickSort(a, p + 1, hi, cmp, st);
                hi = p - 1;
            }
        }
    }
    
    // ---------- Heap sort: build max-heap, repeatedly extract the max ----------
    public static <T> void heapSort(T[] a, Comparator<? super T> cmp, SortStats st) {
        int n = a.length;
        for (int i = n / 2 - 1; i >= 0; i--) {
            siftDown(a, i, n, cmp, st);
        }
        for (int end = n - 1; end > 0; end--) {
            swap(a, 0, end, st);
            siftDown(a, 0, end, cmp, st);
        }
    }
    
    private static <T> void siftDown(T[] a, int i, int size, Comparator<? super T> cmp, SortStats st) {
        while (true) {
            int left = 2 * i + 1, right = left + 1, largest = i;
            if (left < size && less(a[largest], a[left], cmp, st)) {
                largest = left;
            }
            if (right < size && less(a[largest], a[right], cmp, st)) {
                largest = right;
            }
            if (largest == i) {
                return;
            }
            swap(a, i, largest, st);
            i = largest;
        }
    }
    
    // ---------- Insertion sort: simple, stable, good for small / nearly sorted input ----------
    public static <T> void insertionSort(T[] a, Comparator<? super T> cmp, SortStats st) {
        for (int i = 1; i < a.length; i++) {
            T key = a[i];
            int j = i - 1;
            while (j >= 0 && less(key, a[j], cmp, st)) {
                a[j + 1] = a[j];
                j--;
                st.swap();
            }
            a[j + 1] = key;
        }
    }
    
    private static <T> boolean less(T x, T y, Comparator<? super T> cmp, SortStats st) {
        st.compare();
        return cmp.compare(x, y) < 0;
    }
    
    private static <T> void swap(T[] a, int i, int j, SortStats st) {
        if (i == j) {
            return;
        }
        T t = a[i];
        a[i] = a[j];
        a[j] = t;
        st.swap();
    }
}
