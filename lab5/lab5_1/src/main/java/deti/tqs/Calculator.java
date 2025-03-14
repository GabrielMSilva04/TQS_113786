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
        } else {
            stack.add((Number) arg);
        }
    }

    public Number value() {
        return stack.getLast().intValue();
    }
}