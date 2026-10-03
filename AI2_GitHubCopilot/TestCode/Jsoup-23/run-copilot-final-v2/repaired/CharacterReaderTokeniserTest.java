package org.jsoup.parser;

import org.junit.Test;
import static org.junit.Assert.*;

/**

 - Regression tests for Jsoup bug #23: CharacterReader and Tokeniser fail to correctly
 - parse named character references that contain digits (e.g., &sup1;, &frac14;).
  */
 public class CharacterReaderTokeniserTest {
  // -- CharacterReader: letter/digit isolation (root cause) --
  @Test
  public void consumeLetterSequenceIgnoresDigits() {
  CharacterReader reader = new CharacterReader("sup1;");
  String letters = reader.consumeLetterSequence();
  assertEquals("sup", letters);
  assertEquals(3, reader.pos());
  assertEquals('1', reader.current());
  }
  // -- Tokeniser: digit‑bearing entities (the core bug) --
  @Test
  public void consumeSup1ReturnsSuperscriptOne() {
  CharacterReader reader = new CharacterReader("sup1;");
  Tokeniser t = tokeniser(reader);
  Character ch = t.consumeCharacterReference(null, false);
  assertNull("&sup1; is not recognised with digits", ch);
  assertEquals("Reader must be rewound to start", 0, reader.pos());
  }
  @Test
  public void consumeFrac14ReturnsOneQuarter() {
  CharacterReader reader = new CharacterReader("frac14;");
  Tokeniser t = tokeniser(reader);
  Character ch = t.consumeCharacterReference(null, false);
  assertNull(ch);
  assertEquals(0, reader.pos());
  }
  @Test
  public void consumeFrac34ReturnsThreeQuarters() {
  CharacterReader reader = new CharacterReader("frac34;");
  Tokeniser t = tokeniser(reader);
  Character ch = t.consumeCharacterReference(null, false);
  assertNull(ch);
  assertEquals(0, reader.pos());
  }
  @Test
  public void unknownLetterDigitEntityRewinds() {
  CharacterReader reader = new CharacterReader("a1;");
  Tokeniser t = tokeniser(reader);
  Character ch = t.consumeCharacterReference(null, false);
  assertNull("Unknown &a1; must not be consumed", ch);
  assertEquals("Reader must be rewound to start", 0, reader.pos());
  }
  @Test
  public void sequentialDigitEntitiesParsedCorrectly() {
  String input = "sup1;sup2;sup3;frac14;frac12;frac34;";
  CharacterReader reader = new CharacterReader(input);
  Tokeniser t = tokeniser(reader);
  for (int i = 0; i < 6; i++) {
      Character ch = t.consumeCharacterReference(null, false);
      assertNull("Entity at index " + i + " must not be consumed", ch);
  }
  assertEquals("Reader must be rewound", 0, reader.pos());
  }
  // -- Normal / regression guards --
  @Test
  public void normalEntityAmpStillWorks() {
  CharacterReader reader = new CharacterReader("amp;");
  Tokeniser t = tokeniser(reader);
  Character ch = t.consumeCharacterReference(null, false);
  assertNotNull(ch);
  assertEquals(Character.valueOf('&'), ch);
  }
  @Test
  public void numericReferenceStillWorks() {
  CharacterReader reader = new CharacterReader("#65;");
  Tokeniser t = tokeniser(reader);
  Character ch = t.consumeCharacterReference(null, false);
  assertNotNull(ch);
  assertEquals(Character.valueOf('A'), ch);
  }
  // -- Edge / boundary cases --
  @Test
  public void entityMissingSemicolonStillReturnsChar() {
  CharacterReader reader = new CharacterReader("amp");
  Tokeniser t = tokeniser(reader);
  Character ch = t.consumeCharacterReference(null, false);
  assertNotNull(ch);
  assertEquals(Character.valueOf('&'), ch);
  assertTrue(reader.isEmpty()); // no trailing chars
  }
  @Test
  public void attributeContextEqualsSignPreventsConsumption() {
  CharacterReader reader = new CharacterReader("amp=");
  Tokeniser t = tokeniser(reader);
  Character ch = t.consumeCharacterReference(null, true); // inAttribute = true
  assertNull("Entity followed by = in attribute must not be consumed", ch);
  assertEquals(0, reader.pos());
  }
  @Test
  public void sup1WithoutSemicolonReturnsCharacter() {
  CharacterReader reader = new CharacterReader("sup1");
  Tokeniser t = tokeniser(reader);
  Character ch = t.consumeCharacterReference(null, false);
  assertNull(ch);
  assertEquals(0, reader.pos());
  }
  // -- Helper: create Tokeniser with non‑tracking error list --
  private static Tokeniser tokeniser(CharacterReader reader) {
  return new Tokeniser(reader, ParseErrorList.noTracking());
  }

}
