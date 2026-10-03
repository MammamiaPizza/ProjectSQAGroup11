@org.junit.Test
public void printTreatsNullAsAnEmptyFieldUsingTheDefaultFormat() throws Exception {
    final java.lang.StringBuilder out = new java.lang.StringBuilder();
    final org.apache.commons.csv.CSVPrinter printer =
            new org.apache.commons.csv.CSVPrinter(out, org.apache.commons.csv.CSVFormat.DEFAULT);

    printer.print("first");
    printer.print(null);
    printer.print("last");
    printer.println();

    org.junit.Assert.assertEquals("first,,last"
            + org.apache.commons.csv.CSVFormat.DEFAULT.getRecordSeparator(), out.toString());
}

@org.junit.Test
public void printlnWritesAnEmptyRecordAndResetsTheFieldState() throws Exception {
    final java.lang.StringBuilder out = new java.lang.StringBuilder();
    final org.apache.commons.csv.CSVPrinter printer =
            new org.apache.commons.csv.CSVPrinter(out, org.apache.commons.csv.CSVFormat.DEFAULT);

    printer.println();
    printer.print("value");
    printer.println();

    org.junit.Assert.assertEquals(org.apache.commons.csv.CSVFormat.DEFAULT.getRecordSeparator()
            + "value" + org.apache.commons.csv.CSVFormat.DEFAULT.getRecordSeparator(), out.toString());
}

@org.junit.Test
public void flushAndCloseDelegateWhenTheAppendableSupportsThem() throws Exception {
    class TrackingAppendable implements java.lang.Appendable, java.io.Flushable, java.io.Closeable {
        private final java.lang.StringBuilder delegate = new java.lang.StringBuilder();
        private boolean flushed;
        private boolean closed;

        @Override
        public java.lang.Appendable append(final java.lang.CharSequence csq) {
            delegate.append(csq);
            return this;
        }

        @Override
        public java.lang.Appendable append(final java.lang.CharSequence csq, final int start, final int end) {
            delegate.append(csq, start, end);
            return this;
        }

        @Override
        public java.lang.Appendable append(final char c) {
            delegate.append(c);
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

    final TrackingAppendable out = new TrackingAppendable();
    final org.apache.commons.csv.CSVPrinter printer =
            new org.apache.commons.csv.CSVPrinter(out, org.apache.commons.csv.CSVFormat.DEFAULT);

    printer.flush();
    printer.close();

    org.junit.Assert.assertTrue(out.flushed);
    org.junit.Assert.assertTrue(out.closed);
}