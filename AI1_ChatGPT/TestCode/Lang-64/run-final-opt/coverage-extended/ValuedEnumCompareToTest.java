package org.apache.commons.lang.enums;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class ValuedEnumCompareToTest {

    private static final FirstEnum FIRST_LOW = new FirstEnum("LOW", 10);
    private static final FirstEnum FIRST_HIGH = new FirstEnum("HIGH", 20);
    private static final FirstEnum FIRST_SAME_VALUE = new FirstEnum("SAME_VALUE", 10);
    private static final SecondEnum SECOND_LOW = new SecondEnum("LOW", 5);

    @Test
    public void compareToOrdersValuesWithinTheSameEnumClass() {
        assertTrue(FIRST_LOW.compareTo(FIRST_HIGH) < 0);
        assertTrue(FIRST_HIGH.compareTo(FIRST_LOW) > 0);
    }

    @Test
    public void compareToReturnsZeroForDifferentInstancesWithTheSameValueAndType() {
        assertEquals(0, FIRST_LOW.compareTo(FIRST_SAME_VALUE));
    }

    @Test
    public void compareToRejectsValuedEnumsOfDifferentClasses() {
        try {
            FIRST_LOW.compareTo(SECOND_LOW);
            fail("Comparing different enum classes must throw ClassCastException");
        } catch (ClassCastException expected) {
            // expected
        }
    }

    @Test
    public void compareToRejectsNull() {
        try {
            FIRST_LOW.compareTo(null);
            fail("Comparing to null must throw NullPointerException");
        } catch (NullPointerException expected) {
            // expected
        }
    }

    @Test
    public void compareToRejectsNonEnumObjects() {
        try {
            FIRST_LOW.compareTo("not an enum");
            fail("Comparing to a non-enum object must throw ClassCastException");
        } catch (ClassCastException expected) {
            // expected
        }
    }

    private static final class FirstEnum extends ValuedEnum {
        private FirstEnum(String name, int value) {
            super(name, value);
        }
    }

    private static final class SecondEnum extends ValuedEnum {
        private SecondEnum(String name, int value) {
            super(name, value);
        }
    }
}
