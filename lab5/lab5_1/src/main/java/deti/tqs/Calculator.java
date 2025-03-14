package deti.tqs;

import static java.util.Arrays.asList;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class Calculator {
    private final Deque<Number> stack = new LinkedList<Number>();
    private static final List<String> OPS = asList("-", "+", "*", "/");

    public void push(Object arg) {
        if (OPS.contains(arg)) {
            Number y = stack.removeLast();
            Number x = stack.isEmpty() ? 0 : stack.removeLast();
            Integer val = null;
            if (arg.equals("-")) {
                val = x.intValue() - y.intValue();
            } else if (arg.equals("+")) {
                val = x.intValue() + y.intValue();
            } else if (arg.equals("*")) {
                val = x.intValue() * y.intValue();
            } else if (arg.equals("/")) {
                val = x.intValue() / y.intValue();
            }
            push(val);
        } else if (arg instanceof Number) {
            stack.add((Number) arg);
        } else {
            throw new IllegalArgumentException("Invalid operation");
        }
    }

    public Number value() {
        if (stack.isEmpty()) {
            throw new IllegalArgumentException("No value in stack");
        }
        return stack.getLast().intValue();
    }
}