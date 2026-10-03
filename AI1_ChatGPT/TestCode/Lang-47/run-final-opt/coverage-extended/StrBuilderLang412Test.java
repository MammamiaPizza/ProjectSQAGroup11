package org.apache.commons.lang.text;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class StrBuilderLang412Test {

    @Test
    public void testAppendFixedWidthPadLeftNullWithDefaultNullTextPadsEntireWidth() {
        StrBuilder builder = new StrBuilder();

        StrBuilder result = builder.appendFixedWidthPadLeft((Object) null, 4, '*');

        assertSame(builder, result);
        assertEquals("****", builder.toString());
        assertEquals(4, builder.length());
    }

    @Test
    public void testAppendFixedWidthPadRightNullWithDefaultNullTextPadsEntireWidth() {
        StrBuilder builder = new StrBuilder();

        StrBuilder result = builder.appendFixedWidthPadRight((Object) null, 4, '*');

        assertSame(builder, result);
        assertEquals("****", builder.toString());
        assertEquals(4, builder.length());
    }

    @Test
    public void testAppendFixedWidthPadLeftNullWithShortConfiguredNullText() {
        StrBuilder builder = new StrBuilder().setNullText("NA");

        builder.appendFixedWidthPadLeft((Object) null, 5, '_');

        assertEquals("___NA", builder.toString());
    }

    @Test
    public void testAppendFixedWidthPadRightNullWithShortConfiguredNullText() {
        StrBuilder builder = new StrBuilder().setNullText("NA");

        builder.appendFixedWidthPadRight((Object) null, 5, '_');

        assertEquals("NA___", builder.toString());
    }

    @Test
    public void testAppendFixedWidthPaddingNullWithEqualConfiguredNullText() {
        StrBuilder left = new StrBuilder().setNullText("NULL");
        StrBuilder right = new StrBuilder().setNullText("NULL");

        left.appendFixedWidthPadLeft((Object) null, 4, '_');
        right.appendFixedWidthPadRight((Object) null, 4, '_');

        assertEquals("NULL", left.toString());
        assertEquals("NULL", right.toString());
    }

    @Test
    public void testAppendFixedWidthPaddingNullWithLongConfiguredNullTextTruncatesByDirection() {
        StrBuilder left = new StrBuilder().setNullText("ABCDEFG");
        StrBuilder right = new StrBuilder().setNullText("ABCDEFG");

        left.appendFixedWidthPadLeft((Object) null, 3, '_');
        right.appendFixedWidthPadRight((Object) null, 3, '_');

        assertEquals("EFG", left.toString());
        assertEquals("ABC", right.toString());
    }

    @Test
    public void testAppendFixedWidthPaddingNullWithZeroWidthDoesNotChangeContent() {
        StrBuilder builder = new StrBuilder("prefix");

        builder.appendFixedWidthPadLeft((Object) null, 0, '_');
        builder.appendFixedWidthPadRight((Object) null, 0, '_');

        assertEquals("prefix", builder.toString());
        assertEquals(6, builder.length());
    }

    @Test
    public void testAppendFixedWidthPaddingWithEmptyConfiguredNullTextPadsEntireWidth() {
        StrBuilder left = new StrBuilder().setNullText("");
        StrBuilder right = new StrBuilder().setNullText("");

        left.appendFixedWidthPadLeft((Object) null, 3, '.');
        right.appendFixedWidthPadRight((Object) null, 3, '.');

        assertEquals("...", left.toString());
        assertEquals("...", right.toString());
    }

@Test
public void testEmptyConstructorsCreateUsableBuilders() {
    StrBuilder zeroCapacity = new StrBuilder(0);
    assertEquals(0, zeroCapacity.length());
    assertSame(zeroCapacity, zeroCapacity.append('x'));
    assertEquals("x", zeroCapacity.toString());

    StrBuilder nullString = new StrBuilder((String) null);
    assertEquals(0, nullString.length());
    assertSame(nullString, nullString.append('y'));
    assertEquals("y", nullString.toString());
}

@Test
public void testAppendCharacterAndNumericValues() {
    StrBuilder builder = new StrBuilder();

    assertSame(builder, builder.append('a'));
    assertSame(builder, builder.append(7));
    assertSame(builder, builder.append(8L));
    assertSame(builder, builder.append(1.5f));
    assertSame(builder, builder.append(2.5d));

    assertEquals("a781.52.5", builder.toString());
}

@Test
public void testAppendObjectAndStringRangesWithConfiguredNullText() {
    StrBuilder builder = new StrBuilder();
    builder.setNullText("<null>");

    assertSame(builder, builder.append((Object) null));
    assertSame(builder, builder.append((Object) "X"));
    assertSame(builder, builder.append("abcdef", 2, 3));
    assertSame(builder, builder.append("ignored", 1, 0));
    assertSame(builder, builder.append((String) null));
    assertSame(builder, builder.append((String) null, 0, 0));

    assertEquals("<null>Xcde<null><null>", builder.toString());
}

@Test(expected = StringIndexOutOfBoundsException.class)
public void testAppendStringRangeRejectsNegativeStartIndex() {
    new StrBuilder().append("abc", -1, 1);
}
}
