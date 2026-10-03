package org.apache.commons.math.fraction;

import java.text.ParsePosition;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ProperFractionFormatGeneratedTest {

    @Test
    public void parsesPositiveProperFractionAndAdvancesPosition() {
        ProperFractionFormat format = new ProperFractionFormat();
        ParsePosition position = new ParsePosition(0);

        Fraction result = format.parse("3 1 / 2 trailing", position);

        assertEquals(new Fraction(7, 2), result);
        assertEquals(7, position.getIndex());
    }

    @Test
    public void parsesNegativeWholeProperFraction() {
        ProperFractionFormat format = new ProperFractionFormat();
        ParsePosition position = new ParsePosition(0);

        Fraction result = format.parse("-3 1 / 2", position);

        assertEquals(new Fraction(-7, 2), result);
        assertEquals(8, position.getIndex());
    }

    @Test
    public void parsesImproperFractionUsingInheritedSyntax() {
        ProperFractionFormat format = new ProperFractionFormat();
        ParsePosition position = new ParsePosition(0);

        Fraction result = format.parse("7 / 3", position);

        assertEquals(new Fraction(7, 3), result);
        assertEquals(5, position.getIndex());
    }

    @Test
    public void rejectsMinusBeforeNumerator() {
        ProperFractionFormat format = new ProperFractionFormat();
        ParsePosition position = new ParsePosition(0);

        Fraction result = format.parse("3 -1 / 2", position);

        assertNull(result);
        assertEquals(0, position.getIndex());
    }

    @Test
    public void rejectsMinusBeforeNumeratorWhenWholeIsNegative() {
        ProperFractionFormat format = new ProperFractionFormat();
        ParsePosition position = new ParsePosition(0);

        Fraction result = format.parse("-3 -1 / 2", position);

        assertNull(result);
        assertEquals(0, position.getIndex());
    }

    @Test
    public void rejectsMinusBeforeDenominator() {
        ProperFractionFormat format = new ProperFractionFormat();
        ParsePosition position = new ParsePosition(0);

        Fraction result = format.parse("3 1 / -2", position);

        assertNull(result);
        assertEquals(0, position.getIndex());
    }

    @Test
    public void rejectsMinusBeforeDenominatorWithNegativeWhole() {
        ProperFractionFormat format = new ProperFractionFormat();
        ParsePosition position = new ParsePosition(0);

        Fraction result = format.parse("-3 1 / -2", position);

        assertNull(result);
        assertEquals(0, position.getIndex());
    }

    @Test
    public void rejectsProperFractionWithMissingDenominator() {
        ProperFractionFormat format = new ProperFractionFormat();
        ParsePosition position = new ParsePosition(0);

        Fraction result = format.parse("3 1 /", position);

        assertNull(result);
        assertEquals(0, position.getIndex());
    }
}
