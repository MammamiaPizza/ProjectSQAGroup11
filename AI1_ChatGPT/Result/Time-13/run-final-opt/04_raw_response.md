@org.junit.Test(expected = IllegalArgumentException.class)
public void appendRejectsNullFormatter() {
    new org.joda.time.format.PeriodFormatterBuilder()
            .append((org.joda.time.format.PeriodFormatter) null);
}

@org.junit.Test(expected = IllegalArgumentException.class)
public void appendRejectsMissingPrinterAndParser() {
    new org.joda.time.format.PeriodFormatterBuilder()
            .append((org.joda.time.format.PeriodPrinter) null,
                    (org.joda.time.format.PeriodParser) null);
}

@org.junit.Test
public void appendFormatterClearsPendingPrefix() {
    org.joda.time.format.PeriodFormatter nested =
            new org.joda.time.format.PeriodFormatterBuilder()
                    .appendLiteral("nested")
                    .toFormatter();

    org.joda.time.format.PeriodFormatter formatter =
            new org.joda.time.format.PeriodFormatterBuilder()
                    .appendPrefix("unused")
                    .append(nested)
                    .toFormatter();

    org.junit.Assert.assertEquals("nested", formatter.print(org.joda.time.Period.ZERO));
}

@org.junit.Test
public void millisFieldsRetainNegativeSignAndThreeDigitPadding() {
    org.junit.Assert.assertEquals(
            "-8",
            new org.joda.time.format.PeriodFormatterBuilder()
                    .appendMillis()
                    .toFormatter()
                    .print(org.joda.time.Period.millis(-8)));
    org.junit.Assert.assertEquals(
            "-008",
            new org.joda.time.format.PeriodFormatterBuilder()
                    .appendMillis3Digit()
                    .toFormatter()
                    .print(org.joda.time.Period.millis(-8)));
}