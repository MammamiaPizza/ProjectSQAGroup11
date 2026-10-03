package org.apache.commons.csv;

 import org.junit.Test;

 import static org.junit.Assert.*;

 /**
  * Tests duplicate header detection in CSVFormat.withHeader(String...).
  * Bug CSV-114: withHeader should throw IllegalArgumentException for duplicate header names.
  */
 public class CSVFormatTest {

     private static CSVFormat format() {
         return CSVFormat.newFormat(',');
     }

     @Test(expected = IllegalArgumentException.class)
     public void testDuplicateHeaderElements() {
         format().withHeader("a", "b", "a");
     }

     @Test(expected = IllegalArgumentException.class)
     public void testDuplicateHeaderAtStart() {
         format().withHeader("x", "x", "y");
     }

     @Test(expected = IllegalArgumentException.class)
     public void testDuplicateHeaderInMiddle() {
         format().withHeader("a", "b", "b", "c");
     }

     @Test(expected = IllegalArgumentException.class)
     public void testDuplicateHeaderAtEnd() {
         format().withHeader("p", "q", "p");
     }

     @Test(expected = IllegalArgumentException.class)
     public void testAllDuplicateHeaderElements() {
         format().withHeader("dup", "dup", "dup");
     }

     @Test(expected = IllegalArgumentException.class)
     public void testSingleRepeatedHeaderElement() {
         format().withHeader("only", "only");
     }

     @Test
     public void testUniqueHeaderElements() {
         CSVFormat fmt = format().withHeader("id", "name", "value");
         assertNotNull(fmt);
         String[] h = fmt.getHeader();
         assertEquals(3, h.length);
         assertEquals("id", h[0]);
         assertEquals("name", h[1]);
         assertEquals("value", h[2]);
     }

     @Test
     public void testCaseSensitiveUniqueHeaders() {
         CSVFormat fmt = format().withHeader("Header", "header", "HEADER");
         assertArrayEquals(new String[]{"Header", "header", "HEADER"}, fmt.getHeader());
     }

     @Test
     public void testSingleHeaderElement() {
         CSVFormat fmt = format().withHeader("single");
         assertArrayEquals(new String[]{"single"}, fmt.getHeader());
     }

     @Test
     public void testEmptyHeaderVarargs() {
         CSVFormat fmt = format().withHeader();
         assertNotNull(fmt.getHeader());
         assertEquals(0, fmt.getHeader().length);
     }

     @Test
     public void testNullHeaderArray() {
         CSVFormat fmt = format().withHeader("col1", "col2");
         assertNotNull(fmt.getHeader());
         CSVFormat fmtNull = fmt.withHeader((String[]) null);
         assertNull(fmtNull.getHeader());
     }

     @Test
     public void testDuplicateWithLargeHeaderSet() {
         String[] headers = new String[50];
         for (int i = 0; i < 50; i++) {
             headers[i] = "col" + i;
         }
         CSVFormat fmt = format().withHeader(headers);
         assertEquals(50, fmt.getHeader().length);

         headers[49] = "col0";
         try {
             format().withHeader(headers);
             fail("Expected IllegalArgumentException for duplicate in large header");
         } catch (IllegalArgumentException expected) {
             // expected
         }
     }

@Test
    public void testEqualsWithSelf() {
        CSVFormat format = CSVFormat.DEFAULT.withIgnoreEmptyLines(true);
        assertTrue(format.equals(format));
    }

 @Test
 public void testEqualsWithDifferentQuoteChar() {
     CSVFormat format1 = CSVFormat.DEFAULT.withQuoteChar('"');
     CSVFormat format2 = CSVFormat.DEFAULT.withQuoteChar('\'');
     assertFalse(format1.equals(format2));
 }

 @Test
 public void testEqualsWithDifferentCommentStart() {
     CSVFormat format1 = CSVFormat.DEFAULT.withCommentStart('#');
     CSVFormat format2 = CSVFormat.DEFAULT;
     assertFalse(format1.equals(format2));
 }

 @Test(expected = IllegalArgumentException.class)
 public void testNewFormatWithLineBreakDelimiter() {
     CSVFormat.newFormat('\n');
 }
}
