/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tqs.sets;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import tqs.sets.BoundedSetOfNaturals;

/**
 * @author ico0
 */
class BoundedSetOfNaturalsTest {
    private BoundedSetOfNaturals setA;
    private BoundedSetOfNaturals setB;
    private BoundedSetOfNaturals setC;
    private BoundedSetOfNaturals setD;


    @BeforeEach
    public void setUp() {
        setA = new BoundedSetOfNaturals(1);
        setB = BoundedSetOfNaturals.fromArray(new int[]{10, 20, 30, 40, 50, 60});
        setC = BoundedSetOfNaturals.fromArray(new int[]{50, 60});
        setD = new BoundedSetOfNaturals(5);
    }

    @AfterEach
    public void tearDown() {
        setA = setB = setC = setD = null;
    }

    @Test
    public void testAddElement() {

        setA.add(99);
        assertTrue(setA.contains(99), "add: added element not found in set.");
        assertEquals(1, setA.size());

        setD.add(10);
        assertThrows(IllegalArgumentException.class, () -> setD.add(10), "add: adding an element already in the set should throw an exception.");

        assertThrows(IllegalArgumentException.class, () -> setB.add(99), "add: adding an element to a full set should throw an exception.");

        assertThrows(IllegalArgumentException.class, () -> setD.add(-99), "add: adding a negative element should throw an exception.");
    }

//    @Disabled("TODO revise to test the construction from invalid arrays")
    @Test
    public void testAddFromBadArray() {
        int[] elems = new int[]{10, -20, -30};

        // must fail with exception
        assertThrows(IllegalArgumentException.class, () -> setA.add(elems));
    }

    @Test
    public void testEquals() {
        setA.add(99);
        BoundedSetOfNaturals setA2 = setA;

        assertTrue(setA.equals(setA2), "equals: two sets with the same element should be equal");

        assertFalse(setA.equals(setB), "equals: two sets with different elements should not be equal");

        int test1 = 2;
        assertFalse(setA.equals(test1), "equals: two objects of different classes should not be equal");

        BoundedSetOfNaturals test2 = null;
        assertFalse(setA.equals(test2), "equals: comparing to null should return false");
    }

    @Test
    public void testIntersects() {
        assertTrue(setB.intersects(setC), "intersects: two sets with common elements should intersect");
        assertFalse(setA.intersects(setB), "intersects: two sets with no common elements should not intersect");
    }

    @Test
    public void testSize() {
        assertEquals(0, setA.size(), "size: setA should have size 1");
        assertEquals(6, setB.size(), "size: setB should have size 6");
    }

    @Test
    public void testContains() {
        assertTrue(setB.contains(10), "contains: setB should contain 10");
        assertFalse(setB.contains(99), "contains: setB should not contain 99");
    }
}
