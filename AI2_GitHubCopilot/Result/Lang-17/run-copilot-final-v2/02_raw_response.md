package org.apache.commons.lang3.text.translate;

 import static org.junit.Assert.*;

 import java.io.IOException;
 import java.io.StringWriter;
 import java.io.Writer;

 import org.junit.Test;

 /**
  * Test class for {@link CharSequenceTranslator}.
  * Focuses on supplementary character handling, edge cases, and contract verification.
  */
 public class CharSequenceTranslatorTest {

     // --- Helper translators ---------------------------------------------------

     /** Identity translator: writes the codepoint at the given index and consumes 1 codepoint. */
     private static final CharSequenceTranslator IDENTITY = new CharSequenceTranslator() {
         @Override
         public int translate(CharSequence input, int index, Writer out) throws IOException {
             int codepoint = Character.codePointAt(input, index);
             out.write(Character.toChars(codepoint));
             return 1;
         }
     };

     /** A translator that always returns 0 consumed – exercising the default write branch. */
     private static final CharSequenceTranslator ZERO_CONSUME = new CharSequenceTranslator() {
         @Override
         public int translate(CharSequence input, int index, Writer out) throws IOException {
             return 0;
         }
     };

     // --- null / empty ---------------------------------------------------------

     @Test
     public void testTranslateNullReturnsNull() {
         assertNull(IDENTITY.translate(null));
     }

     @Test
     public void testTranslateEmptyReturnsEmpty() {
         assertEquals("", IDENTITY.translate(""));
     }

     @Test
     public void testTranslateNullWriterDoesNothing() throws IOException {
         StringWriter out = new StringWriter();
         IDENTITY.translate(null, out);
         assertEquals("", out.toString());
     }

     // --- basic BMP ------------------------------------------------------------

     @Test
     public void testIdentityBMP() {
         assertEquals("Hello World!", IDENTITY.translate("Hello World!"));
     }

     @Test
     public void testZeroConsumeBMP() {
         assertEquals("A", ZERO_CONSUME.translate("A"));
     }

     // --- supplementary characters ---------------------------------------------

     @Test
     public void testIdentitySingleSupplementary() {
         String input = "𠮷"; // U+20BB7
         assertEquals(input, IDENTITY.translate(input));
     }

     @Test
     public void testIdentityMultipleSupplementary() {
         String input = "𠮷𠮷";
         assertEquals(input, IDENTITY.translate(input));
     }

     @Test
     public void testIdentitySupplementarySurroundedByBMP() {
         // Exact trigger for LANG-720
         assertEquals("𠮷A", IDENTITY.translate("𠮷A"));
         assertEquals("A𠮷B", IDENTITY.translate("A𠮷B"));
     }

     @Test
     public void testIdentitySupplementaryNextToBoundary() {
         String input = "\uFFFF𠮷"; // U+FFFF is the last BMP codepoint
         assertEquals(input, IDENTITY.translate(input));
     }

     @Test
     public void testZeroConsumeSupplementary() {
         String input = "𠮷";
         assertEquals(input, ZERO_CONSUME.translate(input));
     }

     // --- writer contract ------------------------------------------------------

     @Test
     public void testTranslateNullWriterThrows() throws IOException {
         try {
             IDENTITY.translate("test", null);
             fail("Expected IllegalArgumentException");
         } catch (IllegalArgumentException expected) {
             // contract: out must not be null
         }
     }

     @Test
     public void testTranslateWriterOutput() throws IOException {
         StringWriter out = new StringWriter();
         IDENTITY.translate("Hello", out);
         assertEquals("Hello", out.toString());
     }

     @Test
     public void testTranslateWriterPropagatesIOException() {
         Writer failingWriter = new Writer() {
             @Override
             public void write(char[] cbuf, int off, int len) throws IOException {
                 throw new IOException("fail");
             }
             @Override public void flush() throws IOException {}
             @Override public void close() throws IOException {}
         };
         try {
             IDENTITY.translate("test", failingWriter);
             fail("Expected IOException");
         } catch (IOException expected) {
             assertEquals("fail", expected.getMessage());
         }
     }
 }