/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tqs.euromillions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import tqs.euromillions.Dip;

/**
 * @author ico0
 */
public class DipTest {

    private Dip sampleInstance;


    @BeforeEach
    public void setUp() {
        sampleInstance = new Dip(new int[]{10, 20, 30, 40, 50}, new int[]{1, 2});
    }

    @AfterEach
    public void tearDown() {
        sampleInstance = null;
    }

    @DisplayName("format as string show all elements")
    @Test
    public void testFormat() {
        String result = sampleInstance.format();
        assertEquals("N[ 10 20 30 40 50] S[  1  2]", result, "format as string: formatted string not as expected. ");
    }

    @DisplayName("new Dip rejects wrong size ou negatives")
    @Test
    public void testConstructorFromBadArrays() {

        // insufficient args
        assertThrows(IllegalArgumentException.class,
                () -> new Dip( new int[]{10, 11}, new int[]{} ) );

        //negative numbers
        assertThrows(IllegalArgumentException.class,
                () -> new Dip( new int[]{10, 11, 12, 13, -1}, new int[]{1, 2} ) );

        // this test will reveal that the code was not yet checking ranges


    }

    @DisplayName("new Dip rejects out of range elements")
    @Test
    public void testConstructorFromBadRanges() {
        // creating Dip with numbers or starts outside the expected range
        // expects an exception
        assertThrows(IllegalArgumentException.class,
                () -> new Dip( new int[]{10, 11, 12, 13, Dip.NUMBERS_RANGE_MAX * 2}, new int[]{1,2} ) );
        assertThrows(IllegalArgumentException.class,
                () -> new Dip( new int[]{11, 12, 13, 14, 15}, new int[]{ Dip.STARS_RANGE_MAX*2 ,1} ) );

    }

    @Test
    public void testRandomDip() {
        Dip randomDip = Dip.generateRandomDip();
        assertNotNull(randomDip, "generateRandomDip: returned null");
        assertEquals(Dip.NUMBERS_REQUIRED, randomDip.getNumbersColl().size(), "generateRandomDip: numbers set has wrong size");
        assertEquals(Dip.STARS_REQUIRED, randomDip.getStarsColl().size(), "generateRandomDip: stars set has wrong size");
    }


    @DisplayName("equals method returns true for equal objects")
    @Test
    public void equalsReturnsTrueForEqualObjects() {
        Dip dip1 = new Dip(new int[]{1, 2, 3, 4, 5}, new int[]{1, 2});
        Dip dip2 = new Dip(new int[]{1, 2, 3, 4, 5}, new int[]{1, 2});
        assertTrue(dip1.equals(dip2), "equals: two Dips with the same elements should be equal");
    }

    @DisplayName("equals method returns false for different objects")
    @Test
    public void equalsReturnsFalseForDifferentObjects() {
        Dip dip1 = new Dip(new int[]{1, 2, 3, 4, 5}, new int[]{1, 2});
        Dip dip2 = new Dip(new int[]{6, 7, 8, 9, 10}, new int[]{3, 4});
        assertFalse(dip1.equals(dip2), "equals: two Dips with different elements should not be equal");
    }

    @DisplayName("equals method returns false for null")
    @Test
    public void equalsReturnsFalseForNull() {
        Dip dip1 = new Dip(new int[]{1, 2, 3, 4, 5}, new int[]{1, 2});
        assertFalse(dip1.equals(null), "equals: comparing to null should return false");
    }

    @DisplayName("equals method returns false for different class")
    @Test
    public void equalsReturnsFalseForDifferentClass() {
        Dip dip1 = new Dip(new int[]{1, 2, 3, 4, 5}, new int[]{1, 2});
        String notADip = "Not a Dip";
        assertFalse(dip1.equals(notADip), "equals: comparing to different class should return false");
    }

    @DisplayName("equals method returns true for a copy")
    @Test
    public void equalsReturnsTrueForCopy() {
        Dip dip1 = new Dip(new int[]{1, 2, 3, 4, 5}, new int[]{1, 2});
        Dip dip2 = dip1;
        assertTrue(dip1.equals(dip2), "equals: two Dips that are the same object should be equal");
    }

    @DisplayName("hashCode returns the same value for equal objects")
    @Test
    public void hashCodeReturnsSameValueForEqualObjects() {
        Dip dip1 = new Dip(new int[]{1, 2, 3, 4, 5}, new int[]{1, 2});
        Dip dip2 = new Dip(new int[]{1, 2, 3, 4, 5}, new int[]{1, 2});
        assertEquals(dip1.hashCode(), dip2.hashCode(), "hashCode: two Dips with the same elements should have the same hash code");
    }

}
