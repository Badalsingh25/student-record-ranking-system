package com.college.studentranking.dsa;

import java.util.Comparator;

/**
 * Fixed-capacity binary min-heap stored in an array.
 * Keeping only the best K items makes "Top 10 performers" run in O(n log k) instead of sorting everything.
 */
public final class MinHeap<T> {
    private final Object[] heap;
    private final Comparator<? super T> cmp;
    private int size;
    
    public MinHeap(int capacity, Comparator<? super T> cmp) {
        this.heap = new Object[Math.max(1, capacity)];
        this.cmp = cmp;
    }
    
    public int size() {
        return size;
    }
    
    /**
     * Adds an item; once the heap is full the current worst item is evicted if the new one is better.
     */
    public void offer(T item) {
        if (size < heap.length) {
            heap[size] = item;
            siftUp(size++);
        } else if (cmp.compare(item, peek()) > 0) {
            heap[0] = item;
            siftDown(0);
        }
    }
    
    @SuppressWarnings("unchecked")
    public T peek() {
        return (T) heap[0];
    }
    
    @SuppressWarnings("unchecked")
    private T at(int i) {
        return (T) heap[i];
    }
    
    /**
     * Removes and returns the smallest element.
     */
    public T poll() {
        T top = peek();
        heap[0] = heap[--size];
        heap[size] = null;
        if (size > 0) {
            siftDown(0);
        }
        return top;
    }
    
    private void siftUp(int i) {
        while (i > 0) {
            int parent = (i - 1) / 2;
            if (cmp.compare(at(i), at(parent)) >= 0) {
                return;
            }
            swap(i, parent);
            i = parent;
        }
    }
    
    private void siftDown(int i) {
        while (true) {
            int l = 2 * i + 1, r = l + 1, smallest = i;
            if (l < size && cmp.compare(at(l), at(smallest)) < 0) {
                smallest = l;
            }
            if (r < size && cmp.compare(at(r), at(smallest)) < 0) {
                smallest = r;
            }
            if (smallest == i) {
                return;
            }
            swap(i, smallest);
            i = smallest;
        }
    }
    
    private void swap(int i, int j) {
        Object t = heap[i];
        heap[i] = heap[j];
        heap[j] = t;
    }
}
