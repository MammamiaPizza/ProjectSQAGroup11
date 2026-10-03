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

@org.junit.Test
public void parsesSmallestIntegerSecondsValue() {
    org.joda.time.format.PeriodFormatter formatter =
            new org.joda.time.format.PeriodFormatterBuilder()
                    .appendLiteral("PT")
                    .appendSeconds()
                    .appendLiteral("S")
                    .toFormatter();

    org.joda.time.Period period = formatter.parsePeriod("PT-2147483648S");

    org.junit.Assert.assertEquals(Integer.MIN_VALUE, period.getSeconds());
}

@org.junit.Test
public void appendsFormatterAsPartOfCompositeFormatter() {
    org.joda.time.format.PeriodFormatter secondsFormatter =
            new org.joda.time.format.PeriodFormatterBuilder()
                    .appendSeconds()
                    .appendLiteral("S")
                    .toFormatter();
    org.joda.time.format.PeriodFormatter formatter =
            new org.joda.time.format.PeriodFormatterBuilder()
                    .appendLiteral("P")
                    .append(secondsFormatter)
                    .toFormatter();

    org.joda.time.Period period = formatter.parsePeriod("P7S");

    org.junit.Assert.assertEquals(7, period.getSeconds());
    org.junit.Assert.assertEquals("P7S", formatter.print(period));
}

@org.junit.Test
public void parsesAndPrintsMultiplePeriodFieldsIncludingThreeDigitMillis() {
    org.joda.time.format.PeriodFormatter formatter =
            new org.joda.time.format.PeriodFormatterBuilder()
                    .appendMonths()
                    .appendLiteral(":")
                    .appendDays()
                    .appendLiteral(":")
                    .appendHours()
                    .appendLiteral(":")
                    .appendMinutes()
                    .appendLiteral(":")
                    .appendMillis3Digit()
                    .toFormatter();

    org.joda.time.Period period = formatter.parsePeriod("2:3:4:5:006");

    org.junit.Assert.assertEquals(2, period.getMonths());
    org.junit.Assert.assertEquals(3, period.getDays());
    org.junit.Assert.assertEquals(4, period.getHours());
    org.junit.Assert.assertEquals(5, period.getMinutes());
    org.junit.Assert.assertEquals(6, period.getMillis());
    org.junit.Assert.assertEquals("2:3:4:5:006", formatter.print(period));
}

@org.junit.Test(expected = IllegalArgumentException.class)
public void rejectsAppendWhenNeitherPrinterNorParserIsSupplied() {
    new org.joda.time.format.PeriodFormatterBuilder().append(
            (org.joda.time.format.PeriodPrinter) null,
            (org.joda.time.format.PeriodParser) null);
}
}
