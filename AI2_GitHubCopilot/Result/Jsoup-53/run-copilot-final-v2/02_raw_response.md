package org.jsoup.parser;

import org.junit.Test;
import static org.junit.Assert.*;

/**

 - Tests for TokenQueue focusing on the bug where quoted attribute values
 - containing brackets are not correctly handled by chompBalanced.
  */
 public class TokenQueueTest {
  @Test
  public void testChompBalanced_bracketsInsideSingleQuote() {
  // closing bracket inside single-quoted attribute value
  TokenQueue tq = new TokenQueue("[data='End]']");
  String result = tq.chompBalanced('[', ']');
  assertEquals("data='End]'", result);
  assertTrue(tq.isEmpty());
  }
  @Test
  public void testChompBalanced_bracketsInsideDoubleQuote() {
  // closing bracket inside double-quoted attribute value
  TokenQueue tq = new TokenQueue("[data="End]"]");
  String result = tq.chompBalanced('[', ']');
  assertEquals("data="End]"", result);
  assertTrue(tq.isEmpty());
  }
  @Test
  public void testChompBalanced_nestedBracketsInsideQuotes() {
  // multiple brackets inside quotes should be treated as literal
  TokenQueue tq = new TokenQueue("[test='foo[bar]baz']");
  String result = tq.chompBalanced('[', ']');
  assertEquals("test='foo[bar]baz'", result);
  assertTrue(tq.isEmpty());
  }
  @Test
  public void testChompBalanced_emptyQuotedValue() {
  // empty quoted string between brackets
  TokenQueue tq = new TokenQueue("[data='']");
  String result = tq.chompBalanced('[', ']');
  assertEquals("data=''", result);
  assertTrue(tq.isEmpty());
  }
  @Test
  public void testChompBalanced_escapedBracketInQuotes() {
  // an escaped closing bracket inside quotes should not close the outer pair
  TokenQueue tq = new TokenQueue("[data='End\]']");
  String result = tq.chompBalanced('[', ']');
  assertEquals("data='End\]'", result);
  assertTrue(tq.isEmpty());
  }
  @Test
  public void testChompBalanced_escapedQuoteInsideQuotes() {
  // escaped single quote inside single quotes
  TokenQueue tq = new TokenQueue("[data='It\'s']");
  String result = tq.chompBalanced('[', ']');
  assertEquals("data='It\'s'", result);
  assertTrue(tq.isEmpty());
  }
  @Test
  public void testChompBalanced_simpleBalanced() {
  // normal balanced brackets without quotes
  TokenQueue tq = new TokenQueue("[outer[inner]outer]");
  String result = tq.chompBalanced('[', ']');
  assertEquals("outer[inner]outer", result);
  assertTrue(tq.isEmpty());
  }
  @Test
  public void testChompBalanced_adjacentBrackets() {
  // empty adjacent brackets
  TokenQueue tq = new TokenQueue("[]");
  String result = tq.chompBalanced('[', ']');
  assertEquals("", result);
  assertTrue(tq.isEmpty());
  }
  @Test
  public void testChompBalanced_unbalancedOpen() {
  // no closing bracket – should consume to end
  TokenQueue tq = new TokenQueue("[data='End");
  String result = tq.chompBalanced('[', ']');
  assertEquals("data='End", result);
  assertTrue(tq.isEmpty());
  }
  @Test
  public void testConsumeAttributeKey_basic() {
  // key ends before '='
  TokenQueue tq = new TokenQueue("data='value'] rest");
  assertEquals("data", tq.consumeAttributeKey());
  assertEquals('=', tq.consume());
  }
  @Test
  public void testConsumeToAny_multipleDelimiters() {
  // stops at first of several delimiters
  TokenQueue tq = new TokenQueue("some=thing] rest");
  assertEquals("some", tq.consumeToAny("=", "]"));
  assertEquals('=', tq.consume());
  }

}