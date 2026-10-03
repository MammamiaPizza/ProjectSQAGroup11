package org.joda.time.format;

import static org.junit.Assert.assertEquals;

import org.joda.time.Period;
import org.junit.Test;

public class PeriodFormatterBuilderBug64Test {

    private PeriodFormatter secondsFormatter() {
        return new PeriodFormatterBuilder()
                .appendLiteral("PT")
                .appendSeconds()
                .appendSuffix("S")
                .toFormatter();
    }

    @Test
    public void parsesTenDigitSecondsValueWithSuffix() {
        Period period = secondsFormatter().parsePeriod("PT1003199059S");

        assertEquals(1003199059, period.getSeconds());
    }

    @Test
    public void printsAndParsesOrdinarySecondsValue() {
        PeriodFormatter formatter = secondsFormatter();

        Period period = formatter.parsePeriod("PT42S");

        assertEquals(42, period.getSeconds());
        assertEquals("PT42S", formatter.print(period));
    }

    @Test
    public void parsesLargestIntegerSecondsValueWithTenDigits() {
        Period period = secondsFormatter().parsePeriod("PT2147483647S");

        assertEquals(Integer.MAX_VALUE, period.getSeconds());
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsMoreThanDefaultMaximumOfTenParsedDigits() {
        secondsFormatter().parsePeriod("PT10031990590S");
    }

    @Test
    public void honorsConfiguredMaximumParsedDigits() {
        PeriodFormatter formatter = new PeriodFormatterBuilder()
                .appendLiteral("PT")
                .maximumParsedDigits(3)
                .appendSeconds()
                .appendSuffix("S")
                .toFormatter();

        Period period = formatter.parsePeriod("PT123S");

        assertEquals(123, period.getSeconds());
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsValueExceedingConfiguredMaximumParsedDigits() {
        PeriodFormatter formatter = new PeriodFormatterBuilder()
                .appendLiteral("PT")
                .maximumParsedDigits(3)
                .appendSeconds()
                .appendSuffix("S")
                .toFormatter();

        formatter.parsePeriod("PT1234S");
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsSignedSecondsWhenConfiguredToRejectSigns() {
        PeriodFormatter formatter = new PeriodFormatterBuilder()
                .appendLiteral("PT")
                .rejectSignedValues(true)
                .appendSeconds()
                .appendSuffix("S")
                .toFormatter();

        formatter.parsePeriod("PT-1S");
    }
}
