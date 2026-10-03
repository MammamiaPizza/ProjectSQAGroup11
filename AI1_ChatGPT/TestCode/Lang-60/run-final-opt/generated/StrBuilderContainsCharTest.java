package org.apache.commons.lang.text;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StrBuilderContainsCharTest {

    @Test
    public void testContainsNullCharacterOnEmptyBuilderIsFalse() {
        StrBuilder builder = new StrBuilder();

        assertFalse(builder.contains('\0'));
    }

    @Test
    public void testContainsAbsentNullCharacterDoesNotSearchUnusedCapacity() {
        StrBuilder builder = new StrBuilder(100);
        builder.append("abc");

        assertEquals(3, builder.length());
        assertFalse(builder.contains('\0'));
    }

    @Test
    public void testContainsFindsFirstAndLastLogicalCharacters() {
        StrBuilder builder = new StrBuilder(1);
        builder.append('a').append("middle").append('z');

        assertTrue(builder.contains('a'));
        assertTrue(builder.contains('z'));
    }

    @Test
    public void testContainsFindsAppendedNullCharacterWhenItIsLogicalContent() {
        StrBuilder builder = new StrBuilder();
        builder.append("a").append('\0').append("b");

        assertTrue(builder.contains('\0'));
    }

    @Test
    public void testContainsDoesNotFindCharactersAfterClear() {
        StrBuilder builder = new StrBuilder();
        builder.append("retained");

        builder.clear();

        assertEquals(0, builder.length());
        assertFalse(builder.contains('r'));
    }

    @Test
    public void testContainsDoesNotFindCharactersBeyondTruncatedLength() {
        StrBuilder builder = new StrBuilder();
        builder.append("abc");

        builder.setLength(2);

        assertEquals("ab", builder.toString());
        assertFalse(builder.contains('c'));
    }
}
