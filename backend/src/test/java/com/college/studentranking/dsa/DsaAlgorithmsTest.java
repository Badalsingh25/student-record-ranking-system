package com.college.studentranking.dsa;

import com.college.studentranking.dsa.SortingAlgorithms.Algorithm;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class DsaAlgorithmsTest {
    private static Integer[] randomArray(int n, long seed) {
        Random r = new Random(seed);
        Integer[] a = new Integer[n];
        for (int i = 0; i < n; i++) a[i] = r.nextInt(50);   // many duplicates on purpose
        return a;
    }

    @Test void everyAlgorithmMatchesTheReferenceSort() {
        for (Algorithm algo : Algorithm.values()) {
            for (int n : new int[]{0, 1, 2, 3, 10, 101, 500}) {
                Integer[] actual = randomArray(n, n);
                Integer[] expected = actual.clone();
                Arrays.sort(expected);
                SortingAlgorithms.sort(actual, Comparator.naturalOrder(), algo, new SortStats());
                assertArrayEquals(expected, actual, algo + " failed for n=" + n);
            }
        }
    }

    @Test void sortsDescendingWithReversedComparator() {
        Integer[] a = {3, 9, 1, 7};
        SortingAlgorithms.mergeSort(a, Comparator.<Integer>naturalOrder().reversed(), new SortStats());
        assertArrayEquals(new Integer[]{9, 7, 3, 1}, a);
    }

    @Test void quickSortSurvivesSortedAndReverseSortedInput() {
        Integer[] asc = new Integer[5000], desc = new Integer[5000];
        for (int i = 0; i < asc.length; i++) { asc[i] = i; desc[i] = asc.length - i; }
        SortingAlgorithms.quickSort(asc, 0, asc.length - 1, Comparator.naturalOrder(), new SortStats());
        SortingAlgorithms.quickSort(desc, 0, desc.length - 1, Comparator.naturalOrder(), new SortStats());
        assertEquals(0, asc[0]);
        assertEquals(1, desc[0]);
    }

    @Test void mergeAndInsertionSortAreStable() {
        record Item(int key, int order) {}
        for (Algorithm algo : new Algorithm[]{Algorithm.MERGE, Algorithm.INSERTION}) {
            Random r = new Random(7);
            Item[] items = new Item[200];
            for (int i = 0; i < items.length; i++) items[i] = new Item(r.nextInt(5), i);
            SortingAlgorithms.sort(items, Comparator.comparingInt(Item::key), algo, new SortStats());
            for (int i = 1; i < items.length; i++) {
                if (items[i - 1].key() == items[i].key()) assertTrue(items[i - 1].order() < items[i].order(), algo + " not stable");
            }
        }
    }

    @Test void statsAreRecorded() {
        SortStats stats = new SortStats();
        SortingAlgorithms.mergeSort(randomArray(100, 1), Comparator.naturalOrder(), stats);
        assertTrue(stats.comparisons() > 0);
    }

    @Test void binarySearchFindsEveryElementAndRejectsMissing() {
        Integer[] a = {2, 4, 6, 8, 10, 12, 14};
        for (int i = 0; i < a.length; i++) assertEquals(i, SearchAlgorithms.binary(a, a[i], Comparator.naturalOrder()).index());
        assertFalse(SearchAlgorithms.binary(a, 5, Comparator.naturalOrder()).found());
        assertFalse(SearchAlgorithms.binary(new Integer[0], 5, Comparator.naturalOrder()).found());
    }

    @Test void binarySearchUsesFarFewerStepsThanLinear() {
        Integer[] a = new Integer[1024];
        for (int i = 0; i < a.length; i++) a[i] = i;
        long binary = SearchAlgorithms.binary(a, 1023, Comparator.naturalOrder()).comparisons();
        long linear = SearchAlgorithms.linear(a, 1023, Comparator.naturalOrder()).comparisons();
        assertTrue(binary <= 11);
        assertEquals(1024, linear);
    }

    @Test void lowerBoundFindsFirstPrefixMatch() {
        String[] names = {"Aarav", "Ananya", "Diya", "Dev", "Isha"};
        Arrays.sort(names, String.CASE_INSENSITIVE_ORDER);
        int i = SearchAlgorithms.lowerBound(names, "d", s -> s);
        assertEquals("Dev", names[i]);
        assertEquals(names.length, SearchAlgorithms.lowerBound(names, "zzz", s -> s));
    }

    @Test void minHeapKeepsOnlyTheKLargest() {
        MinHeap<Integer> heap = new MinHeap<>(3, Comparator.naturalOrder());
        for (int v : new int[]{5, 1, 9, 3, 7, 8, 2}) heap.offer(v);
        assertEquals(3, heap.size());
        assertEquals(7, heap.poll());
        assertEquals(8, heap.poll());
        assertEquals(9, heap.poll());
    }

    @Test void hashIndexIsCaseInsensitive() {
        RollNumberIndex<String> idx = RollNumberIndex.build(List.of("CSE001", "IT002"), s -> s);
        assertTrue(idx.find(" cse001 ").isPresent());
        assertFalse(idx.contains("ECE999"));
        assertEquals(2, idx.size());
    }
}
