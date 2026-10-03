package com.google.javascript.jscomp;

import junit.framework.TestCase;

/**

 - Tests for {@link CodeConsumer}.
 -
 - Focuses on the space-adding logic in {@link CodeConsumer#add(String)}
 - that was defective in issue 620 (missing space between consecutive
 - forward-slashes caused a regex character class to lose its content
 - when the generated code was interpreted as a line comment).
  */
 public class CodeConsumerTest extends TestCase {

  /** A concrete {@link CodeConsumer} that collects all output in memory. */ private static final
class TestConsumer extends CodeConsumer {
    private final StringBuilder sb = new StringBuilder();
    private char lastChar = '\0';

 @Override
 char getLastChar() {
   return lastChar;
 }

 @Override
 void append(String str) {
   if (str == null || str.isEmpty()) {
     return;
   }
   sb.append(str);
   lastChar = str.charAt(str.length() - 1);
 }

 String getOutput() {
   return sb.toString();
 }

 void reset() {
   sb.setLength(0);
   lastChar = '\0';
   statementNeedsEnded = false;
   statementStarted = false;
   sawFunction = false;
 } }

  private TestConsumer consumer;

  @Override protected void setUp() {
    consumer = new TestConsumer(); }

  /**

 - Bug 620: when two consecutive forward slashes appear (e.g. after a
 - division operator and before a regex), a space must be inserted so
 - that the output is not mistaken for a line comment.
    */
   public void testAddSlashAfterSlash() {
 consumer.add("/");
 consumer.add("/");
 // Without the fix, the output would be "//" which begins a comment.
 assertEquals("/ /", consumer.getOutput());
   }

  /**

 - A space token that follows a forward slash must be preserved,
 - otherwise a regex character class like [ ] loses its content.
    */
   public void testAddSpaceAfterSlash() {
 consumer.add("/");
 consumer.add(" ");
 consumer.add("/");
 assertEquals("/ /", consumer.getOutput());
   }

  /**

 - The exact sequence that triggers issue 620:
 - after printing an opening bracket '[', a space inside a character
 - class must not be dropped.
    */
   public void testSpaceInsideCharacterClassPreserved() {
 consumer.add("/");
 consumer.add(" ");
 consumer.add("/");
 consumer.add("[");
 consumer.add(" ");
 consumer.add("]");
 consumer.add("/");
 // Expected pattern: "/ /[ ]/ "
 assertEquals("/ /[ ]/", consumer.getOutput());
   }

  /**

 - Space token after a non-word character (like '(') must be appended
 - without suppression.
    */
   public void testAddSpaceTokenAfterNonWordChar() {
 consumer.add("(");
 consumer.add(" ");
 assertEquals("( ", consumer.getOutput());
   }

  /**

 - When both the last character and the first character of the new
 - code are word characters, a separating space is inserted.
    */
   public void testAddSeparatingSpaceBetweenWords() {
 consumer.add("foo");
 consumer.add("bar");
 assertEquals("foo bar", consumer.getOutput());
   }

  /**

 - No separating space is added when the previous character is not a
 - word character, even if the new code starts with a word character.
    */
   public void testNoSeparatingSpaceAfterNonWordChar() {
 consumer.add("[");
 consumer.add("a");
 assertEquals("[a", consumer.getOutput());
   }

  /**

 - Backslash is treated like a word character for separation purposes
 - (escape sequence continuation).
    */
   public void testAddBackslashAfterWordChar() {
 consumer.add("x");
 consumer.add("\n");
 assertEquals("x \n", consumer.getOutput());
   }

  /**

 - A space is inserted before an escape sequence, but not after a
 - backslash: adding a space there would break the escape.
    */
   public void testAddWordAfterBackslash() {
 consumer.add("x");
 // simulate the backslash as the last char of previous token
 consumer.append("\");
 consumer.lastChar = '\';
 consumer.add("w");
 assertEquals("x\w", consumer.getOutput());
   }

  /**

 - Adding an empty string does not modify the output and does not
 - change the last character.
    */
   public void testAddEmptyStringNoOp() {
 consumer.add("/");
 String before = consumer.getOutput();
 char prev = consumer.getLastChar();
 consumer.add("");
 assertEquals(before, consumer.getOutput());
 assertEquals(prev, consumer.getLastChar());
   }

  /**

 - addOp inserts a space before a binary operator if the previous
 - character is identical (e.g. + after + to avoid ++).
    */
   public void testAddOpAvoidPlusPlus() {
 consumer.add("+");
 // The last char after add("+") is '+'
 consumer.addOp("+", true);
 // A space is inserted to prevent "++"
 assertTrue(consumer.getOutput().contains(" "));
   }

  /**

 - Statement ending logic: a pending semicolon is emitted before the next
 - token is added.
    */
   public void testPendingSemicolonEmitted() {
 consumer.endStatement(true); // marks statementNeedsEnded = true
 consumer.add("foo");
 assertTrue(consumer.getOutput().startsWith(";foo"));
   }

  /**

 - Verifies that a space token at the very start of output is preserved.
    */
   public void testAddSpaceAsFirstToken() {
 consumer.add(" ");
 assertEquals(" ", consumer.getOutput());
   }
 }
