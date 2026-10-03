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