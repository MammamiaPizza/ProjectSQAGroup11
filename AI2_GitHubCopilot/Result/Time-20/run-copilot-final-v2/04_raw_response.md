@org.junit.Test
    public void testAppendFormatterWithNullThrows() {
        DateTimeFormatterBuilder b = new DateTimeFormatterBuilder();
        try {
            b.append((DateTimeFormatter) null);
            org.junit.Assert.fail("Expected IllegalArgumentException for null formatter");
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }

 @org.junit.Test
 public void testAppendPrinterWithNullThrows() {
     DateTimeFormatterBuilder b = new DateTimeFormatterBuilder();
     try {
         b.append((DateTimePrinter) null);
         org.junit.Assert.fail("Expected IllegalArgumentException for null printer");
     } catch (IllegalArgumentException expected) {
         // expected
     }
 }

 @org.junit.Test
 public void testAppendParserWithNullThrows() {
     DateTimeFormatterBuilder b = new DateTimeFormatterBuilder();
     try {
         b.append((DateTimeParser) null);
         org.junit.Assert.fail("Expected IllegalArgumentException for null parser");
     } catch (IllegalArgumentException expected) {
         // expected
     }
 }

 @org.junit.Test
 public void testAppendWithNullParserVariantsThrows() {
     DateTimeFormatterBuilder b = new DateTimeFormatterBuilder();
     DateTimePrinter printer = new DateTimeFormatterBuilder().appendLiteral('x').toPrinter();

     try {
         b.append((DateTimePrinter) null, (DateTimeParser) null);
         org.junit.Assert.fail("Expected IllegalArgumentException for null printer");
     } catch (IllegalArgumentException expected) {
         // expected
     }

     try {
         b.append(printer, (DateTimeParser) null);
         org.junit.Assert.fail("Expected IllegalArgumentException for null parser");
     } catch (IllegalArgumentException expected) {
         // expected
     }

     try {
         b.append(printer, (DateTimeParser[]) null);
         org.junit.Assert.fail("Expected IllegalArgumentException for null parser array");
     } catch (IllegalArgumentException expected) {
         // expected
     }

     try {
         b.append(printer, new DateTimeParser[]{null});
         org.junit.Assert.fail("Expected IllegalArgumentException for null parser element");
     } catch (IllegalArgumentException expected) {
         // expected
     }
 }