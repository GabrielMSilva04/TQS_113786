package deti.tqs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class TqsStackTest {

    private TqsStack<Integer> stack;

    @BeforeEach
    void setUp() {
        stack = new TqsStack<>();
    }

    @Test
    void testPush() {
        stack.push(1);
        assertEquals(1, stack.size());
        assertFalse(stack.isEmpty());
    }

    @Test
    void testPop() {
        stack.push(1);
        stack.push(2);
        assertEquals(2, stack.pop());
        assertEquals(1, stack.pop());
        assertTrue(stack.isEmpty());
    }

    @Test
    void testPopEmptyStack() {
        assertThrows(NoSuchElementException.class, stack::pop);
    }

    @Test
    void testPeek() {
        stack.push(1);
        assertEquals(1, stack.peek());
        assertEquals(1, stack.size());
    }

    @Test
    void testPeekEmptyStack() {
        assertThrows(NoSuchElementException.class, stack::peek);
    }

    @Test
    void testIsEmpty() {
        assertTrue(stack.isEmpty());
        stack.push(1);
        assertFalse(stack.isEmpty());
    }

    @Test
    void testBoundedStack() {
        TqsStack<Integer> boundedStack = new TqsStack<>(1);
        boundedStack.push(1);
        assertThrows(IllegalStateException.class, () -> boundedStack.push(2));
    }

    @Test
    void testPopTopN() {
        stack.push(1);
        stack.push(2);
        stack.push(3);
        stack.push(4);
        assertEquals(3, stack.popTopN(3));
    }
}