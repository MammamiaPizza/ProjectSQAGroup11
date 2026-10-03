package org.joda.time.format;

import static org.junit.Assert.assertEquals;

import org.joda.time.Period;
import org.junit.Test;

public class PeriodFormatterBuilderNegativeMillisTest {

    @Test
    public void standardFormatterRetainsSignForNegativeSubSecondPeriod() {
        assertEquals("PT-0.008S", ISOPeriodFormat.standard().print(Period.millis(-8)));
    }

    @Test
    public void standardFormatterFormatsPositiveSubSecondPeriod() {
        assertEquals("PT0.008S", ISOPeriodFormat.standard().print(Period.millis(8)));
    }

    @Test
    public void standardFormatterFormatsZeroMillisAsZeroSeconds() {
        assertEquals("PT0S", ISOPeriodFormat.standard().print(Period.millis(0)));
    }

    @Test
    public void standardFormatterRetainsSignWithWholeAndFractionalNegativeSeconds() {
        assertEquals("PT-1.008S", ISOPeriodFormat.standard().print(Period.millis(-1008)));
    }

    @Test
    public void mandatoryMillisFormatterRetainsSignForNegativeSubSecondPeriod() {
        PeriodFormatter formatter = new PeriodFormatterBuilder()
                .appendLiteral("PT")
                .appendSecondsWithMillis()
                .appendSuffix("S")
                .toFormatter();

        assertEquals("PT-0.008S", formatter.print(Period.millis(-8)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void appendLiteralRejectsNullText() {
        new PeriodFormatterBuilder().appendLiteral(null);
    }
}