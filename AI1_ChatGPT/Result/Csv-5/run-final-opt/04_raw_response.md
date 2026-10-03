@org.junit.Test
public void printNullUsesEmptyFieldAndKeepsDelimiterState() throws java.io.IOException {
    final java.lang.StringBuilder output = new java.lang.StringBuilder();
    final org.apache.commons.csv.CSVPrinter printer =
            new org.apache.commons.csv.CSVPrinter(output, org.apache.commons.csv.CSVFormat.DEFAULT);

    printer.print(null);
    printer.print("value");

    org.junit.Assert.assertEquals(",value", output.toString());
}

@org.junit.Test
public void printWithoutQuoteOrEscapeAppendsValueUnchanged() throws java.io.IOException {
    final java.lang.StringBuilder output = new java.lang.StringBuilder();
    final org.apache.commons.csv.CSVPrinter printer = new org.apache.commons.csv.CSVPrinter(output,
            org.apache.commons.csv.CSVFormat.DEFAULT.withQuote(null));

    printer.print("a,b\n");

    org.junit.Assert.assertEquals("a,b\n", output.toString());
}

@org.junit.Test
public void printWithEscapeEscapesDelimiterNewlineAndEscapeCharacter() throws java.io.IOException {
    final java.lang.StringBuilder output = new java.lang.StringBuilder();
    final org.apache.commons.csv.CSVPrinter printer = new org.apache.commons.csv.CSVPrinter(output,
            org.apache.commons.csv.CSVFormat.DEFAULT.withQuote(null).withEscape('\\'));

    printer.print("a,b\nc\\d");

    org.junit.Assert.assertEquals("a\\,b\\nc\\\\d", output.toString());
}

@org.junit.Test
public void flushAndCloseDelegateToAppendableWhenSupported() throws java.io.IOException {
    class TrackingAppendable implements java.lang.Appendable, java.io.Flushable, java.io.Closeable {
        private boolean flushed;
        private boolean closed;

        @Override
        public TrackingAppendable append(final java.lang.CharSequence value) {
            return this;
        }

        @Override
        public TrackingAppendable append(final java.lang.CharSequence value, final int start, final int end) {
            return this;
        }

        @Override
        public TrackingAppendable append(final char value) {
            return this;
        }

        @Override
        public void flush() {
            flushed = true;
        }

        @Override
        public void close() {
            closed = true;
        }
    }

    final TrackingAppendable output = new TrackingAppendable();
    final org.apache.commons.csv.CSVPrinter printer =
            new org.apache.commons.csv.CSVPrinter(output, org.apache.commons.csv.CSVFormat.DEFAULT);

    printer.flush();
    printer.close();

    org.junit.Assert.assertTrue(output.flushed);
    org.junit.Assert.assertTrue(output.closed);
}