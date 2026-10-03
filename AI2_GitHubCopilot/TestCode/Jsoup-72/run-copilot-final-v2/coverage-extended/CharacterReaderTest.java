package org.jsoup.parser;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;

 import org.junit.Test;

 public class CharacterReaderTest {

     @Test
     public void consumeToDelimiterAtLastPosition() {
         CharacterReader reader = new CharacterReader("abc");
         String consumed = reader.consumeTo('c');
         assertEquals("ab", consumed);
         assertEquals('c', reader.current());
         assertEquals(2, reader.pos());
     }

     @Test
     public void consumeToDelimiterAbsentWithRemaining() {
         CharacterReader reader = new CharacterReader("abc");
         String consumed = reader.consumeTo('x');
         assertEquals("abc", consumed);
         assertTrue(reader.isEmpty());
         assertEquals(CharacterReader.EOF, reader.current());
     }

     @Test
     public void consumeToDelimiterAbsentAtEndAfterFullConsumption() {
         CharacterReader reader = new CharacterReader("abc");
         reader.consumeTo('d'); // consume all
         // reader is at end, delimiter not found, expecting empty string without exception
         String consumed = reader.consumeTo('x');
         assertEquals("", consumed);
         assertTrue(reader.isEmpty());
     }

     @Test
     public void consumeToEmptyInput() {
         CharacterReader reader = new CharacterReader("");
         String consumed = reader.consumeTo('x');
         assertEquals("", consumed);
         assertTrue(reader.isEmpty());
     }

     @Test
     public void consumeToAnyDelimiterPresentAtEnd() {
         CharacterReader reader = new CharacterReader("abcz");
         String consumed = reader.consumeToAny('z', 'w');
         assertEquals("abc", consumed);
         assertEquals('z', reader.current());
     }

     @Test
     public void consumeToAnyDelimiterAbsentBeforeEnd() {
         CharacterReader reader = new CharacterReader("abc");
         String consumed = reader.consumeToAny('x', 'y');
         assertEquals("abc", consumed);
         assertTrue(reader.isEmpty());
     }

     @Test
     public void consumeToAnyDelimiterAbsentAtEndAfterFullConsumption() {
         CharacterReader reader = new CharacterReader("abc");
         reader.consumeTo('d'); // consume all
         String consumed = reader.consumeToAny('x', 'y');
         assertEquals("", consumed);
         assertTrue(reader.isEmpty());
     }

     @Test
     public void consumeToAnyMultipleDelimitersPresent() {
         CharacterReader reader = new CharacterReader("abcdef");
         String consumed = reader.consumeToAny('c', 'e');
         assertEquals("ab", consumed);
         assertEquals('c', reader.current());
     }

     @Test
     public void consumeToAnySortedDelimiterPresent() {
         CharacterReader reader = new CharacterReader("abcz");
         String consumed = reader.consumeToAnySorted('a', 'w', 'z');
         assertEquals("", consumed);
         assertEquals('a', reader.current());
     }

     @Test
     public void consumeToAnySortedDelimiterAbsent() {
         CharacterReader reader = new CharacterReader("abc");
         String consumed = reader.consumeToAnySorted('x', 'y');
         assertEquals("abc", consumed);
         assertTrue(reader.isEmpty());
     }

     @Test
     public void consumeToWithLongInputDoesNotThrow() {
         StringBuilder sb = new StringBuilder();
         for (int i = 0; i < 40000; i++) {
             sb.append('a');
         }
         CharacterReader reader = new CharacterReader(sb.toString());
         // advance to near the end, skipping many characters
         for (int i = 0; i < 39000; i++) {
             reader.advance();
         }
         // consumeTo with missing delimiter after heavy buffer shifting should not throw
         String consumed = reader.consumeTo('z');
         assertFalse(consumed == null);
         assertTrue(reader.isEmpty());
     }

     @Test
     public void consumeAfterConsumeToEndThenConsumeToDoesNotThrow() {
         CharacterReader reader = new CharacterReader("data");
         reader.consumeTo('x'); // consume all
         reader.consume();      // try to read past end (should return EOF and advance position)
         String consumed = reader.consumeTo('z');
         assertEquals("", consumed);
         assertTrue(reader.isEmpty());
     }

@Test public void constructorWithFailingReaderThrows() {
        boolean thrown = false;
        try {
            CharacterReader reader = new CharacterReader(new java.io.Reader() {
                @Override public int read(char[] cbuf, int off, int len) throws java.io.IOException
{
                    throw new java.io.IOException("boom");
                }
                @Override public void close() throws java.io.IOException {
                }
            });
            reader.consumeData();
        } catch (RuntimeException expected) {
            thrown = true;
        }
        assertTrue(thrown);
    }

 @Test public void consumeDataStopsAtSpecialCharacters() {
     CharacterReader reader = new CharacterReader("abc<def");
     assertEquals("abc", reader.consumeData());
     assertEquals('<', reader.current());

     CharacterReader amp = new CharacterReader("abc&def");
     assertEquals("abc", amp.consumeData());
     assertEquals('&', amp.current());

     CharacterReader nul = new CharacterReader("abc\u0000def");
     assertEquals("abc", nul.consumeData());
     assertEquals('\u0000', nul.current());
 }

 @Test public void consumeDataReturnsEmptyWhenStartingAtSpecialCharacter() {
     CharacterReader reader = new CharacterReader("&rest");
     assertEquals("", reader.consumeData());
     assertEquals('&', reader.current());
 }

 @Test public void consumeDigitSequenceStopsAtNonDigitAndReturnsEmptyWithoutDigits() {
     CharacterReader reader = new CharacterReader("12345z");
     assertEquals("12345", reader.consumeDigitSequence());
     assertEquals('z', reader.current());

     CharacterReader noDigits = new CharacterReader("abc");
     assertEquals("", noDigits.consumeDigitSequence());
     assertEquals('a', noDigits.current());
 }
}
