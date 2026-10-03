package org.mockito.internal.matchers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class EqualityGeneratedTest {

    @Test
    public void shouldTreatSameReferenceAndTwoNullsAsEqual() {
        Object value = new Object();

        assertTrue(Equality.areEqual(value, value));
        assertTrue(Equality.areEqual(null, null));
    }

    @Test
    public void shouldTreatExactlyOneNullAsNotEqual() {
        assertFalse(Equality.areEqual(null, "value"));
        assertFalse(Equality.areEqual("value", null));
    }

    @Test
    public void shouldUseEqualsForNonArrayValues() {
        assertTrue(Equality.areEqual(new Value(7), new Value(7)));
        assertFalse(Equality.areEqual(new Value(7), new Value(8)));
        assertTrue(Equality.areEqual("mockito", new String("mockito")));
        assertFalse(Equality.areEqual("mockito", "junit"));
    }

    @Test
    public void shouldNotConsiderDistinctObjectsWithoutEqualsImplementationEqual() {
        assertFalse(Equality.areEqual(new Object(), new Object()));
    }

    @Test
    public void shouldComparePrimitiveArraysByLengthAndElements() {
        assertTrue(Equality.areEqual(new int[] {1, 2, 3}, new int[] {1, 2, 3}));
        assertFalse(Equality.areEqual(new int[] {1, 2, 3}, new int[] {1, 2}));
        assertFalse(Equality.areEqual(new int[] {1, 2, 3}, new int[] {1, 9, 3}));
    }

    @Test
    public void shouldCompareObjectArraysByLengthAndElements() {
        assertTrue(Equality.areEqual(
                new String[] {"one", "two"},
                new String[] {new String("one"), new String("two")}));
        assertFalse(Equality.areEqual(
                new String[] {"one", "two"},
                new String[] {"one", "three"}));
        assertFalse(Equality.areEqual(
                new String[] {"one"},
                new String[] {"one", "two"}));
    }

    @Test
    public void shouldCompareArrayElementsEvenWhenArrayRuntimeTypesDiffer() {
        assertTrue(Equality.areEqual(
                new String[] {"one", "two"},
                new Object[] {"one", "two"}));
        assertTrue(Equality.areEqual(
                new int[] {1, 2},
                new Integer[] {Integer.valueOf(1), Integer.valueOf(2)}));
        assertFalse(Equality.areEqual(
                new int[] {1, 2},
                new long[] {1L, 2L}));
    }

    @Test
    public void shouldCompareNestedArraysRecursively() {
        Object[] left = new Object[] {
                new int[] {1, 2},
                new Object[] {"a", new String[] {"b", "c"}}
        };
        Object[] equalRight = new Object[] {
                new int[] {1, 2},
                new Object[] {"a", new String[] {new String("b"), "c"}}
        };
        Object[] differentRight = new Object[] {
                new int[] {1, 2},
                new Object[] {"a", new String[] {"b", "x"}}
        };

        assertTrue(Equality.areEqual(left, equalRight));
        assertFalse(Equality.areEqual(left, differentRight));
    }

    @Test
    public void shouldReportArrayLengthEqualityThroughHelper() {
        assertTrue(Equality.areArrayLengthsEqual(new boolean[] {true, false}, new Object[] {"x", "y"}));
        assertFalse(Equality.areArrayLengthsEqual(new byte[] {1}, new byte[] {1, 2}));
    }

    @Test
    public void shouldReportArrayElementEqualityThroughHelpers() {
        assertTrue(Equality.areArrayElementsEqual(
                new Object[] {new int[] {1, 2}, "x"},
                new Object[] {new int[] {1, 2}, new String("x")}));
        assertFalse(Equality.areArrayElementsEqual(
                new Object[] {new int[] {1, 2}, "x"},
                new Object[] {new int[] {1, 3}, "x"}));
        assertTrue(Equality.areArraysEqual(
                new Object[] {"x", new int[] {4}},
                new Object[] {new String("x"), new int[] {4}}));
    }

    private static final class Value {
        private final int value;

        private Value(int value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof Value && value == ((Value) other).value;
        }

        @Override
        public int hashCode() {
            return value;
        }
    }

@Test
public void shouldNotConsiderArraysAndNonArraysEqual() {
    assertFalse(Equality.areEqual(new int[] {1, 2}, "not an array"));
    assertFalse(Equality.areEqual("not an array", new int[] {1, 2}));
}
}
