package org.apache.commons.lang.text;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StrBuilderLang299Test {

    @Test
    public void testInsertCharArraySliceAtBeginning() {
        StrBuilder builder = new StrBuilder("tail");

        builder.insert(0, new char[] { 'x', 'a', 'b', 'y' }, 1, 2);

        assertEquals("abtail", builder.toString());
        assertEquals(6, builder.length());
        assertEquals('a', builder.charAt(0));
    }

    @Test
    public void testInsertCharArraySliceInMiddle() {
        StrBuilder builder = new StrBuilder("abcdef");

        builder.insert(3, new char[] { '0', 'X', 'Y', 'Z', '9' }, 1, 3);

        assertEquals("abcXYZdef", builder.toString());
        assertEquals('X', builder.charAt(3));
        assertEquals('d', builder.charAt(6));
    }

    @Test
    public void testInsertCharArraySliceAtEndUsingRemainingArray() {
        StrBuilder builder = new StrBuilder("start");

        builder.insert(builder.length(), new char[] { 'q', 'r', 's', 't' }, 1, 3);

        assertEquals("startrst", builder.toString());
        assertEquals(8, builder.length());
        assertArrayEquals(
                new char[] { 's', 't', 'a', 'r', 't', 'r', 's', 't' },
                builder.toCharArray());
    }

    @Test
    public void testInsertCharArraySliceForcesCapacityGrowthAndPreservesSuffix() {
        StrBuilder builder = new StrBuilder(2);
        builder.append("ab");

        builder.insert(1, new char[] { 'W', 'X', 'Y', 'Z' }, 0, 4);

        assertEquals("aWXYZb", builder.toString());
        assertEquals(6, builder.length());
        assertEquals('a', builder.charAt(0));
        assertEquals('b', builder.charAt(5));
    }

    @Test
    public void testInsertSingleElementSliceWithNonZeroOffset() {
        StrBuilder builder = new StrBuilder("left-right");

        builder.insert(5, new char[] { '0', 'M', '2' }, 1, 1);

        assertEquals("left-Mright", builder.toString());
        assertEquals('M', builder.charAt(5));
    }

    @Test
    public void testInsertZeroLengthSliceLeavesContentUnchanged() {
        StrBuilder builder = new StrBuilder("content");

        builder.insert(3, new char[] { 'a', 'b', 'c' }, 3, 0);

        assertEquals("content", builder.toString());
        assertEquals(7, builder.length());
    }

    @Test(expected = StringIndexOutOfBoundsException.class)
    public void testInsertCharArraySliceRejectsInvalidInsertionIndex() {
        new StrBuilder("abc").insert(4, new char[] { 'x' }, 0, 1);
    }

    @Test(expected = StringIndexOutOfBoundsException.class)
    public void testInsertCharArraySliceRejectsInvalidOffset() {
        new StrBuilder("abc").insert(1, new char[] { 'x', 'y' }, 3, 0);
    }

    @Test(expected = StringIndexOutOfBoundsException.class)
    public void testInsertCharArraySliceRejectsLengthBeyondSourceArray() {
        new StrBuilder("abc").insert(1, new char[] { 'x', 'y' }, 1, 2);
    }

@Test
public void testConstructorsHandleZeroCapacityAndNullString() {
    StrBuilder zeroCapacity = new StrBuilder(0);
    zeroCapacity.append('x');
    assertEquals("x", zeroCapacity.toString());

    StrBuilder nullString = new StrBuilder((java.lang.String) null);
    nullString.append('y');
    assertEquals("y", nullString.toString());
}

@Test
public void testAppendObjectAndStringNullUseConfiguredNullText() {
    StrBuilder builder = new StrBuilder().setNullText("<null>");

    builder.append((java.lang.Object) null);
    builder.append((java.lang.String) null);
    builder.append((java.lang.Object) new java.lang.StringBuilder("value"));

    assertEquals("<null><null>value", builder.toString());
}

@Test
public void testAppendStringSliceAcceptsBoundariesAndRejectsInvalidRanges() {
    StrBuilder builder = new StrBuilder();

    builder.append("ABCDE", 1, 3);
    builder.append("ABCDE", 5, 0);
    assertEquals("BCD", builder.toString());

    try {
        builder.append("ABCDE", -1, 1);
        org.junit.Assert.fail("Expected StringIndexOutOfBoundsException");
    } catch (java.lang.StringIndexOutOfBoundsException expected) {
    }

    try {
        builder.append("ABCDE", 4, 2);
        org.junit.Assert.fail("Expected StringIndexOutOfBoundsException");
    } catch (java.lang.StringIndexOutOfBoundsException expected) {
    }

    assertEquals("BCD", builder.toString());
}

@Test
public void testAppendPrimitiveValuesUsesStringRepresentations() {
    StrBuilder builder = new StrBuilder();

    builder.append('x').append(12).append(34L).append(1.5f).append(2.5d);

    assertEquals("x12341.52.5", builder.toString());
}
}
