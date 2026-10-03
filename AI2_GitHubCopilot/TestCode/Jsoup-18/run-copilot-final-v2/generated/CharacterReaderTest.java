package org.jsoup.parser;

import org.junit.Test;
import static org.junit.Assert.*;

/**

 - JUnit 4.5 tests targeting the buggy version of CharacterReader (Jsoup-18b).
 - Focused on the off-by-one in consumeToEnd() and missing carriage-return
 - normalization (CR+LF → single LF, lone CR → LF).
  */
 public class CharacterReaderTest {
  @Test
  public void testConsumeToEndReturnsFullString() {
  CharacterReader reader = new CharacterReader("hello");
  assertEquals("hello", reader.consumeToEnd());
  }
  @Test
  public void testConsumeToEndAfterPartialConsume() {
  CharacterReader reader = new CharacterReader("hello");
  reader.advance(); // pos 1
  reader.advance(); // pos 2
  assertEquals("llo", reader.consumeToEnd());
  }
  @Test
  public void testConsumeToEndEmptyInput() {
  CharacterReader reader = new CharacterReader("");
  assertEquals("", reader.consumeToEnd());
  }
  @Test
  public void testConsumeToEndAtEnd() {
  CharacterReader reader = new CharacterReader("a");
  reader.consume(); // pos 1, now at end
  assertTrue(reader.isEmpty());
  assertEquals("", reader.consumeToEnd());
  }
  @Test
  public void testConsumeToEndAdvancesPosToEnd() {
  CharacterReader reader = new CharacterReader("abc");
  reader.consumeToEnd();
  assertTrue(reader.isEmpty());
  assertEquals(3, reader.pos());
  }
  @Test
  public void testConsumeToCharNotFoundFallsBackToConsumeToEnd() {
  CharacterReader reader = new CharacterReader("world");
  assertEquals("world", reader.consumeTo('x'));
  }
  @Test
  public void testConsumeToStringNotFoundFallsBackToConsumeToEnd() {
  CharacterReader reader = new CharacterReader("world");
  assertEquals("world", reader.consumeTo("xy"));
  }
  @Test
  public void testCarriageReturnPairNormalizedInConsumeToEnd() {
  CharacterReader reader = new CharacterReader("one\r\ntwo");
  assertEquals("one\ntwo", reader.consumeToEnd());
  }
  @Test
  public void testCarriageReturnSingleNormalizedInConsumeToEnd() {
  CharacterReader reader = new CharacterReader("a\rb");
  assertEquals("a\nb", reader.consumeToEnd());
  }
  @Test
  public void testCarriageReturnAtEndNormalizedInConsumeToEnd() {
  CharacterReader reader = new CharacterReader("abc\r");
  assertEquals("abc\n", reader.consumeToEnd());
  }
  @Test
  public void testCarriageReturnNormalizationViaConsumeSequence() {
  CharacterReader reader = new CharacterReader("a\r\nb");
  assertEquals('a', reader.consume());
  assertEquals('\n', reader.consume()); // CRLF → single LF
  assertEquals('b', reader.consume());
  assertEquals(CharacterReader.EOF, reader.consume());
  }
  @Test
  public void testToStringNormalized() {
  CharacterReader reader = new CharacterReader("x\r\ny");
  assertEquals("x\ny", reader.toString());
  }

}
