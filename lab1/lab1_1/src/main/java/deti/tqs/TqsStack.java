package deti.tqs;

import java.util.LinkedList;
import java.util.NoSuchElementException;

public class TqsStack<T> {
    private LinkedList<T> list;
    private int bound;

    public TqsStack() {
        list = new LinkedList<>();
    }

    public TqsStack(int bound) {
        list = new LinkedList<>();
        this.bound = bound;
    }

    public T pop() {
        if (list.isEmpty()) {
            throw new NoSuchElementException();
        }

        return list.removeLast();
    }

    public int size() {
        return list.size();
    }

    public T peek() {
        if (list.isEmpty()) {
            throw new NoSuchElementException();
        }

        return list.getLast();
    }

    public void push(T item) {
        if (bound > 0 && list.size() == bound) {
            throw new IllegalStateException();
        }
        list.add(item);
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public T popTopN(int n) {
        T top = null;
        for (int i = 0; i < n; i++) {
            top = list.removeFirst();
        }
        return top;
    }
}
