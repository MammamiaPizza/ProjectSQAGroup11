@org.junit.Test(expected = IllegalArgumentException.class)
public void testAppendNullFormatterIsRejected() {
    new org.joda.time.format.DateTimeFormatterBuilder()
        .append((org.joda.time.format.DateTimeFormatter) null);
}

@org.junit.Test(expected = IllegalArgumentException.class)
public void testAppendNullPrinterIsRejected() {
    new org.joda.time.format.DateTimeFormatterBuilder()
        .append((org.joda.time.format.DateTimePrinter) null);
}

@org.junit.Test(expected = IllegalArgumentException.class)
public void testAppendNullParserIsRejected() {
    new org.joda.time.format.DateTimeFormatterBuilder()
        .append((org.joda.time.format.DateTimeParser) null);
}

@org.junit.Test(expected = IllegalArgumentException.class)
public void testAppendNullParserArrayIsRejected() {
    new org.joda.time.format.DateTimeFormatterBuilder()
        .append((org.joda.time.format.DateTimePrinter) null,
                (org.joda.time.format.DateTimeParser[]) null);
}