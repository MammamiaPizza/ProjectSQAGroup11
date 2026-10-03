package org.apache.commons.lang3.text.translate;

 import static org.junit.Assert.*;

 import java.io.IOException;
 import java.io.StringWriter;
 import java.io.Writer;
 import java.util.Locale;

 import org.junit.Test;

 /**
  * Tests for {@link CharSequenceTranslator} focusing on the LANG-857
  * StringIndexOutOfBoundsException when a supplementary character ends the
  * input in the buggy "consumed&gt;0" loop.
  */
 public class CharSequenceTranslatorTest {

     /**
      * A minimal concrete translator that:
      * - returns the number of chars consumed (1 for BMP, 2 for supplementary)
      * - for BMP: writes the characters unchanged
      * - for supplementary: writes a hex escape of the form \U+XXXXXX
      */
     static final class SimpleTranslator extends CharSequenceTranslator {
         @Override
         public int translate(CharSequence input, int index, Writer out)
                 throws IOException {
             int codePoint = Character.codePointAt(input, index);
             int count = Character.charCount(codePoint);
             if (Character.isSupplementaryCodePoint(codePoint)) {
                 out.write("\\U+");
                 out.write(Integer.toHexString(codePoint)
                         .toUpperCase(Locale.ROOT));
                 return count;          // 2, triggers the buggy loop when last
             } else {
                 for (int i = 0; i < count; i++) {
                     out.write(input.charAt(index + i));
                 }
                 return count;          // 1
             }
         }
     }

     /**
      * A translator whose {@code translate} method always throws
      * IOException – used to verify the wrapping in {@code translate(CharSequence)}.
      */
     static final class ThrowingTranslator extends CharSequenceTranslator {
         @Override
         public int translate(CharSequence input, int index, Writer out)
                 throws IOException {
             throw new IOException("simulated");
         }
     }

     // -----------------------------------------------------------------
     // Writer-based translate(CharSequence,Writer) tests
     // -----------------------------------------------------------------

     @Test(expected = IllegalArgumentException.class)
     public void testTranslateWithNullWriter() throws IOException {
         new SimpleTranslator().translate("something", null);
     }

     @Test
     public void testTranslateWithNullInput() throws IOException {
         StringWriter writer = new StringWriter();
         new SimpleTranslator().translate((CharSequence) null, writer);
         assertEquals("", writer.toString());
     }

     @Test
     public void testTranslateEmptyInput() throws IOException {
         StringWriter writer = new StringWriter();
         new SimpleTranslator().translate("", writer);
         assertequals("", writer.toString());
     }

     @Test
     public void testTranslateBMPCharacters() throws IOException {
         StringWriter writer = new StringWriter();
         new SimpleTranslator().translate("abc", writer);
         assertEquals("abc", writer.toString());
     }

     // Reproducer for LANG-857: supplementary at the very end
     @Test
     public void testSurrogatePairAtEnd() throws IOException {
         String input = "a\uD83D\uDE00";          // 'a' + GRINNING FACE (U+1F600)
         StringWriter writer = new StringWriter();
         // Buggy version throws StringIndexOutOfBoundsException here.
         // After the fix the translation completes without error.
         new SimpleTranslator().translate(input, writer);
         assertEquals("a\\U+1F600", writer.toString());
     }

     @Test
     public void testSurrogatePairAtStart() throws IOException {
         String input = "\uD83D\uDE00b";
         StringWriter writer = new StringWriter();
         new SimpleTranslator().translate(input, writer);
         assertEquals("\\U+1F600b", writer.toString());
     }

     @Test
     public void testSurrogatePairInMiddle() throws IOException {
         String input = "a\uD83D\uDE00b";
         StringWriter writer = new StringWriter();
         new SimpleTranslator().translate(input, writer);
         assertEquals("a\\U+1F600b", writer.toString());
     }

     @Test
     public void testMultipleSurrogatePairs() throws IOException {
         // Two supplementary characters, the last one triggers the bug
         String input = "\uD83D\uDE00\uD83D\uDE00";
         StringWriter writer = new StringWriter();
         new SimpleTranslator().translate(input, writer);
         assertEquals("\\U+1F600\\U+1F600", writer.toString());
     }

     @Test
     public void testMixedContentWithPairAtEnd() throws IOException {
         String input = "ab\uD83D\uDE00cd\uD83D\uDE00";
         StringWriter writer = new StringWriter();
         new SimpleTranslator().translate(input, writer);
         assertEquals("ab\\U+1F600cd\\U+1F600", writer.toString());
     }

     /**
      * Unpaired surrogate: consumer returns 0 for the high surrogate,
      * so the fallback branch writes the lone surrogate char – no exception.
      */
     @Test
     public void testUnpairedHighSurrogateAtEnd() throws IOException {
         String input = "a\uD83D";               // lone high surrogate
         StringWriter writer = new StringWriter();
         new SimpleTranslator().translate(input, writer);
         assertEquals("a\uD83D", writer.toString());
     }

     // -----------------------------------------------------------------
     // String-based translate(CharSequence) tests
     // -----------------------------------------------------------------

     @Test
     public void testTranslateStringNullReturnsNull() {
         assertNull(new SimpleTranslator().translate((CharSequence) null));
     }

     @Test
     public void testTranslateStringWithSupplementary() {
         String input = "\uD83D\uDE00";
         String result = new SimpleTranslator().translate(input);
         assertEquals("\\U+1F600", result);
     }

     @Test(expected = RuntimeException.class)
     public void testTranslateStringWrapsIOException() {
         new ThrowingTranslator().translate("anything");
     }
  }```
