package org.jsoup.parser;

import static org.junit.Assert.*;
import org.junit.Test;

public class CharacterReaderTest {

 @Test
 public void testNextIndexOfCharNotFound() {
     CharacterReader reader = new CharacterReader("abc");
     assertEquals(-1, reader.nextIndexOf('d'));
 }

 @Test
 public void testNextIndexOfCharFound() {
     CharacterReader reader = new CharacterReader("abc");
     assertEquals(1, reader.nextIndexOf('b'));
 }

 @Test
 public void testNextIndexOfCharAtEnd() {
     CharacterReader reader = new CharacterReader("abc");
     reader.advance();
     reader.advance();
     assertEquals(0, reader.nextIndexOf('c'));
 }

 @Test
 public void testNextIndexOfCharEmptyInput() {
     CharacterReader reader = new CharacterReader("");
     assertEquals(-1, reader.nextIndexOf('a'));
 }

 @Test
 public void testNextIndexOfCharPastEOF() {
     CharacterReader reader = new CharacterReader("a");
     reader.consume();
     assertEquals(-1, reader.nextIndexOf('a'));
 }

 @Test
 public void testNextIndexOfSeqNotFound() {
     CharacterReader reader = new CharacterReader("hello");
     assertEquals(-1, reader.nextIndexOf("world"));
 }

 @Test
 public void testNextIndexOfSeqFound() {
     CharacterReader reader = new CharacterReader("hello world");
     assertEquals(6, reader.nextIndexOf("world"));
 }

 @Test
 public void testNextIndexOfSeqAtEnd() {
     CharacterReader reader = new CharacterReader("xyz");
     reader.consume();
     assertEquals(0, reader.nextIndexOf("yz"));
 }

 @Test
 public void testNextIndexOfSeqEmptyInput() {
     CharacterReader reader = new CharacterReader("");
     assertEquals(-1, reader.nextIndexOf("abc"));
 }

 @Test
 public void testNextIndexOfSeqPastEOF() {
     CharacterReader reader = new CharacterReader("a");
     reader.consume();
     assertEquals(-1, reader.nextIndexOf("a"));
 }

 @Test
 public void testNextIndexOfSeqOverflowStartMatch() {
     // Triggers ArrayIndexOutOfBoundsException in buggy version when seq exceeds remaining input
     CharacterReader reader = new CharacterReader("ab");
     try {
         int result = reader.nextIndexOf("bc");
         assertEquals(-1, result);
     } catch (ArrayIndexOutOfBoundsException e) {
         fail("nextIndexOf(seq) should not throw ArrayIndexOutOfBoundsException");
     }
 }

 @Test
 public void testConsumeToAnyBoundaries() {
     // Normal matching
     CharacterReader reader = new CharacterReader("abc,def");
     String result = reader.consumeToAny(',', ';');
     assertEquals("abc", result);
     assertEquals(',', reader.current());

     // Empty delimiter array consumes the whole string
     CharacterReader reader2 = new CharacterReader("data");
     String result2 = reader2.consumeToAny();
     assertEquals("data", result2);
     assertTrue(reader2.isEmpty());

     // No matching delimiter consumes the whole string
     CharacterReader reader3 = new CharacterReader("data");
     String result3 = reader3.consumeToAny('x', 'y', 'z');
     assertEquals("data", result3);
     assertTrue(reader3.isEmpty());
 }

@Test
    public void testConsumeLetterSequence() {
        CharacterReader reader = new CharacterReader("abc123");
        String result = reader.consumeLetterSequence();
        assertEquals("abc", result);
        assertEquals('1', reader.current());
    }

 @Test
 public void testConsumeLetterThenDigitSequence() {
     CharacterReader reader = new CharacterReader("abc123");
     String result = reader.consumeLetterThenDigitSequence();
     assertEquals("abc123", result);
     assertEquals(6, reader.pos());
 }

 @Test
 public void testConsumeHexSequence() {
     CharacterReader reader = new CharacterReader("aBc12g");
     String result = reader.consumeHexSequence();
     assertEquals("aBc12", result);
     assertEquals('g', reader.current());
 }

 @Test
 public void testConsumeDigitSequence() {
     CharacterReader reader = new CharacterReader("123abc");
     String result = reader.consumeDigitSequence();
     assertEquals("123", result);
     assertEquals('a', reader.current());
 }
}
